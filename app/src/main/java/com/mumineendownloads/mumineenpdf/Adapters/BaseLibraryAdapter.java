package com.mumineendownloads.mumineenpdf.Adapters;

import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.widget.CardView;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.marcinorlowski.fonty.Fonty;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.ArrayList;

public abstract class BaseLibraryAdapter extends SectionedRecyclerViewAdapter<BaseLibraryAdapter.SubHeaderHolder, RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(PDF.PdfBean pdf  );
    }

    private ArrayList<PDF.PdfBean> pdfBeanArrayList;

    @Override
    public int getViewType(int position) {
        position = getItemPositionForViewHolder(position);
        if(pdfBeanArrayList.get(position).getGo().equals("ZeeAd")){
            return 1;
        }
        return 0;
    };

    OnItemClickListener onItemClickListener;

    static class SubHeaderHolder extends RecyclerView.ViewHolder {

        public final TextView remove;
        TextView mSubHeaderText;
        TextView downloadLeft;
        public ImageButton download_all;
        public ImageButton delete;
        public CardView mainView;

        SubHeaderHolder(View itemView) {
            super(itemView);
            Fonty.setFonts((ViewGroup) itemView);
            this.mSubHeaderText = (TextView) itemView.findViewById(R.id.sectionHeader);
            this.downloadLeft = (TextView) itemView.findViewById(R.id.download_left);
            this.remove = (TextView) itemView.findViewById(R.id.remove);
            this.mainView = (CardView)itemView.findViewById(R.id.mainView);
        }

    }

    static class PDFViewHolder extends RecyclerView.ViewHolder {
        CircularProgressBar progressBarDownload;
        ImageView imageView;
        public TextView title;
        public TextView size;
        RelativeLayout mainView,cancelView;
        LinearLayout parentView;
        public ProgressView loading;
        Button button;
        ImageButton cancel;
        public TextView album;

        PDFViewHolder(View view) {
            super(view);
            Fonty.setFonts((ViewGroup) view);
            album = (TextView) view.findViewById(R.id.album);
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
    public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        if(viewType!=1) {
            return new PDFViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_pdf_item, parent, false));
        }else {
            return new AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_view, parent, false));
        }
    }

    @Override
    public SubHeaderHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        return new SubHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.section_header, parent, false));
    }

    @Override
    public int getItemSize() {
        return pdfBeanArrayList.size();
    }


    private class AdViewHolder extends RecyclerView.ViewHolder {
        NativeExpressAdView adView;
        CardView cardView;
        public AdViewHolder(View inflate) {
            super(inflate);
            cardView = (CardView)inflate.findViewById(R.id.card);
            adView = new NativeExpressAdView(inflate.getContext());
            adView.setAdUnitId("ca-app-pub-4276158682587806/7378652958");
            adView.setAdSize(new AdSize(AdSize.FULL_WIDTH,80));
            cardView.addView(adView);
            Fonty.setFonts((ViewGroup) inflate);
            AdRequest request = new AdRequest.Builder().addTestDevice("265F30F3EA52FBFB7782FEE86B7DE645").build();
            adView.loadAd(request);
        }
    }
}
