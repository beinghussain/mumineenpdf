package com.mumineendownloads.mumineenpdf.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.R;


public class SectionViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView delete;

    public SectionViewHolder(View itemView) {
        super(itemView);
        Fonty.setFonts((ViewGroup) itemView);
        name = (TextView) itemView.findViewById(R.id.sectionHeader);
        delete = (TextView) itemView.findViewById(R.id.remove);
    }
}
