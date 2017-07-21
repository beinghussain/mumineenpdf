package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.multithreaddownload.DownloadManager;
import com.itextpdf.text.pdf.PdfReader;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Fragments.Go;
import com.mumineendownloads.mumineenpdf.Fragments.LibraryFragment;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.Library;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.mumineendownloads.mumineenpdf.R.id.sectionHeader;

/**
 * Created by Hussain on 7/8/2017.
 */


public class LibraryAdapter extends BaseLibraryAdapter {

    private final Context context;
    private ArrayList<PDF.PdfBean> pdfBeanArrayList;
    private LibraryFragment pdfListFragment;
    private PDFHelper pdfHelper;

    public LibraryAdapter(ArrayList<PDF.PdfBean> itemList, Context context, LibraryFragment pdfListFragment) {
        super(itemList);
        pdfHelper = new PDFHelper(context);
        this.pdfBeanArrayList = itemList;
        this.context = context;
        this.pdfListFragment = pdfListFragment;
        Log.e("SubHeader","Coming here");

    }

    @Override
    public boolean onPlaceSubheaderBetweenItems(int position) {

        final PDF.PdfBean pdf = pdfBeanArrayList.get(position);
        final PDF.PdfBean nextPdf = pdfBeanArrayList.get(position + 1);
        return !pdf.getGo().equals(nextPdf.getGo());
    }

    @Override
    public void onBindItemViewHolder(final PDFViewHolder holder, final int position) {
        final PDF.PdfBean child = pdfBeanArrayList.get(position);
        String output = child.getTitle().substring(0, 1).toUpperCase() + child.getTitle().substring(1).toLowerCase();
        holder.title.setText(output);
        String al = "";
        final int pdfDownloadStatus = child.getStatus();
        if (pdfDownloadStatus == Status.STATUS_LOADING) {
            holder.imageView.setVisibility(View.GONE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.button.setVisibility(View.GONE);
            holder.size.setText("Connecting..");
            holder.cancel.setVisibility(View.VISIBLE);
            holder.cancelView.setVisibility(View.VISIBLE);
            holder.loading.setVisibility(View.VISIBLE);
        } else if (pdfDownloadStatus == PDF.STATUS_QUEUED) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.button.setVisibility(View.GONE);
            holder.size.setText("Queued..");
            holder.cancel.setVisibility(View.GONE);
            holder.cancelView.setVisibility(View.GONE);
        } else if (pdfDownloadStatus == Status.STATUS_DOWNLOADING) {
            holder.imageView.setVisibility(View.GONE);
            holder.progressBarDownload.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
            holder.loading.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.cancelView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setProgress(child.getProgress());
            holder.size.setText(child.getDownloadPerSize());
        } else if (pdfDownloadStatus == Status.STATUS_DOWNLOADED) {
            holder.cancelView.setVisibility(View.GONE);
            holder.size.setText(getPagesString(child.getPageCount()) + Utils.fileSize(child.getSize()));
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.button.setVisibility(View.GONE);
            holder.loading.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.GONE);
        } else if (pdfDownloadStatus == Status.STATUS_CONNECTED) {
            holder.size.setText("Downloading..");
            holder.loading.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.cancelView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.size.setText(getPagesString(child.getPageCount()) + Utils.fileSize(child.getSize()));
            holder.button.setVisibility(View.VISIBLE);
            holder.loading.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.GONE);
            holder.cancelView.setVisibility(View.VISIBLE);
        }

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.getInstance().cancel(String.valueOf(child.getPid()));
            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<PDF.PdfBean> arrayList = new ArrayList<PDF.PdfBean>();
                ArrayList<Integer> positionList = new ArrayList<Integer>();
                arrayList.add(child);
                positionList.add(position);
                DownloadService.intentDownload(positionList,arrayList,context);
                child.setStatus(PDF.STATUS_QUEUED);
            }
        });


        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF(child, position);
            }
        });



    }

    private void openPDF(final PDF.PdfBean pdf, final int position) {
        File f = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/Mumineen/"+pdf.getPid()+".pdf");
        if(f.exists()) {
            Intent intent = new Intent(context, PDFActivity.class);
            intent.putExtra("mode", 0);
            intent.putExtra("pid", pdf.getPid());
            intent.putExtra("title", pdf.getTitle());
            context.startActivity(intent);
        }else {
            new MaterialDialog.Builder(context).title("File not downloaded").content("Do you want to download the file")
                    .positiveText("Download")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            ArrayList<PDF.PdfBean> arrayList = new ArrayList<PDF.PdfBean>();
                            ArrayList<Integer> positionList = new ArrayList<Integer>();
                            arrayList.add(pdf);
                            positionList.add(position);
                            DownloadService.intentDownload(positionList,arrayList,context);
                        }
                    }).build().show();
        }
    }

    @Override
    public void onBindSubheaderViewHolder(SubHeaderHolder subheaderHolder, int nextItemPosition) {
        final PDF.PdfBean nextPDF = pdfBeanArrayList.get(nextItemPosition);
        subheaderHolder.mSubHeaderText.setText(nextPDF.getGo());
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
        notifyDataChanged();
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
