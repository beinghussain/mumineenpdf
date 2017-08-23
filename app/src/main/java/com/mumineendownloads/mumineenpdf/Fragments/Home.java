package com.mumineendownloads.mumineenpdf.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Adapters.FragmentPagerAdapterCustom;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapter;
import com.mumineendownloads.mumineenpdf.Helpers.CstTabLayout;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;

import java.util.ArrayList;


public class Home extends Fragment {
    private MainActivity activity;
    private ArrayList<PDF.PdfBean> arrayList;
    private RecyclerView mRecyclerView;
    private PDFAdapter mPDFAdapter;
    public static ViewPager viewPager;
    private static FragmentPagerAdapterCustom viewPagerAdapter;
    public static CstTabLayout tabLayout;


    public Home newInstance(MainActivity activity) {
        return new Home();
    }


    public Home(){

    }

    public static Toolbar mActivityActionBarToolbar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mActivityActionBarToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mActivityActionBarToolbar);
        Fonty.setFonts(mActivityActionBarToolbar);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        ArrayList<String> arrayTabList;
        PDFHelper pdfHelper = new PDFHelper(getContext());
        arrayTabList = pdfHelper.getAlbums();
        mActivityActionBarToolbar.setTitle("Home");
        viewPagerAdapter = new FragmentPagerAdapterCustom(getChildFragmentManager(), activity, arrayTabList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(6);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                PDFListFragment.destory();
            }
        });
        tabLayout = (CstTabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Fonty.setFonts(tabLayout);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate( R.menu.toolbar_menu, menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static void toggleTab(boolean hideShow){
        if(hideShow){
            tabLayout.setVisibility(View.GONE);
        }else {
            tabLayout.setVisibility(View.VISIBLE);
        }
    }
}
