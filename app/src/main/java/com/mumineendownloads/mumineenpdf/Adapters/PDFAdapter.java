package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.itextpdf.text.pdf.PdfReader;
import com.marcinorlowski.fonty.Fonty;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Constants;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.Model.PDFDownload;
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
    private PDF.PdfBean pdf;
    private PDFDownload pdfDownload = new PDFDownload();
    private Utils utils;


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
        TextView title;
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
        this.pdfBeanArrayList = pdfList;
        this.context = applicationContext;
        this.pdfListFragment = pdfListFragment;
    }


    private void startDownload(final PDF.PdfBean pdf, final int position, final PDFAdapter.MyViewHolder holder) {
        File mDownloadDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File mFile = new File(mDownloadDir + "/Mumineen/");
        final DownloadRequest request = new DownloadRequest.Builder()
                .setName(pdf.getTitle() + ".pdf")
                .setUri("http://mumineendownloads.com/downloadFile.php?file="+pdf.getSource())
                .setFolder(mFile)
                .build();


        DownloadManager.getInstance().download(request, "http://mumineendownloads.com/downloadFile.php?file="+pdf.getSource(), new CallBack() {
            @Override
            public void onStarted() {
                pdf.setStatus(Constants.STATUS_DOWNLOADING);
                notifyItemChanged(position);
                pdfDownload.setStatus(Constants.STATUS_DOWNLOADING);
                pdfDownload.setPid(pdf.getPid());

            }

            @Override
            public void onConnecting() {
                pdf.setStatus(Constants.STATUS_LOADING);
                notifyItemChanged(position);
                pdfDownload.setPid(Constants.STATUS_DOWNLOADING);
            }

            @Override
            public void onConnected(long total, boolean isRangeSupport) {
                pdf.setStatus(Constants.STATUS_DOWNLOADING);
                notifyItemChanged(position);
            }

            @Override
            public void onProgress(long finished, long total, final int progress) {
                pdfListFragment.updateProgressBar(progress, position, finished, total);
            }

            @Override
            public void onCompleted() {
                notifyItemChanged(position);
                PDFHelper pdfHelper = new PDFHelper(context);
                pdf.setStatus(Constants.STATUS_DOWNLOADED);
                pdf.setPageCount(getFilePages(pdf));
                pdfHelper.updatePDF(pdf);
            }


            @Override
            public void onDownloadPaused() {
                pdf.setStatus(Constants.STATUS_PAUSED);
                notifyItemChanged(position);
            }

            @Override
            public void onDownloadCanceled() {
                pdf.setStatus(Constants.STATUS_NULL);
                notifyItemChanged(position);
            }

            @Override
            public void onFailed(DownloadException e) {
                pdf.setStatus(Constants.STATUS_NULL);
                Toasty.error(context,"Download Failed!",500).show();
                notifyItemChanged(position);
            }
        });
    }




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_item, parent, false);

        utils = new Utils(context);


        Fonty.setFonts((ViewGroup) itemView);


        return new MyViewHolder(itemView);
    }

    private int getFilePages(PDF.PdfBean pdf){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+pdf.getTitle() + ".pdf");
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        pdf = pdfBeanArrayList.get(position);
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

        if(utils.CheckIfExists(pdf.getPid())) {
            int pid = pdf.getPid();
            PDFDownload pdfDownload = utils.getStatus(pid);
            if (pdfDownload.getPid() == Constants.STATUS_LOADING) {
                holder.imageView.setVisibility(View.GONE);
                holder.progressBarDownload.setVisibility(View.GONE);
                holder.button.setVisibility(View.GONE);
                holder.size.setText("Connecting..");
                holder.cancel.setVisibility(View.VISIBLE);
                holder.loading.setVisibility(View.VISIBLE);
            } else if (pdfDownload.getStatus() == Constants.STATUS_DOWNLOADING) {
                holder.imageView.setVisibility(View.GONE);
                holder.progressBarDownload.setVisibility(View.VISIBLE);
                holder.button.setVisibility(View.GONE);
                holder.loading.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.VISIBLE);
            } else if (pdfDownload.getStatus() == Constants.STATUS_DOWNLOADED) {
                if(pdf.getPageCount()==0) {
                    pdf.setPageCount(getFilePages(pdf));
                }
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageView.setImageResource(R.drawable.pdf_downloaded);
                holder.progressBarDownload.setVisibility(View.GONE);
                holder.size.setText(getPagesString(pdf.getPageCount()) + t) ;
                holder.button.setVisibility(View.VISIBLE);
                holder.loading.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.GONE);
            }
        }

        else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.pdf);
            holder.button.setVisibility(View.GONE);
            holder.loading.setVisibility(View.GONE);
            holder.cancel.setVisibility(View.GONE);
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pdfListFragment.getActivity(), PDFActivity.class);
                intent.putExtra("title", pdf.getTitle());
                pdfListFragment.startActivity(intent);
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.getInstance().cancel("http://mumineendownloads.com/downloadFile.php?file="+pdf.getSource());
            }
        });


        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFDownload pdfDownload = utils.getStatus(pdf.getPid());
                int array = R.array.preference_values;

                if(utils.CheckIfExists(pdf.getPid())) {
                    if (pdfDownload.getStatus() == Constants.STATUS_DOWNLOADED) {
                        array = R.array.preference_values_downloaded;
                    }
                }
                new MaterialDialog.Builder(holder.mainView.getContext())
                        .items(array)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (text.equals("Download")) {
                                    if(Utils.isConnected(context)) {
                                        startDownload(pdf, holder.getAdapterPosition(), holder);
                                    } else {
                                        Toasty.error(context, "Internet connection not found!", Toast.LENGTH_SHORT, true).show();
                                    }
                                } else if (text.equals("View Online")) {
                                    Toast.makeText(context, "Viewing online..", Toast.LENGTH_SHORT).show();
                                } else if (text.equals("Share")) {
                                    Toast.makeText(context, "Sharing..", Toast.LENGTH_SHORT).show();
                                } else if (text.equals("Report")) {
                                    Toast.makeText(context, "Reporting...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return pdfBeanArrayList.size();
    }
}
