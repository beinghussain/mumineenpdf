package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.aspsine.multithreaddownload.DownloadManager;
import com.itextpdf.text.pdf.PdfReader;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Fragments.Saved;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;




public class SavedPDFAdapter extends BaseSavedAdapter{

    private final Context context;
    private ArrayList<PDF.PdfBean> pdfBeanArrayList;
    private Saved pdfListFragment;
    private PDFHelper pdfHelper;

    public SavedPDFAdapter(ArrayList<PDF.PdfBean> itemList, Context context, Saved pdfListFragment) {
        super(itemList);
        pdfHelper = new PDFHelper(context);
        this.pdfBeanArrayList = itemList;

        this.context = context;
        this.pdfListFragment = pdfListFragment;
    }

    @Override
    public boolean onPlaceSubheaderBetweenItems(int position) {
        final PDF.PdfBean pdf = pdfBeanArrayList.get(position);
        final PDF.PdfBean nextPdf = pdfBeanArrayList.get(position + 1);
        return !pdf.getCat().equals(nextPdf.getCat());
    }


    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder1, final int position) {
        PDF.PdfBean p = pdfBeanArrayList.get(position);
        if(p.getPid()!=-5) {
            final PDFViewHolder holder = ((PDFViewHolder) holder1);
            final PDF.PdfBean pdf = pdfBeanArrayList.get(position);
            String output = pdf.getTitle().substring(0, 1).toUpperCase() + pdf.getTitle().substring(1).toLowerCase();
            holder.title.setText(output);
            String al = "";
            if(pdf.getAudio()!=1) {
                holder.imageView.setImageResource(R.drawable.pdf_downloaded);
            }else {
                holder.imageView.setImageResource(R.drawable.pdf_downloaded_audio);
            }
            final int pdfDownloadStatus = pdf.getStatus();
            if (pdfDownloadStatus == Status.STATUS_LOADING) {
                holder.imageView.setVisibility(View.GONE);
                holder.progressBarDownload.setVisibility(View.GONE);
                holder.size.setText("Connecting..");
                holder.loading.setVisibility(View.VISIBLE);
            } else if (pdfDownloadStatus == PDF.STATUS_QUEUED) {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.progressBarDownload.setVisibility(View.GONE);
                holder.size.setText("Queued..");
            } else if (pdfDownloadStatus == Status.STATUS_DOWNLOADING) {
                holder.imageView.setVisibility(View.GONE);
                holder.progressBarDownload.setVisibility(View.VISIBLE);
                holder.loading.setVisibility(View.GONE);
                holder.progressBarDownload.setProgress(pdf.getProgress());
                holder.size.setText(pdf.getDownloadPerSize());
            } else if (pdfDownloadStatus == Status.STATUS_DOWNLOADED) {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.progressBarDownload.setVisibility(View.GONE);
                holder.size.setText(getPagesString(pdf.getPageCount()) + Utils.fileSize(pdf.getSize()));
                holder.loading.setVisibility(View.GONE);
            } else if (pdfDownloadStatus == Status.STATUS_CONNECTED) {
                holder.size.setText("Downloading..");
                holder.loading.setVisibility(View.INVISIBLE);
                holder.imageView.setVisibility(View.INVISIBLE);
                holder.progressBarDownload.setVisibility(View.VISIBLE);
            } else {
                holder.size.setText(getPagesString(pdf.getPageCount()) + Utils.fileSize(pdf.getSize()));
                holder.imageView.setVisibility(View.VISIBLE);
                holder.progressBarDownload.setVisibility(View.GONE);
                holder.loading.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
            }

            holder.cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  pdfListFragment.openDialog(pdf,position);
                }
            });

            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pdfListFragment.openDialog(pdf,position);
                }
            });

            holder.parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPDF(pdf);
                }
            });
        }

    }

    private void playAudio(PDF.PdfBean pdf) {

    }

    private void openPDF(PDF.PdfBean pdf) {
        Intent intent = new Intent(pdfListFragment.getActivity(), PDFActivity.class);
        intent.putExtra("mode", 0);
        intent.putExtra("pid", pdf.getPid());
        intent.putExtra("title", pdf.getTitle());
        pdfListFragment.startActivity(intent);
    }

    @Override
    public void onBindSubheaderViewHolder(SubHeaderHolder subheaderHolder, int nextItemPosition) {
        final PDF.PdfBean nextPDF = pdfBeanArrayList.get(nextItemPosition);
        int count = 0;
        for(PDF.PdfBean p : pdfBeanArrayList){
            if(nextPDF.getCat().equals(p.getCat())){
                count++;
            }
        }
        String unit = "items";
        if(count==1){
            unit="item";
        }
        if(nextPDF.getAlbum().equals(nextPDF.getCat())){
            subheaderHolder.mSubHeaderText.setText(nextPDF.getCat());
            subheaderHolder.itemCount.setText(count  + " " +  unit);
        } else {
            subheaderHolder.mSubHeaderText.setText(nextPDF.getAlbum() + " | " + nextPDF.getCat());
            subheaderHolder.itemCount.setText(count +" "+ unit);
        }
    }

    private String getPagesString(int filePages) {
        if(filePages==0){
            return "";
        }
        if(filePages>1){
            return filePages + " pages • ";
        }
        return filePages + " page • ";
    }

    public void filter(ArrayList<PDF.PdfBean>newList) {
        pdfBeanArrayList = newList;
        notifyDataSetChanged();
    }

    private int getFilePages(PDF.PdfBean pdf){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+pdf.getPid() + ".pdf");
        int count;
        try {
            PdfReader pdfReader = new PdfReader(String.valueOf(file));
            count = pdfReader.getNumberOfPages();
            return count;
        } catch (IOException ignored) {
            return 0;
        } catch (NoClassDefFoundError ignored){
            return 0;
        }
    }

}
