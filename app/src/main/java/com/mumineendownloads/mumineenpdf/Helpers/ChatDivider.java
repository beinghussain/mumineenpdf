package com.mumineendownloads.mumineenpdf.Helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ChatDivider extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable divider;

    public ChatDivider(Context context) {
        try {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            styledAttributes.recycle();
        }catch (NullPointerException ignored){

        }
    }


    public ChatDivider(Context context, int resId) {
        divider = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right = 24;
        outRect.left = 24;

        if(parent.getChildAdapterPosition(view) == state.getItemCount()-1){
            outRect.bottom = 24;
            outRect.top = 0;
        }else {
            outRect.bottom = 24;
        }
        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = 24;
            outRect.bottom = 12;
        }
    }
}
