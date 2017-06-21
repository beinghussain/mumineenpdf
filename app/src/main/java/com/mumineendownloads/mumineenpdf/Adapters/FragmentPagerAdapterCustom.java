package com.mumineendownloads.mumineenpdf.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.mumineendownloads.mumineenpdf.Fragments.PDFDialogFragment;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;



public class FragmentPagerAdapterCustom extends FragmentPagerAdapter {

    public FragmentPagerAdapterCustom(FragmentManager fm) {
        super(fm);
    }

    @Override
    public PDFListFragment getItem(int position) {
        return new PDFListFragment(position);
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
