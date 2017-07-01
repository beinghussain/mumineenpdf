package com.mumineendownloads.mumineenpdf.Adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mumineendownloads.mumineenpdf.Fragments.PDFSavedListFragment;

import java.util.ArrayList;


public class SavedViewPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<String> arrayList;

    public SavedViewPagerAdapter(FragmentManager fm, ArrayList<String> noOfTabs) {
        super(fm);
        this.arrayList = noOfTabs;
    }

    @Override
    public PDFSavedListFragment getItem(int position) {
        return new PDFSavedListFragment(arrayList, position);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        for(int i = 0; i<arrayList.size(); i++){
            if(position==i){
                title = arrayList.get(i);
            }
        }
        return title;
    }
}
