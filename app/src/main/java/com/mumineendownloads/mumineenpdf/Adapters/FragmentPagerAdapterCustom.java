package com.mumineendownloads.mumineenpdf.Adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;



public class FragmentPagerAdapterCustom extends FragmentPagerAdapter {
    MainActivity activity;

    public FragmentPagerAdapterCustom(FragmentManager fm, MainActivity activity) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public PDFListFragment getItem(int position) {
        return new PDFListFragment(position,activity);
    }

    @Override
    public int getCount() {
        return 5;
    }

    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Marasiya";
        }
        else if (position == 1)
        {
            title = "Madeh";
        }
        else if (position == 2)
        {
            title = "Rasa";
        }
        else if (position == 3)
        {
            title = "Other";
        }
        else if (position == 4)
        {
            title = "Quran";
        }
        return title;
    }
}
