package com.mumineendownloads.mumineenpdf.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.mumineendownloads.mumineenpdf.Fragments.PDFDialogFragment;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;
import com.mumineendownloads.mumineenpdf.Fragments.PDFSavedListFragment;


public class SavedViewPagerAdapter extends FragmentPagerAdapter {

    public SavedViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public PDFSavedListFragment getItem(int position) {
        return new PDFSavedListFragment(position);
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
