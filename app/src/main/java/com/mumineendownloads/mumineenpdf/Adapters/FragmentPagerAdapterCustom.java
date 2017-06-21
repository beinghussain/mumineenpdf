package com.mumineendownloads.mumineenpdf.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;



public class FragmentPagerAdapterCustom extends FragmentPagerAdapter {

    public FragmentPagerAdapterCustom(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new PDFListFragment("Marasiya");
        }
        else if (position == 1)
        {
            fragment = new PDFListFragment("Madeh");
        }
        else if (position == 2)
        {
            fragment = new PDFListFragment("Rasa");
        }
        else if(position==3)
        {
            fragment = new PDFListFragment("Other");
        }
        else if(position==4)
        {
            fragment = new PDFListFragment("Quran30");
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
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
