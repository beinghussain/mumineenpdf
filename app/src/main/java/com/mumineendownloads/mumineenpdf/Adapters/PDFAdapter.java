package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
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

    private Context context;
    private PDFListFragment pdfListFragment;
    private ArrayList<PDF.PdfBean> pdfBeanArrayList;
    private PDFHelper pdfHelper;


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
        RelativeLayout mainView;
        ProgressView loading;
        Button button;
        ImageButton cancel;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            size = (TextView) view.findViewById(R.id.size);
            mainView = (RelativeLayout) view.findViewById(R.id.mainView);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            button = (Button) view.findViewById(R.id.openButton);
            progressBarDownload = (CircularProgressBar) view.findViewById(R.id.spv);
            loading = (ProgressView) view.findViewById(R.id.loading);
            cancel = (ImageButton) view.findViewById(R.id.cancelButton);
        }
    }

    public void filter(ArrayList<PDF.PdfBean>newList)
    {
        pdfBeanArrayList=new ArrayList<>();
        pdfBeanArrayList.addAll(newList);
        notifyDataSetChanged();
    }


    public PDFAdapter(ArrayList<PDF.PdfBean> pdfList, Context applicationContext, PDFListFragment pdfListFragment) {
        pdfHelper = new PDFHelper(applicationContext);
        this.pdfBeanArrayList = pdfList;
        this.context = applicationContext;
        this.pdfListFragment = pdfListFragment;
    }


    public void startDownload(final PDF.PdfBean pdf, int position) {
        if(position==-1){
            position = pdfBeanArrayList.indexOf(pdf);
        }
        File mDownloadDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File mFile = new File(mDownloadDir + "/Mumineen/");
        final DownloadRequest request = new DownloadRequest.Builder()
                .setName(pdf.getPid() + ".pdf")
                .setUri("http://mumineendownloads.com/downloadFile.php?file="+pdf.getSource())
                .setFolder(mFile)
                .build();


        final int finalPosition = position;
        DownloadManager.getInstance().download(request, "http://mumineendownloads.com/downloadFile.php?file="+pdf.getSource(), new CallBack() {
            @Override
            public void onStarted() {
                pdf.setStatus(Status.STATUS_DOWNLOADING);
                notifyItemChanged(finalPosition);
                pdfHelper.updatePDF(pdf);
            }

            @Override
            public void onConnecting() {
                pdf.setStatus(Status.STATUS_LOADING);
                notifyItemChanged(finalPosition);
            }

            @Override
            public void onConnected(long total, boolean isRangeSupport) {
                pdf.setStatus(Status.STATUS_DOWNLOADING);
                notifyItemChanged(finalPosition);
                pdfHelper.updatePDF(pdf);
            }

            @Override
            public void onProgress(long finished, long total, final int progress) {
                pdfListFragment.updateProgressBar(progress, finalPosition, finished, total);
            }

            @Override
            public void onCompleted() {
                notifyItemChanged(finalPosition);
                pdf.setStatus(Status.STATUS_DOWNLOADED);
                pdf.setPageCount(getFilePages(pdf));
                pdfHelper.updatePDF(pdf);
            }


            @Override
            public void onDownloadPaused() {
                pdf.setStatus(Status.STATUS_PAUSED);
                notifyItemChanged(finalPosition);
                pdfHelper.updatePDF(pdf);
            }

            @Override
            public void onDownloadCanceled() {
                pdf.setStatus(Status.STATUS_NULL);
                notifyItemChanged(finalPosition);
                pdfHelper.updatePDF(pdf);
            }

            @Override
            public void onFailed(DownloadException e) {
                pdf.setStatus(Status.STATUS_NULL);
                Toasty.error(context,"Download Failed!",500).show();
                notifyItemChanged(finalPosition);
            }
        });
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_item, parent, false);

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
        final String t;
        if (Integer.parseInt(pdf.getSize()) < 1024) {
            t = pdf.getSize() + " KB";
        } else {
            Float size = Float.valueOf(pdf.getSize()) / 1024;
            t = new DecimalFormat("##.##").format(size) + " MB";
        }
        String al = "";


        holder.size.setText(t + al);
            int pdfDownloadStatus = pdf.getStatus();
            if (pdfDownloadStatus == Status.STATUS_LOADING) {
                holder.imageView.setVisibility(View.GONE);
                holder.progressBarDownload.setVisibility(View.GONE);
                holder.button.setVisibility(View.GONE);
                holder.size.setText("Connecting..");
                holder.cancel.setVisibility(View.VISIBLE);
                holder.loading.setVisibility(View.VISIBLE);
            } else if (pdfDownloadStatus == Status.STATUS_DOWNLOADING) {
                holder.imageView.setVisibility(View.GONE);
                holder.progressBarDownload.setVisibility(View.VISIBLE);
                holder.button.setVisibility(View.GONE);
                holder.loading.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.VISIBLE);
            } else if (pdfDownloadStatus == Status.STATUS_DOWNLOADED) {
                if(pdf.getPageCount()==0) {
                    pdf.setPageCount(getFilePages(pdf));
                }
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageView.setImageResource(R.drawable.pdf_downloaded);
                holder.progressBarDownload.setVisibility(View.GONE);
                holder.size.setText(getPagesString(pdf.getPageCount()) + t) ;
                holder.button.setVisibility(View.VISIBLE);
                if(pdfListFragment.isMultiSelect){
                    holder.button.setAlpha(0.2f);
                    holder.button.setEnabled(false);
                }else{
                    holder.button.setAlpha(1.0f);
                    holder.button.setEnabled(true);
                }
                holder.loading.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
        }
        else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.pdf);
            holder.button.setVisibility(View.GONE);
            holder.loading.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.GONE);
        }

        if(pdfListFragment.getMultiSelect_list().contains(pdf)){
            holder.mainView.setBackgroundColor(Color.parseColor("#F3F4F3"));
        }else {
            holder.mainView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFilePages(pdf)!=0) {
                    Intent intent = new Intent(pdfListFragment.getActivity(), PDFActivity.class);
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
                DownloadManager.getInstance().cancel("http://mumineendownloads.com/downloadFile.php?file="+pdf.getSource());
            }
        });

        holder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pdfListFragment.enableMultiSelect(position, pdf);
                return true;
            }
        });


        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfListFragment.openDialog(holder.mainView.getContext(),position,pdf);
            }
        });
    }

    public void viewOnline(PDF.PdfBean pdf, int adapterPosition, MyViewHolder holder) {
        Intent intent = new Intent(pdfListFragment.getActivity(), PDFActivity.class);
        intent.putExtra("mode",1);
        intent.putExtra("pid", pdf.getPid());
        intent.putExtra("url",pdf.getSource());
        intent.putExtra("title",pdf.getTitle());
        pdfListFragment.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return pdfBeanArrayList.size();
    }
}
