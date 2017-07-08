package com.mumineendownloads.mumineenpdf.Adapters;

import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.itextpdf.text.Font;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.ArrayList;

public abstract class BasePDFAdapter extends SectionedRecyclerViewAdapter<BasePDFAdapter.SubHeaderHolder, BasePDFAdapter.PDFViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(PDF.PdfBean pdf  );
    }

    ArrayList<PDF.PdfBean> pdfBeanArrayList;

    OnItemClickListener onItemClickListener;

    static class SubHeaderHolder extends RecyclerView.ViewHolder {

        private static Typeface meduiumTypeface = null;

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

    static class PDFViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView size;

        PDFViewHolder(View itemView) {
            super(itemView);
            Fonty.setFonts((ViewGroup) itemView);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.size = (TextView) itemView.findViewById(R.id.size);
        }
    }

    BasePDFAdapter(ArrayList<PDF.PdfBean> itemList) {
        super();
        this.pdfBeanArrayList = itemList;
    }

    @Override
    public PDFViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new PDFViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.go_pdf_item, parent, false));
    }

    @Override
    public SubHeaderHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        return new SubHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.section_header, parent, false));
    }

    @Override
    public int getItemSize() {
        return pdfBeanArrayList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
