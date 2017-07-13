package com.mumineendownloads.mumineenpdf.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Adapters.BasePDFAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.GoSectionAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapterCat;
import com.mumineendownloads.mumineenpdf.Helpers.CstTabLayout;
import com.mumineendownloads.mumineenpdf.Helpers.CustomDivider;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.SectionHeader;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class Go extends Fragment implements BasePDFAdapter.OnItemClickListener {
    private MainActivity activity;
    private ArrayList<PDF.PdfBean> arrayList;
    public static RecyclerView mRecyclerView;
    private GoSectionAdapter goSectionAdapter;
    public static CstTabLayout tabLayout;
    private SearchView searchView;
    private ArrayList<PDF.PdfBean> goList;
    private PDFHelper pdfHelper;
    private static CardView empty;
    public static ProgressView progress;
    private DownloadReceiver mReceiver;
    private Comparator<PDF.PdfBean> pdfBeanComparator;
    private BasePDFAdapter mSectionedRecyclerAdapter;
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
        mActivityActionBarToolbar.setTitle("Library");
        Fonty.setFonts(mActivityActionBarToolbar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.goList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new CustomDivider(getContext()));
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        pdfHelper = new PDFHelper(getContext());
        empty = (CardView) rootView.findViewById(R.id.emptyCard);
        progress = (ProgressView)rootView.findViewById(R.id.progress);
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
                                progress.setVisibility(View.GONE);
                            }
                        });
                    }
                    for(String s : sectionList){
                        ArrayList<Integer> a = Utils.loadArray(getContext(),s);
                        ArrayList<PDF.PdfBean> b = new ArrayList<PDF.PdfBean>();
                        for(int i = 0; i<a.size(); i++){
                            PDF.PdfBean pdf = pdfHelper.getPDF(a.get(i));
                            b.add(pdf);
                            arrayList.add(pdf);
                        }
                        sections.add(new SectionHeader(b, s));
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goSectionAdapter = new GoSectionAdapter(getContext(),sections);
                            mRecyclerView.setAdapter(goSectionAdapter);
                            progress.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }

                    });
                } catch (NullPointerException ignored) {
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

    @Override
    public void onItemClicked(PDF.PdfBean pdf) {

    }

    class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(arrayList==null){
                return;
            }
            final String action = intent.getAction();
            if (action == null || !action.equals(DownloadService.ACTION_DOWNLOAD_BROAD_CAST)) {
                return;
            }
            final int position = intent.getIntExtra(DownloadService.EXTRA_POSITION, -1);
            final PDF.PdfBean tmpPdf = (PDF.PdfBean) intent.getSerializableExtra(DownloadService.EXTRA_APP_INFO);
            if (tmpPdf == null || position == -1) {
                return;
            }
            if(isCurrentListViewItemVisible(position)) {
                final PDF.PdfBean pdf = getPDF(tmpPdf.getPid());
                final int status = tmpPdf.getStatus();
                if(status!=Status.STATUS_DOWNLOADING){
                    pdfHelper.updatePDF(tmpPdf);
                }
                if (pdf.getPid() == tmpPdf.getPid()) {
                    if (status == Status.STATUS_LOADING) {
                        pdf.setStatus(Status.STATUS_LOADING);
                        goSectionAdapter.notifyDataSetChanged();
                    } else if (status == Status.STATUS_DOWNLOADING) {
                        pdf.setStatus(Status.STATUS_DOWNLOADING);
                        pdf.setDownloadPerSize(tmpPdf.getDownloadPerSize());
                        pdf.setProgress(tmpPdf.getProgress());
                        goSectionAdapter.notifyDataSetChanged();
                    } else if (status == Status.STATUS_NULL) {
                        pdf.setStatus(Status.STATUS_NULL);
                        goSectionAdapter.notifyDataSetChanged();
                    } else if (status==Status.STATUS_DOWNLOADED){
                        pdf.setStatus(Status.STATUS_DOWNLOADED);
                        goSectionAdapter.notifyDataSetChanged();
                    } else if (status==Status.STATUS_CONNECTED){
                        pdf.setStatus(Status.STATUS_CONNECTED);
                        goSectionAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private boolean isCurrentListViewItemVisible(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        return first <= position && position <= last;
    }

    private PDF.PdfBean getPDF(int pid){
        for(PDF.PdfBean pdfBean : arrayList){
            if(pdfBean.getPid()==pid){
                return pdfBean;
            }
        }
        return arrayList.get(0);
    }

    public void onPause() {
        super.onPause();
        unRegister();
    }

    public void onResume() {
        super.onResume();
        register();
    }

    private void register() {
        mReceiver = new Go.DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        }
    }

}
