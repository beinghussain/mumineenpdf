package com.mumineendownloads.mumineenpdf.ViewHolder;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.R;
import com.rey.material.widget.Button;


public class SectionViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView delete;
    public ImageButton download_all;
    public TextView downloadLeft;

    public SectionViewHolder(View itemView) {
        super(itemView);
        Fonty.setFonts((ViewGroup) itemView);
        name = (TextView) itemView.findViewById(R.id.sectionHeader);
        delete = (TextView) itemView.findViewById(R.id.remove);
        downloadLeft = (TextView) itemView.findViewById(R.id.download_left);
    }
}
