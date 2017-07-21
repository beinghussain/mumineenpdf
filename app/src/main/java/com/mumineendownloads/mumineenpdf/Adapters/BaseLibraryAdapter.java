package com.mumineendownloads.mumineenpdf.Adapters;

import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.itextpdf.text.Font;
import com.marcinorlowski.fonty.Fonty;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.ArrayList;

public abstract class BaseLibraryAdapter extends SectionedRecyclerViewAdapter<BaseLibraryAdapter.SubHeaderHolder, BaseLibraryAdapter.PDFViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(PDF.PdfBean pdf  );
    }

    ArrayList<PDF.PdfBean> pdfBeanArrayList;

    OnItemClickListener onItemClickListener;

    static class SubHeaderHolder extends RecyclerView.ViewHolder {

        TextView mSubHeaderText;
        TextView downloadLeft;
        public ImageButton download_all;
        public ImageButton delete;

        SubHeaderHolder(View itemView) {
            super(itemView);
            Fonty.setFonts((ViewGroup) itemView);
            this.mSubHeaderText = (TextView) itemView.findViewById(R.id.sectionHeader);
            this.downloadLeft = (TextView) itemView.findViewById(R.id.download_left);
        }

    }

    public static class PDFViewHolder extends RecyclerView.ViewHolder {
        public CircularProgressBar progressBarDownload;
        ImageView imageView;
        public TextView title;
        public TextView size;
        RelativeLayout mainView,cancelView;
        LinearLayout parentView;
        public ProgressView loading;
        Button button;
        ImageButton cancel;

        PDFViewHolder(View view) {
            super(view);
            Fonty.setFonts((ViewGroup) view);
            title = (TextView) view.findViewById(R.id.title);
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

    BaseLibraryAdapter(ArrayList<PDF.PdfBean> itemList) {
        super();
        this.pdfBeanArrayList = itemList;
    }

    @Override
    public PDFViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new PDFViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.go_pdf_item, parent, false));
    }

    @Override
    public SubHeaderHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        return new SubHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_section_header, parent, false));
    }

    @Override
    public int getItemSize() {
        return pdfBeanArrayList.size();
    }
}
