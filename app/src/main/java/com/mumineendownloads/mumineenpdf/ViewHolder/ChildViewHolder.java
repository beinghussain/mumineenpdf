package com.mumineendownloads.mumineenpdf.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mumineendownloads.mumineenpdf.R;



public class ChildViewHolder extends RecyclerView.ViewHolder {

    public final ImageButton imageButton;
    public TextView title,size;
    public RelativeLayout mainView;

    public ChildViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        size = (TextView) view.findViewById(R.id.size);
        imageButton = (ImageButton) view.findViewById(R.id.cancelButton);
        mainView = (RelativeLayout) view.findViewById(R.id.mainView);
    }
}