package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Fragments.Go;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.SectionHeader;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;
import com.mumineendownloads.mumineenpdf.ViewHolder.ChildViewHolder;
import com.mumineendownloads.mumineenpdf.ViewHolder.SectionViewHolder;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class GoSectionAdapter extends SectionRecyclerViewAdapter<SectionHeader, PDF.PdfBean, SectionViewHolder, ChildViewHolder> {

    private Context context;
    private int download_left = 0;
    private String sectionHeader;

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
    public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, final int sectionPosition, final SectionHeader sectionHeader) {
        sectionViewHolder.name.setText(sectionHeader.getSectionText());
        download_left = Utils.getPDFNotDownloadedCount(context,sectionHeader.getSectionText());
        sectionViewHolder.download_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownloadDialog(v.getContext(), sectionHeader.getSectionText());
            }
        });
        this.sectionHeader = sectionHeader.getSectionText();
        if(download_left!=0){
           sectionViewHolder.download_all.setVisibility(View.VISIBLE);
            sectionViewHolder.downloadLeft.setText(download_left + " files not downloaded");
       } else {
           sectionViewHolder.download_all.setVisibility(View.INVISIBLE);
       }
        sectionViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
           showAlert(context,sectionPosition,sectionHeader.getSectionText());
            }
        });
    }

    private void showDownloadDialog(final Context context, String sectionText) {
        int count = Utils.getPDFNotDownloadedCount(context,sectionText);
        List<Integer> pid = Utils.loadArray(context,sectionText);
        final ArrayList<PDF.PdfBean> pdfBeanArrayList = new ArrayList<>();
        final ArrayList<Integer> fakePositionList = new ArrayList<>();
        PDFHelper p = new PDFHelper(context);
        for(int i : pid){
            PDF.PdfBean pdfBean = p.getPDF(i);
            if(pdfBean.getStatus()!= Status.STATUS_DOWNLOADED){
                pdfBeanArrayList.add(pdfBean);
                fakePositionList.add(i);
            }
        }
        new MaterialDialog.Builder(context)
                .title("Download "+count + " files")
                .negativeText("Cancel")
                .positiveText("Download")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                       showDownloading(context,pdfBeanArrayList,fakePositionList);
                    }
                }).build().show();
    }

    private void showDownloading(Context context, ArrayList<PDF.PdfBean> pdfBeanArrayList, ArrayList<Integer> fakePositionList) {
        DownloadService.intentDownload(fakePositionList,pdfBeanArrayList,context);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                builder.title("Downloading " + pdfBeanArrayList.size() + " Files")
                .content("Please wait..")
                .cancelable(false)
                .progress(true,100)
                .progressIndeterminateStyle(true)
                .build().show();
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
    public void onBindChildViewHolder(ChildViewHolder childViewHolder, final int sectionPosition, final int childPosition, final PDF.PdfBean child) {
        String t;
        childViewHolder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF(child);
            }
        });
        childViewHolder.title.setText(child.getTitle());
        childViewHolder.size.setText(child.getAlbum() + " • " + Utils.fileSize(child.getSize()));
        if(child.getStatus()!=Status.STATUS_DOWNLOADED){
          childViewHolder.imageButton.setVisibility(View.VISIBLE);

        }

        childViewHolder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showOptionDialog(v.getContext(),child,sectionPosition,childPosition);
                return true;
            }
        });

    }

    private void showOptionDialog(final Context context, final PDF.PdfBean pdfBean, final int sectionPosition, final int position) {
        List<String> list = new ArrayList<>();
        list.add("Remove from list");
        new MaterialDialog.Builder(context)
                .items(list)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text.equals("Remove from list")) {
                           showSingleRemoveDialog(view.getContext(),pdfBean,sectionPosition,position);
                        }
                    }
                })
                .show();
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

    private void openPDF(PDF.PdfBean pdf) {
            Intent intent = new Intent(context, PDFActivity.class);
            intent.putExtra("mode",0);
            intent.putExtra("pid", pdf.getPid());
            intent.putExtra("title", pdf.getTitle());
            context.startActivity(intent);
    }
}