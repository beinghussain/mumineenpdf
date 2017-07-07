package com.mumineendownloads.mumineenpdf.Fragments;


import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.multithreaddownload.DownloadManager;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.SavedPDFAdapter;
import com.mumineendownloads.mumineenpdf.Helpers.CustomDivider;
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

public class PDFSavedListFragment extends Fragment {
    private String album;
    private ArrayList<PDF.PdfBean> arrayList;
    private RecyclerView mRecyclerView;
    private SavedPDFAdapter mPDFAdapter;
    private ProgressView progressView;
    private PDFHelper mPDFHelper;
    private ArrayList<Integer> downloadArray;
    private static ActionMode mActionMode;
    private ArrayList<PDF.PdfBean> multiSelect_list;
    public boolean isMultiSelect;
    public ArrayList<PDF.PdfBean> getMultiSelect_list(){
        return multiSelect_list;
    }

    public PDFSavedListFragment(ArrayList arrayList1, int position) {
        multiSelect_list = new ArrayList<>();
        for(int i=0; i<arrayList1.size(); i++){
            if(position==i) {
                album = arrayList1.get(i).toString();
            }
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

        mRecyclerView.addItemDecoration(new CustomDivider(getContext()));
        mRecyclerView.getItemAnimator().setChangeDuration(0);

        SharedPreferences settings = getContext().getSharedPreferences("settings", 0);
        boolean added = settings.getBoolean("added",false);
        refresh(album);


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
        searchView.setQueryHint("Search from saved pdf");
        searchView.setMaxWidth(1100);
        search(searchView);

        MenuItemCompat.setOnActionExpandListener(myActionMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                refresh("all");
                MainActivity.toggle(true);
                Saved.toggleTab(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                refresh(album);
                MainActivity.toggle(false);
                Saved.toggleTab(false);
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openRatings() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    private void showAppInfo(){
        new MaterialDialog.Builder(getActivity())
                .title("Mumineen PDF")
                .content("App for all Zakereins and Mumineen. App developed by Hussain Idrish Dehgamwala")
                .negativeText("OK")
                .positiveText("CONTACT HUSSAIN")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String url = "http://www.hddevelopers.com";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build().show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_sync){
            Intent intent = new Intent(getActivity(),BackgroundSync.class);
            getActivity().startService(intent);
        }
        if(id==R.id.action_info){
            showAppInfo();
        }
        if(id==R.id.action_rate){
            openRatings();
        }
        if(id==R.id.action_share){
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Mumineen PDF");
                String sAux = "\nPDF app for Zakreins and all the mumineens.\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share this using"));
            } catch(Exception ignored) {
            }
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
                            mPDFAdapter = new SavedPDFAdapter(arrayList, getActivity().getApplicationContext(), PDFSavedListFragment.this);
                            mRecyclerView.setAdapter(mPDFAdapter);

                        }

                    });
                }catch (NullPointerException ignored){

                }
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

    public void update() {

    }
}
