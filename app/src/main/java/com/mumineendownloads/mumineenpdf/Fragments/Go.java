package com.mumineendownloads.mumineenpdf.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intrusoft.sectionedrecyclerview.Section;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Adapters.FragmentPagerAdapterCustom;
import com.mumineendownloads.mumineenpdf.Adapters.GoSectionAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.SavedViewPagerAdapter;
import com.mumineendownloads.mumineenpdf.Helpers.CstTabLayout;
import com.mumineendownloads.mumineenpdf.Helpers.CustomAnimator;
import com.mumineendownloads.mumineenpdf.Helpers.CustomDivider;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.SectionHeader;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class Go extends Fragment {
    private MainActivity activity;
    private ArrayList<PDF.PdfBean> arrayList;
    private RecyclerView mRecyclerView;
    private GoSectionAdapter goSectionAdapter;
    public static ViewPager viewPager;
    private static SavedViewPagerAdapter viewPagerAdapter;
    public static CstTabLayout tabLayout;
    private SearchView searchView;
    private ArrayList<PDF.PdfBean> goList;
    private PDFHelper pdfHelper;
    private static CardView empty;

    public Go newInstance() {
        return new Go();
    }

    public Go() {
    }

    public static Toolbar mActivityActionBarToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_go, container, false);
        mActivityActionBarToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mActivityActionBarToolbar);
        mActivityActionBarToolbar.setTitle("Mumineen PDF - OTG Lists");
        Fonty.setFonts(mActivityActionBarToolbar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.goList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new CustomDivider(getContext()));
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        pdfHelper = new PDFHelper(getContext());
        empty = (CardView) rootView.findViewById(R.id.emptyCard);
        Fonty.setFonts((ViewGroup) rootView);

        arrayList = new ArrayList<>();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<SectionHeader> sections = new ArrayList<>();
                    List<String> sectionList = Utils.getSections(getContext());
                    if(sectionList.size()==0){
                      getActivity().runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              empty.setVisibility(View.VISIBLE);
                          }
                      });
                    }
                    for(String s : sectionList){
                        ArrayList<Integer> a = Utils.loadArray(getContext(),s);
                        ArrayList<PDF.PdfBean> b = new ArrayList<PDF.PdfBean>();
                        for(int i = 0; i<a.size(); i++){
                            b.add(pdfHelper.getPDF(a.get(i)));
                        }
                        Log.e("Size", String.valueOf(a.size()));
                        sections.add(new SectionHeader(b, s));
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goSectionAdapter = new GoSectionAdapter(getContext(),sections);
                            mRecyclerView.setAdapter(goSectionAdapter);
                        }

                    });
                } catch (NullPointerException ignored) {
                    Log.e("Crashed","Yup");
                }
            }
        });

        Fonty.setFonts(tabLayout);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate( R.menu.toolbar_menu, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void notifyRemove(Context context) {
        List<String> sectionList = Utils.getSections(context);
        if(sectionList.size()==0){
            empty.setVisibility(View.VISIBLE);
        }
    }
}
