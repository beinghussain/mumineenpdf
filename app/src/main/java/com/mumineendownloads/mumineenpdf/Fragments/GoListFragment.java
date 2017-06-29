package com.mumineendownloads.mumineenpdf.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.multithreaddownload.DownloadManager;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Adapters.GoAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.SavedPDFAdapter;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.CustomAnimator;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.BackgroundSync;
import com.rey.material.widget.ProgressView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import es.dmoral.toasty.Toasty;

public class GoListFragment extends Fragment {
    private String album;
    private ArrayList<PDF.PdfBean> arrayList;
    private RecyclerView mRecyclerView;
    private GoAdapter mPDFAdapter;
    private ProgressView progressView;
    private PDFHelper mPDFHelper;
    private ArrayList<Integer> downloadArray;
    private static ActionMode mActionMode;
    private ArrayList<PDF.PdfBean> multiSelect_list;
    public boolean isMultiSelect;
    public ArrayList<PDF.PdfBean> getMultiSelect_list(){
        return multiSelect_list;
    }

    public GoListFragment(int position) {
        multiSelect_list = new ArrayList<>();
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
        View rootView = inflater.inflate(R.layout.fragment_savedlist, container, false);
        Fonty.setFonts((ViewGroup) rootView);

        mPDFHelper= new PDFHelper(getActivity().getApplicationContext());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        progressView = (ProgressView) rootView.findViewById(R.id.progress);

        mRecyclerView.addItemDecoration(new CustomAnimator(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.getItemAnimator().setChangeDuration(0);

        SharedPreferences settings = getContext().getSharedPreferences("settings", 0);
        boolean added = settings.getBoolean("added",false);

        if(added){
            progressView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            refresh(album);
        }

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_sync){
            Intent intent = new Intent(getActivity(),BackgroundSync.class);
            getActivity().startService(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh(final String mainAlbum){
        mRecyclerView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    arrayList = mPDFHelper.getDownloaded(mainAlbum);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressView.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mPDFAdapter = new GoAdapter(arrayList, getActivity().getApplicationContext(), GoListFragment.this);
                            mRecyclerView.setAdapter(mPDFAdapter);

                        }

                    });
                }catch (NullPointerException ignored){

                }
            }
        });
    }

    private ActionMode.Callback mActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.multiselect, menu);
            mPDFAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),R.color.colorActionModeDark));
            }
            Home.tabLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorActionMode));
            Home.mActivityActionBarToolbar.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorActionMode));
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            switch (id){
                case R.id.navigation_download:
                    final ArrayList<PDF.PdfBean> m = new ArrayList<>();
                    for(int i =0; i<multiSelect_list.size(); i++){
                        if(multiSelect_list.get(i).getStatus()!= Status.STATUS_DOWNLOADED) {
                            m.add(multiSelect_list.get(i));
                        }
                    }
                    if(m.size()>0){
                        if(Utils.isConnected(getContext())) {
                            for(int i =0; i<m.size(); i++) {
                            }
                            Snackbar snackbar = Snackbar
                                    .make(mRecyclerView, "Downloading " + m.size() + " files", Snackbar.LENGTH_LONG)
                                    .setAction("CANCEL", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DownloadManager.getInstance().cancelAll();
                                        }
                                    });
                            snackbar.show();
                        }else {
                            Toasty.error(getContext(),"No internet connection").show();
                        }
                        destory();
                    }else {
                        Toasty.info(getContext(),"No files to download").show();
                    }

                    break;
                case R.id.navigation_add_library:
                    break;
                case R.id.select_all:
                    selectAll(item);
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = mode;
            mActionMode = null;
            isMultiSelect = false;
            multiSelect_list.clear();
            mPDFAdapter.notifyDataSetChanged();
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));
            }
            Home.tabLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            Home.mActivityActionBarToolbar.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
        }
    };

    private void selectAll(MenuItem item) {
        if(item.getTitle().equals("Deselect All")){
            destory();
        }else {
            for (PDF.PdfBean pdfBean : arrayList) {
                multiSelect_list.add(pdfBean);
                pdfBean.setSelected(true);
            }
            item.setTitle("Deselect All");
        }
        mPDFAdapter.notifyDataSetChanged();
    }

    public void multi_select(int position, PDF.PdfBean pdfBean) {
        MenuItem item = null;
        if (mActionMode != null) {
            item = mActionMode.getMenu().findItem(R.id.navigation_download);
            if (multiSelect_list.contains(pdfBean)) {
                multiSelect_list.remove(pdfBean);
                pdfBean.setSelected(false);
            }
            else {
                multiSelect_list.add(pdfBean);
                pdfBean.setSelected(true);
            }
            if (multiSelect_list.size() > 0)
                mActionMode.setTitle( multiSelect_list.size() + " Selected");
            else
            {
                mActionMode.finish();
            }

            mPDFAdapter.notifyItemChanged(position);
        }
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

    public void enableMultiSelect(int position, PDF.PdfBean pdf) {
        if(!isMultiSelect){
            multiSelect_list = new ArrayList<PDF.PdfBean>();
            isMultiSelect = true;
            if (mActionMode == null) {
                mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionCallback);
                multi_select(position,pdf);
            }
        }
    }

    public void openDialog(final Context context, int position, final PDF.PdfBean pdf) {
        if(!isMultiSelect) {
            final PDFAdapter.MyViewHolder holder = getViewHolder(position);
            int array = R.array.preference_values;
            if (pdf.getStatus() == Status.STATUS_DOWNLOADED) {
                array = R.array.preference_values_downloaded;
            }
            new MaterialDialog.Builder(context)
                    .items(array)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            if (text.equals("Download")) {
                                if (Utils.isConnected(context)) {
                                } else {
                                    Toasty.error(context, "Internet connection not found!", Toast.LENGTH_SHORT, true).show();
                                }
                            } else if (text.equals("View Online")) {
                                if (Utils.isConnected(context)) {

                                } else {
                                    Toasty.error(context, "Internet connection not found!", Toast.LENGTH_SHORT, true).show();
                                }
                            } else if (text.equals("Share")) {
                                Toast.makeText(context, "Sharing..", Toast.LENGTH_SHORT).show();
                            } else if (text.equals("Report")) {
                                Toast.makeText(context, "Reporting...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        } else {
            multi_select(position,pdf);
        }
    }

    public static void destory() {
        if(mActionMode!=null) {
            mActionMode.finish();
        }
    }

    public void update() {

    }
}
