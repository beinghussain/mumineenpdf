package com.mumineendownloads.mumineenpdf.Helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.mumineendownloads.mumineenpdf.Model.PDF;

import java.util.ArrayList;

/**
 * Created by Hussain on 7/6/2017.
 */

public class CustomDivider extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};


    private Drawable divider;
    private ArrayList<PDF.PdfBean> arrayList;
    /**
     * Default divider will be used
     */
    public CustomDivider(Context context, ArrayList<PDF.PdfBean> arrayList) {
        try {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            this.arrayList = arrayList;
            styledAttributes.recycle();
        }catch (NullPointerException ignored){

        }
    }

    /**
     * Custom divider will be used
     */
    public CustomDivider(Context context, int resId) {
        divider = ContextCompat.getDrawable(context, resId);
    }

    public CustomDivider(Context context) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        styledAttributes.recycle();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if(parent.getChildAdapterPosition(view) == state.getItemCount()-1){
            outRect.bottom = 24;
            outRect.top = 0; //don't forget about recycling...
        }else {
            outRect.bottom = 1;
        }
        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = 24;
            outRect.bottom = 1;
        }
    }
}