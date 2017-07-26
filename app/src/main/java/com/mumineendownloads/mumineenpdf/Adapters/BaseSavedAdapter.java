package com.mumineendownloads.mumineenpdf.Adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.multithreaddownload.util.L;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.itextpdf.text.Font;
import com.marcinorlowski.fonty.Fonty;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public abstract class BaseSavedAdapter extends SectionedRecyclerViewAdapter<BaseSavedAdapter.SubHeaderHolder, RecyclerView.ViewHolder> {


    private ArrayList<PDF.PdfBean> pdfBeanArrayList;
    PDFListFragment pdfListFragment;

    public BaseSavedAdapter() {

    }

    static class SubHeaderHolder extends RecyclerView.ViewHolder {

        TextView mSubHeaderText;
        TextView downloadLeft;
        public ImageButton download_all;
        public ImageButton delete;
        public CardView cardView;
        public TextView itemCount;

        SubHeaderHolder(View itemView) {
            super(itemView);
            Fonty.setFonts((ViewGroup) itemView);
            this.mSubHeaderText = (TextView) itemView.findViewById(R.id.sectionHeader);
            this.downloadLeft = (TextView) itemView.findViewById(R.id.download_left);
            this.cardView = (CardView)itemView.findViewById(R.id.card);
            this.itemCount = (TextView)itemView.findViewById(R.id.item_count);
        }

    }

    public static class PDFViewHolder extends RecyclerView.ViewHolder {
        CircularProgressBar progressBarDownload;
        ImageView imageView;
        public TextView title;
        public TextView size;
        RelativeLayout mainView,cancelView;
        LinearLayout parentView;
        public ProgressView loading;
        ImageButton cancel;
        CardView cardView;
        PDFViewHolder(View view) {
            super(view);
            Fonty.setFonts((ViewGroup) view);
            cardView = (CardView) view.findViewById(R.id.card);
            title = (TextView) view.findViewById(R.id.title);
            size = (TextView) view.findViewById(R.id.size);
            mainView = (RelativeLayout) view.findViewById(R.id.mainView);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            progressBarDownload = (CircularProgressBar) view.findViewById(R.id.spv);
            loading = (ProgressView) view.findViewById(R.id.loading);
            cancel = (ImageButton) view.findViewById(R.id.cancelButton);
            cancelView = (RelativeLayout) view.findViewById(R.id.cancel);
            parentView = (LinearLayout) view.findViewById(R.id.parentView);
        }
    }

    BaseSavedAdapter(ArrayList<PDF.PdfBean> itemList) {
        super();
        this.pdfBeanArrayList = itemList;
    }

    @Override
    public int getViewType(int position) {
        position = getItemPositionForViewHolder(position);
        if(pdfBeanArrayList.get(position).getCat().equals("ZeeAd")){
            return 1;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_pdf_item, parent, false);

        Fonty.setFonts((ViewGroup) itemView);
        return new PDFViewHolder(itemView);
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
