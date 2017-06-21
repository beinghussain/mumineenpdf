package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.pdf.PdfReader;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Constants;
import com.mumineendownloads.mumineenpdf.Fragments.PDFDialogFragment;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.MyViewHolder>  {

    private ImageView ticked;
    private Context context;
    private PDFListFragment pdfListFragment;
    private ArrayList<PDF.PdfBean> mFilteredList;
    private ArrayList<PDF.PdfBean> pdfBeanArrayList;
    private MyViewHolder holder;
    private final Handler myHandler = new Handler();
    private String pagesString;


    public void removeFromDownload(int pid) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        ArrayList<Integer> arrayList = getDownloadIds();
        if(arrayList==null){
            arrayList = new ArrayList<Integer>();
        }
        arrayList.remove(arrayList.indexOf(pid));
        String json = gson.toJson(arrayList);
        editor.putString(TAG, json);
        editor.apply();
    }

    public String getPagesString(int filePages) {
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

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            size = (TextView) view.findViewById(R.id.size);
            mainView = (RelativeLayout) view.findViewById(R.id.mainView);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            button = (Button) view.findViewById(R.id.openButton);
            progressBarDownload = (CircularProgressBar) view.findViewById(R.id.spv);
            loading = (ProgressView) view.findViewById(R.id.loading);
        }
    }


    private ArrayList<Integer> getDownloadIds(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(TAG, null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        return gson.fromJson(json, type);
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
        mFilteredList = pdfList;
        this.pdfListFragment = pdfListFragment;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public int getFilePages(PDF.PdfBean pdf){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+pdf.getTitle() + ".pdf");
        int count = 0;
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
        this.holder = holder;
        final PDF.PdfBean pdf = pdfBeanArrayList.get(position);
        holder.title.setText(pdf.getTitle());
        String t;
        if (Integer.parseInt(pdf.getSize()) < 1024) {
            t = pdf.getSize() + " KB";
        } else {
            Float size = Float.valueOf(pdf.getSize()) / 1024;
            t = new DecimalFormat("##.##").format(size) + " MB";
        }
        holder.size.setText(t);

        if (pdf.getStatus() == Constants.STATUS_LOADING) {
            holder.imageView.setVisibility(View.GONE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.button.setVisibility(View.GONE);
            holder.size.setText("Downloading");
            holder.loading.setVisibility(View.VISIBLE);
        } else if (pdf.getStatus() == Constants.STATUS_DOWNLOADING) {
            holder.imageView.setVisibility(View.GONE);
            holder.progressBarDownload.setVisibility(View.VISIBLE);
            holder.button.setVisibility(View.GONE);
            holder.loading.setVisibility(View.GONE);
        } else if (pdf.getStatus() == Constants.STATUS_DOWNLOADED) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(R.drawable.pdf_downloaded);
            holder.progressBarDownload.setVisibility(View.GONE);
            String pages = getPagesString(getFilePages(pdf));
            holder.size.setText(pages + t) ;
            holder.button.setVisibility(View.VISIBLE);
            holder.loading.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.drawable.pdf);
            holder.button.setVisibility(View.GONE);
            holder.loading.setVisibility(View.GONE);
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pdfListFragment.getActivity(), PDFActivity.class);
                intent.putExtra("title", pdf.getTitle());
                pdfListFragment.startActivity(intent);
            }
        });


        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int array = R.array.preference_values;
                if (pdf.getStatus() == Constants.STATUS_DOWNLOADED) {
                    array = R.array.preference_values_downloaded;
                }
                new MaterialDialog.Builder(holder.mainView.getContext())
                        .items(array)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (text.equals("Download")) {
                                    startDownload(pdf, position, holder);
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

    public void startDownload(final PDF.PdfBean pdf, final int position, final MyViewHolder holder) {
        File mDownloadDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File mFile = new File(mDownloadDir + "/Mumineen/");
        final DownloadRequest request = new DownloadRequest.Builder()
                .setName(pdf.getTitle() + ".pdf")
                .setUri("http://mumineendownloads.com/downloadFile.php?file="+pdf.getSource())
                .setFolder(mFile)
                .build();
        com.aspsine.multithreaddownload.DownloadManager.getInstance().download(request, pdf.getTitle(), new CallBack() {
            @Override
            public void onStarted() {
                pdf.setStatus(Constants.STATUS_DOWNLOADING);
                notifyItemChanged(position);
            }

            @Override
            public void onConnecting() {
                pdf.setStatus(Constants.STATUS_LOADING);
                notifyItemChanged(position);
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
                notifyDataSetChanged();
                PDFHelper pdfHelper = new PDFHelper(context);
                pdf.setStatus(Constants.STATUS_DOWNLOADED);
                pdfHelper.updatePDF(pdf);
            }


            @Override
            public void onDownloadPaused() {

            }

            @Override
            public void onDownloadCanceled() {

            }

            @Override
            public void onFailed(DownloadException e) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfBeanArrayList.size();
    }



}
