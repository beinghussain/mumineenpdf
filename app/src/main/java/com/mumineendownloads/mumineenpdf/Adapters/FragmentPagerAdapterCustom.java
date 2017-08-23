package com.mumineendownloads.mumineenpdf.Adapters;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentPagerAdapterCustom extends FragmentPagerAdapter {

    private final ArrayList<String> arrayTabList;

    public FragmentPagerAdapterCustom(FragmentManager fm, MainActivity activity, ArrayList<String> arrayTabList) {
        super(fm);
        this.arrayTabList = arrayTabList;
    }

    @Override
    public PDFListFragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        PDFListFragment pdfListFragment = new PDFListFragment();
        pdfListFragment.setArguments(bundle);
        return pdfListFragment;
    }

    @Override
    public int getCount() {
        return arrayTabList.size();
    }

    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        for(int i = 0; i<arrayTabList.size(); i++){
            if(!arrayTabList.get(i).equals("0"))
            if(position==i){
                title = arrayTabList.get(i);
            }
        }
        return title;
    }
}
