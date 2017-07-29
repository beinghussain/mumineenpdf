package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.itextpdf.text.pdf.PdfReader;
import com.marcinorlowski.fonty.Fonty;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity_;
import com.mumineendownloads.mumineenpdf.Fragments.SearchFragment;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.MyViewHolder>  {

    ArrayList<PDF.PdfBean> downloadRequest = new ArrayList<>();

    private Context context;
    private SearchFragment pdfListFragment;
    private ArrayList<PDF.PdfBean> pdfBeanArrayList;
    private PDFHelper pdfHelper;
    private int size = 0;

    private String getPagesString(int filePages) {
        if(filePages==0){
            return "";
        }
        if(filePages>1){
            return filePages + " pages • ";
        }
        return filePages + " page • ";
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircularProgressBar progressBarDownload;
        ImageView imageView;
        public TextView title;
        public TextView size;
        RelativeLayout mainView,cancelView;
        LinearLayout parentView;
        public ProgressView loading;
        Button button;
        ImageButton cancel;
        public ImageView audio;
        public TextView album;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            album = (TextView) view.findViewById(R.id.album);
            size = (TextView) view.findViewById(R.id.size);
            mainView = (RelativeLayout) view.findViewById(R.id.mainView);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            button = (Button) view.findViewById(R.id.openButton);
            progressBarDownload = (CircularProgressBar) view.findViewById(R.id.spv);
            loading = (ProgressView) view.findViewById(R.id.loading);
            cancel = (ImageButton) view.findViewById(R.id.cancelButton);
            cancelView = (RelativeLayout) view.findViewById(R.id.cancel);
            parentView = (LinearLayout) view.findViewById(R.id.parentView);
        }
    }

    public void filter(ArrayList<PDF.PdfBean>newList) {
        pdfBeanArrayList=new ArrayList<>();
        pdfBeanArrayList.addAll(newList);
        notifyDataSetChanged();
    }

    public PDFAdapter(ArrayList<PDF.PdfBean> pdfList, Context applicationContext, SearchFragment pdfListFragment) {
        pdfHelper = new PDFHelper(applicationContext);
        this.pdfBeanArrayList = pdfList;
        this.context = applicationContext;
        this.pdfListFragment = pdfListFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_item_search, parent, false);

        Fonty.setFonts((ViewGroup) itemView);


        return new MyViewHolder(itemView);
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

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PDF.PdfBean pdf = pdfBeanArrayList.get(position);
        holder.title.setText(pdf.getTitle());

        String al = "";
        final int pdfDownloadStatus = pdf.getStatus();
        if(pdf.getCat()!=pdf.getAlbum()) {
            holder.album.setText(pdf.getAlbum() + " > " + pdf.getCat());
        }else {
            holder.album.setText(pdf.getAlbum());
        }
        if (pdfDownloadStatus == Status.STATUS_LOADING) {
            holder.imageView.setVisibility(View.GONE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.button.setVisibility(View.GONE);
            holder.size.setText("Connecting..");
            holder.cancel.setVisibility(View.VISIBLE);
            holder.cancelView.setVisibility(View.VISIBLE);
            holder.loading.setVisibility(View.VISIBLE);
        } else if(pdfDownloadStatus == PDF.STATUS_QUEUED) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.button.setVisibility(View.GONE);
            holder.size.setText("Queued..");
            holder.cancel.setVisibility(View.GONE);
            holder.cancelView.setVisibility(View.GONE);
        }else if (pdfDownloadStatus == Status.STATUS_DOWNLOADING) {
            holder.imageView.setVisibility(View.GONE);
            holder.progressBarDownload.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
            holder.loading.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.cancelView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setProgress(pdf.getProgress());
            holder.size.setText(pdf.getDownloadPerSize());
        } else if (pdfDownloadStatus == Status.STATUS_DOWNLOADED) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.size.setText(getPagesString(pdf.getPageCount()) + Utils.fileSize(pdf.getSize())) ;
            holder.button.setVisibility(View.VISIBLE);
            if(pdfListFragment.isMultiSelect){
                holder.button.setAlpha(0.5f);
                holder.button.setEnabled(false);
                holder.cancelView.setEnabled(false);
            }else{
                holder.button.setAlpha(1.0f);
                holder.button.setEnabled(true);
                holder.cancelView.setEnabled(false);
            }
            holder.loading.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.GONE);
            holder.cancelView.setVisibility(View.VISIBLE);
        }else if(pdfDownloadStatus==Status.STATUS_CONNECTED){
            holder.size.setText("Downloading..");
            holder.loading.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.cancelView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.VISIBLE);
        }
        else {
            holder.cancelView.setVisibility(View.GONE);
            holder.size.setText(Utils.fileSize(pdf.getSize()) + al);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.button.setVisibility(View.GONE);
            holder.loading.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.GONE);
        }

        if(pdf.getAudio()!=1) {
            holder.imageView.setImageResource(R.drawable.pdf_downloaded);
        }else {
            holder.imageView.setImageResource(R.drawable.pdf_downloaded_audio);
        }

        holder.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfDownloadStatus==Status.STATUS_DOWNLOADED) {
                    if (getFilePages(pdf) != 0) {
                        Intent intent = new Intent(pdfListFragment.getActivity(), PDFActivity.class);
                        intent.putExtra("mode", 0);
                        intent.putExtra("pid", pdf.getPid());
                        intent.putExtra("title", pdf.getTitle());
                        pdfListFragment.startActivity(intent);
                    } else {
                        Toasty.error(context, "Invalid file").show();
                        pdf.setStatus(Status.STATUS_NULL);
                        notifyDataSetChanged();
                        pdfHelper.updatePDF(pdf);
                    }
                } else {
                    DownloadManager.getInstance().cancel(String.valueOf(pdf.getPid()));
                }
            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFilePages(pdf)!=0) {
                    Intent intent = new Intent(pdfListFragment.getActivity(), PDFActivity_.class);
                    intent.putExtra("mode",0);
                    intent.putExtra("pid", pdf.getPid());
                    intent.putExtra("title", pdf.getTitle());
                    pdfListFragment.startActivity(intent);
                } else {
                    Toasty.error(context,"Invalid file").show();
                    pdf.setStatus(Status.STATUS_NULL);
                    notifyDataSetChanged();
                    pdfHelper.updatePDF(pdf);
                }
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.getInstance().cancel(String.valueOf(pdf.getPid()));
            }
        });


        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfListFragment.openDialog(holder.parentView.getContext(),position,pdf);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfBeanArrayList.size();
    }
}