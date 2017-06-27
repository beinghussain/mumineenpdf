package com.mumineendownloads.mumineenpdf.Helpers;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import java.lang.reflect.Field;

/**
 * Created by Hussain on 6/27/2017.
 */

public class CstTabLayout extends TabLayout {
    public CstTabLayout(Context context) {
        super(context);
    }

    public CstTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CstTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            if (getTabCount() == 0)
                return;
            Field field = TabLayout.class.getDeclaredField("mTabMinWidth");
            field.setAccessible(true);
            field.set(this, (int) (getMeasuredWidth() / (float) getTabCount()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}