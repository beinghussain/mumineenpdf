package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.intrusoft.sectionedrecyclerview.Section;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Fragments.Go;
import com.mumineendownloads.mumineenpdf.Helpers.SectionHeader;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.ViewHolder.ChildViewHolder;
import com.mumineendownloads.mumineenpdf.ViewHolder.SectionViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by Hussain on 7/6/2017.
 */

public class GoSectionAdapter extends SectionRecyclerViewAdapter<SectionHeader, PDF.PdfBean, SectionViewHolder, ChildViewHolder> {

    private Context context;

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
    public void onBindChildViewHolder(ChildViewHolder childViewHolder, int sectionPosition, int childPosition, final PDF.PdfBean child) {
        String t;
        childViewHolder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF(child);
            }
        });
        if (Integer.parseInt(child.getSize()) < 1024) {
            t = child.getSize() + " KB";
        } else {
            Float size = Float.valueOf(child.getSize()) / 1024;
            t = new DecimalFormat("##.##").format(size) + " MB";
        }
        childViewHolder.title.setText(child.getTitle());
        childViewHolder.size.setText(child.getAlbum() + " • " + t);
    }

    private void openPDF(PDF.PdfBean pdf) {
            Intent intent = new Intent(context, PDFActivity.class);
            intent.putExtra("mode",0);
            intent.putExtra("pid", pdf.getPid());
            intent.putExtra("title", pdf.getTitle());
            context.startActivity(intent);
    }
}