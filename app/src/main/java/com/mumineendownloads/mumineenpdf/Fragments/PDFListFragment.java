package com.mumineendownloads.mumineenpdf.Fragments;

import com.afollestad.materialdialogs.DialogAction;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.*;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.multithreaddownload.DownloadManager;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapter;
import com.mumineendownloads.mumineenpdf.Helpers.CustomDivider;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.BackgroundSync;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;
import com.rey.material.widget.ProgressView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;



public class PDFListFragment extends Fragment {
    private final MainActivity activity;
    private String album;
    private ArrayList<PDF.PdfBean> arrayList;
    private RecyclerView mRecyclerView;
    private PDFAdapter mPDFAdapter;
    private ProgressView progressView;
    private PDFHelper mPDFHelper;
    private ArrayList<Integer> downloadArray;
    private static ActionMode mActionMode;
    private ArrayList<PDF.PdfBean> multiSelect_list;
    public boolean isMultiSelect;
    private DownloadReceiver mReceiver;
    private ArrayList<Integer> positionList = new ArrayList<>();
    private ArrayList<Integer> goList;
    private boolean searching;
    private ArrayList<PDF.PdfBean> newlist;

    public ArrayList<PDF.PdfBean> getMultiSelect_list(){
        return multiSelect_list;
    }
    public ArrayList<PDF.PdfBean> downloadingList = new ArrayList<>();
    private NativeAd nativeAd;
    PDFHelper helper;


