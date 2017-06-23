package com.mumineendownloads.mumineenpdf.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadInfo;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapter;
import com.mumineendownloads.mumineenpdf.Constants;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.PDM;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.BackgroundSync;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PDFListFragment extends Fragment {
    private static final String SAVED_LAYOUT_MANAGER = "lm";
    private String album;
    private ArrayList<PDF.PdfBean> arrayList;
    private RecyclerView mRecyclerView;
    private PDFAdapter mPDFAdapter;
    private String tag = "MumineenPDF";
    private ProgressView progressView;
    private PDFHelper mPDFHelper;


    public PDFListFragment(int position) {
        switch (position){
            case 0:
                album = "Marasiya";
                break;
            case 1:
                album = "Madeh";
                break;
            case 2:
                album = "Rasa";
                break;
            case 3:
                album = "Other";
                break;
            case 4:
                album = "Quran30";
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pdflist, container, false);
        Fonty.setFonts((ViewGroup) rootView);

        mPDFHelper= new PDFHelper(getActivity().getApplicationContext());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        progressView = (ProgressView) rootView.findViewById(R.id.progress);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        refresh(album);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @   Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
    }

    private PDFAdapter.MyViewHolder getViewHolder(int position) {
        return (PDFAdapter.MyViewHolder) mRecyclerView.findViewHolderForLayoutPosition(position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate( R.menu.toolbar_menu, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        search(searchView);

        MenuItemCompat.setOnActionExpandListener(myActionMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                refresh("all");
                MainActivity.toggle(true);
                Home.toggleTab(true);
               return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                refresh(album);
                MainActivity.toggle(false);
                Home.toggleTab(false);
                return true;
            }
        });

        MenuItem item = menu.findItem(R.id.action_notifications);
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        Utils.setBadgeCount(getActivity(), icon, 0);
    }





    public void refresh(final String mainAlbum){
        progressView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                arrayList = mPDFHelper.getAllPDFS(mainAlbum);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressView.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mPDFAdapter = new PDFAdapter(arrayList,getActivity().getApplicationContext(),PDFListFragment.this);
                                mRecyclerView.setAdapter(mPDFAdapter);
                            }
                        }, 500);

                    }
                });
            }
        });
    }




    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText=newText.toLowerCase();
                ArrayList<PDF.PdfBean>newlist=new ArrayList<>();
                for(PDF.PdfBean name:arrayList)
                {
                    String getName=name.getTitle().toLowerCase();
                    if(getName.contains(newText)){
                        newlist.add(name);
                    }
                }
                mPDFAdapter.filter(newlist);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_sync){
            BackgroundSync bg = new BackgroundSync(getActivity().getApplicationContext(), PDFListFragment.this);
            bg.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateProgressBar(final int progress, int position, long finished, long total) {
        try {
            final PDFAdapter.MyViewHolder holder = getViewHolder(position);
            final Handler handler = new Handler();
            String f, t;
            int sizeF = (int) (finished / 1024);
            int sizeT = (int) (total / 1024);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                holder.progressBarDownload.setProgress(progress);
                            } catch (NullPointerException ignored){

                            }
                        }
                    });
                }
            }).start();
            if (total < 1000000) {
                t = total / 1024 + "KB ";
            } else {
                Float size = (float) sizeT / 1024;
                t = new DecimalFormat("##.##").format(size) + " MB ";
            }
            if (finished < 1000000) {
                f = finished / 1024 + "KB / ";
            } else {
                Float size = (float) sizeF / 1024;
                f = new DecimalFormat("##.##").format(size) + " MB / ";
            }
            holder.size.setText(f + t + progress + "%");
        }catch (NullPointerException ignored){

        }
    }

    public void notifyDatasetChanged() {
        mPDFAdapter.notifyDataSetChanged();
    }
}
