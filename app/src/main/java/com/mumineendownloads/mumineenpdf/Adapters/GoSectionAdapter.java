package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Fragments.Go;
import com.mumineendownloads.mumineenpdf.Helpers.SectionHeader;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;
import com.mumineendownloads.mumineenpdf.ViewHolder.ChildViewHolder;
import com.mumineendownloads.mumineenpdf.ViewHolder.SectionViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GoSectionAdapter extends SectionRecyclerViewAdapter<SectionHeader, PDF.PdfBean, SectionViewHolder, ChildViewHolder> {

    private Context context;
    private int download_left = 0;
    private String sectionHeader;
    public static MaterialDialog dialog;

    public GoSectionAdapter(Context context, List<SectionHeader> sectionHeaderItemList) {
        super(context, sectionHeaderItemList);
        this.context = context;
    }

    @Override
    public SectionViewHolder onCreateSectionViewHolder(ViewGroup sectionViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_header, sectionViewGroup, false);
        return new SectionViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.go_pdf_item, childViewGroup, false);
        Fonty.setFonts((ViewGroup) view);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, final int sectionPosition, final SectionHeader sectionHeader)
    {
        String title = sectionHeader.getSectionText();
        String output = title.substring(0,1).toUpperCase() + title.substring(1).toLowerCase();
        sectionViewHolder.name.setText(output);
        download_left = Utils.getPDFNotDownloadedCount(context,sectionHeader.getSectionText());
        this.sectionHeader = sectionHeader.getSectionText();
        if(download_left!=0){
            sectionViewHolder.downloadLeft.setText(download_left + " files not downloaded");
        }
        sectionViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
           showAlert(context,sectionPosition,sectionHeader.getSectionText());
            }
        });
    }

    private void showAlert(final Context context, final int sectionPosition, final String sectionName){
        new MaterialDialog.Builder(context)
                .title("Delete "+sectionName)
                .positiveText("Delete")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteSection(context,sectionName,sectionPosition);
                    }
                })
                .content("Do you really want to delete this section?")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build().show();
    }

    private void deleteSection(final Context mCtx, final String sectionName, final int sectionPosition){
        Go.mRecyclerView.setVisibility(View.GONE);
        Go.progress.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.deleteList(mCtx,sectionName);
                removeSection(sectionPosition);
                Go.notifyRemove(mCtx);
                Go.progress.setVisibility(View.GONE);
                Go.mRecyclerView.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    @Override
    public void onBindChildViewHolder(final ChildViewHolder holder, final int sectionPosition, final int position, final PDF.PdfBean child) {
        String output = child.getTitle().substring(0, 1).toUpperCase() + child.getTitle().substring(1).toLowerCase();
        holder.title.setText(output);
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


        holder.cancelView.setOnClickListener(new View.OnClickListener() {
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

    private String getPagesString(int filePages) {
        if(filePages==0){
            return "";
        }
        if(filePages>1){
            return filePages + " pages • ";
        }
        return filePages + " page • ";
    }

    private void showSingleRemoveDialog(final Context context, final PDF.PdfBean pdfBean, final int sectionPosition, final int position)
    {
        new MaterialDialog.Builder(context)
                .title("Remove "+pdfBean.getTitle()+" ?")
                .content("Do you really want to remove pdf from this list")
                .positiveText("Remove")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Utils.removeSpecificItemFromList(context,pdfBean.getPid(),sectionHeader);
                        removeChild(sectionPosition,position);
                    }
                }).build().show();
    }

    private void openPDF(final PDF.PdfBean pdf, final int position)
    {
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
}