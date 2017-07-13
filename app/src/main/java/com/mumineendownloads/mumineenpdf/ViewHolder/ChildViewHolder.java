package com.mumineendownloads.mumineenpdf.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marcinorlowski.fonty.Fonty;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mumineendownloads.mumineenpdf.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;


public class ChildViewHolder extends RecyclerView.ViewHolder {

    public CircularProgressBar progressBarDownload;
    public ImageView imageView;
    public TextView title;
    public TextView size;
    RelativeLayout mainView;
    public RelativeLayout cancelView;
    public LinearLayout parentView;
    public ProgressView loading;
    public Button button;
    public ImageButton cancel;

    public ChildViewHolder(View view) {
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