    private void reportApp(final PDF.PdfBean pdfBean){
        new MaterialDialog.Builder(getActivity())
                .title("Report "+pdfBean.getTitle())
                .items(R.array.reportItems)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        sendReport(pdfBean.getPid(), text);
                        return true;
                    }
                })
                .positiveText("Choose")
                .show();
    }

    private void sendReport(final int pid, final CharSequence text) {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://mumineendownloads.com/app/pdf_error.php";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams()
            {
                String deviceId = Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Map<String, String>  params = new HashMap<String, String>();
                params.put("pdf_id", String.valueOf(pid));
                params.put("error", (String) text);
                params.put("device_id", deviceId);

                return params;
            }
        };
        queue.add(stringRequest);
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

    private void showNativeAd() {
//        AdSettings.addTestDevice("387fd295a941656b4222e5215d26babd");
//        AdSettings.setTestAdType(AdSettings.TestAdType.VIDEO_HD_9_16_39S_APP_INSTALL);
//        nativeAd = new NativeAd(getContext(), "324867547967949_324868507967853");
//        nativeAd.setAdListener(new com.facebook.ads.AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                Log.e("Ad Error", adError.getErrorCode() + adError.getErrorMessage());
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                Log.e("Ad Loaded", nativeAd.getAdRawBody());
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//
//            }
//        });
//        nativeAd.loadAd();
    }

    private void pause(String tag) {
        DownloadService.intentPause(getActivity(), tag);
    }

    private void pauseAll() {
        DownloadService.intentPauseAll(getActivity());
    }

    public PDFListFragment(int position, MainActivity activity) {
        helper = new PDFHelper(activity.getApplicationContext());
        ArrayList<String> arrayList = helper.getAlbums();
        this.activity = activity;
        multiSelect_list = new ArrayList<>();
        for(int i =0; i<arrayList.size();i++){
            if(position==i){
                album=arrayList.get(i);
            }
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
        mRecyclerView.addItemDecoration(new CustomDivider(getContext()));
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        setRecyclerViewLayoutManager(mRecyclerView);
        goList = new ArrayList<>();
        refresh(album);
        showNativeAd();
        return rootView;
    }

    private void setRecyclerViewLayoutManager(RecyclerView mRecyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
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
                searching = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                refresh(album);
                MainActivity.toggle(false);
                Home.toggleTab(false);
                searching = false;
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
            mRecyclerView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
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

    public void refresh(final String mainAlbum){
        try {
            mRecyclerView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        arrayList = mPDFHelper.getAllPDFS(mainAlbum);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressView.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mPDFAdapter = new PDFAdapter(arrayList, getActivity().getApplicationContext(), PDFListFragment.this);
                                mRecyclerView.setAdapter(mPDFAdapter);
                            }

                        });
                    } catch (NullPointerException ignored) {

                    }
                }
            });
        }catch (NullPointerException ignored){

        }
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
                    if(m.size()>10){
                    Toasty.normal(getContext(),"Cannot download more than 10 file at once").show();
                    }
                    else if(m.size()>0){
                        if(Utils.isConnected(getContext())) {
                            downloadingList.clear();
                            positionList.clear();
                            for(int i =0; i<m.size(); i++) {
                                downloadingList.add((m.get(i)));
                                positionList.add(arrayList.indexOf(m.get(i)));
                                PDF.PdfBean pdfBean = downloadingList.get(i);
                                pdfBean.setStatus(PDF.STATUS_QUEUED);
                                mPDFAdapter.notifyItemChanged(arrayList.indexOf(pdfBean));
                            }
                            startDownloading();
                            final Snackbar snackbar = Snackbar
                                    .make(mRecyclerView, "Downloading " + m.size() + " files", Snackbar.LENGTH_SHORT)
                                    .setAction("CANCEL", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            for(PDF.PdfBean p : downloadingList){
                                                p.setStatus(Status.STATUS_NULL);
                                                mPDFAdapter.notifyItemChanged(arrayList.indexOf(p));
                                            }
                                            downloadingList.clear();
                                            DownloadManager.getInstance().cancelAll();
                                        }
                                    });
                            snackbar.show();
                        }
                        else {
                            Snackbar snackbar = Snackbar
                                    .make(mRecyclerView, "No Internet Connection", Snackbar.LENGTH_SHORT)
                                    .setAction("OK", null);
                            snackbar.show();
                        }
                        destory();
                    } else {
                        Toasty.normal(getContext(),"No files to download").show();
                    }

                    break;
                case R.id.navigation_add_library:
                    goList.clear();
                    for(PDF.PdfBean pdfBean : multiSelect_list){
                        goList.add(pdfBean.getPid());
                    }
                    List<String> a = Utils.getSections(getContext());
                    a.add("Create new list");
                    MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
                    dialog
                            .title("Add "+goList.size() + " pdf to...")
                            .items(a)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    if(text.equals("Create new list")){
                                        showNewDialog(goList);
                                    }
                                    else {
                                        Utils.addToSpecificList(getContext(),goList,String.valueOf(text));
                                    }
                                }
                            });
                    dialog.show();
                    destory();
                    break;
                case R.id.delele_all:
                    deleteAll(multiSelect_list,getContext());
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

    private void showNewDialog(final ArrayList<Integer> pids) {
        new MaterialDialog.Builder(getActivity())
                .title("Enter list name")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Example: Daris Hafti", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Utils.addSectionToList(getContext(),String.valueOf(input));
                        Utils.addToSpecificList(getContext(),pids, String.valueOf(input));
                    }
                }).show();
    }

    private void deleteAll(final ArrayList<PDF.PdfBean> multiSelect_list, Context context) {
        new MaterialDialog.Builder(context)
                .title("Delete "+ multiSelect_list.size() +" files")
                .negativeText("Cancel")
                .positiveText("Delete")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        int count = 0;
                        final Handler handler = new Handler();
                        for(PDF.PdfBean pdfBean : multiSelect_list) {
                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mumineen/" + pdfBean.getPid() + ".pdf");
                            if (file.exists()) {
                                file.delete();
                                count++;
                                pdfBean.setStatus(Status.STATUS_NULL);
                                mPDFHelper.updatePDF(pdfBean);
                                mPDFAdapter.notifyItemChanged(arrayList.indexOf(pdfBean));
                            }
                        }
                        final Snackbar snackbar = Snackbar
                                .make(MainActivity.bottomNavigationView, count + " files deleted", Snackbar.LENGTH_LONG)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                        snackbar.show();
                        destory();

                    }
                })
                .content("Do you really want to delete this files?").build().show();
    }

    private void startDownloading() {
        DownloadService.intentDownload(positionList, downloadingList, getContext());
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
                newlist=new ArrayList<>();
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

    public void openDialog(final Context context, final int position, final PDF.PdfBean pdf) {
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
                                    pdf.setStatus(PDF.STATUS_QUEUED);
                                    mPDFAdapter.notifyItemChanged(position);
                                    downloadingList.clear();
                                    positionList.clear();
                                    downloadingList.add(pdf);
                                    positionList.add(position);
                                    startDownloading();
                                } else {
                                    Snackbar snackbar = Snackbar
                                            .make(mRecyclerView, "No Internet Connection", Snackbar.LENGTH_SHORT)
                                            .setAction("OK", null);
                                    snackbar.show();
                                }

                            } else if(text.equals("Delete file")) {
                                dialog.dismiss();
                                delete(pdf, context, position);
                            } else if (text.equals("View Online")) {
                                if (Utils.isConnected(context)) {
                                    mPDFAdapter.viewOnline(pdf, holder.getAdapterPosition(), holder);
                                } else {
                                    Toasty.error(context, "Internet connection not found!", Toast.LENGTH_SHORT, true).show();
                                }
                            } else if (text.equals("Share")) {
                                if(getFile(pdf.getPid()).exists()) {
                                    Uri uri = FileProvider.getUriForFile(getActivity(),
                                            getActivity().getApplicationContext().getPackageName() + ".provider", getFile(pdf.getPid()));
                                    Intent share = new Intent();
                                    share.setAction(Intent.ACTION_SEND);
                                    share.setType("application/pdf");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    startActivity(Intent.createChooser(share, "Share File"));
                                } else {
                                    Toasty.normal(getContext(), "File not downloaded yet").show();
                                }
                            } else if (text.equals("Report")) {
                                reportApp(pdf);
                            }
                        }
                    })
                    .show();
        } else {
            multi_select(position,pdf);
        }
    }

    private File getFile(int pid) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+pid+".pdf");
    }

    private void delete(final PDF.PdfBean pdf, Context context, final int position) {
        new MaterialDialog.Builder(context)
                .title("Delete file")
                .negativeText("Cancel")
                .positiveText("Delete")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mumineen/" + pdf.getPid() + ".pdf");
                        if (file.exists()) {
                            file.delete();
                            pdf.setStatus(Status.STATUS_NULL);
                            mPDFAdapter.notifyItemChanged(position);
                            mPDFHelper.updatePDF(pdf);
                        }
                    }
                })
                .content("Do you really want to delete this file?").build().show();
    }

    public static void destory() {
        if(mActionMode!=null) {
            mActionMode.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        register();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegister();
    }

    private boolean isCurrentListViewItemVisible(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        return first <= position && position <= last;
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
            Log.e("Posting", String.valueOf(position));
            final PDF.PdfBean tmpPdf = (PDF.PdfBean) intent.getSerializableExtra(DownloadService.EXTRA_APP_INFO);
            if (tmpPdf == null || position == -1) {
                return;
            }
            if(isCurrentListViewItemVisible(position)) {
                final PDF.PdfBean pdf = arrayList.get(position);
                final int status = tmpPdf.getStatus();
                if(status!=Status.STATUS_DOWNLOADING){
                    mPDFHelper.updatePDF(tmpPdf);
                }
                if (pdf.getPid() == tmpPdf.getPid()) {
                    if (status == Status.STATUS_LOADING) {
                        pdf.setStatus(Status.STATUS_LOADING);
                        mPDFAdapter.notifyItemChanged(position);
                    } else if (status == Status.STATUS_DOWNLOADING) {
                        pdf.setStatus(Status.STATUS_DOWNLOADING);
                        pdf.setDownloadPerSize(tmpPdf.getDownloadPerSize());
                        pdf.setProgress(tmpPdf.getProgress());
                        mPDFAdapter.notifyDataSetChanged();
                    } else if (status == Status.STATUS_NULL) {
                        pdf.setStatus(Status.STATUS_NULL);
                        mPDFAdapter.notifyItemChanged(position);
                    } else if (status==Status.STATUS_DOWNLOADED){
                        pdf.setStatus(Status.STATUS_DOWNLOADED);
                        mPDFAdapter.notifyItemChanged(position);
                    } else if (status==Status.STATUS_CONNECTED){
                        pdf.setStatus(Status.STATUS_CONNECTED);
                        mPDFAdapter.notifyItemChanged(position);
                    }
                    if(searching){
                        mPDFAdapter.notifyDataSetChanged();
                    }
                }
            }
       }
    }

    private void register() {
        mReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destory();
    }

    private void unRegister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        }
    }
}