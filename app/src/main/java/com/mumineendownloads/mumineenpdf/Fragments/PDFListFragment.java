package com.mumineendownloads.mumineenpdf.Fragments;

import com.afollestad.materialdialogs.DialogAction;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.util.L;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Adapters.BasePDFAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapterCat;
import com.mumineendownloads.mumineenpdf.Helpers.CustomDivider;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.BackgroundSync;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;
import com.ohoussein.playpause.PlayPauseView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.ProgressView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import es.dmoral.toasty.Toasty;


public class PDFListFragment extends Fragment {
    private int position;
    private String album;
    private ArrayList<PDF.PdfBean> arrayList;
    private RecyclerView mRecyclerView;
    private ProgressView progressView;
    private PDFHelper mPDFHelper;
    private static ActionMode mActionMode;
    private ArrayList<PDF.PdfBean> multiSelect_list;
    public boolean isMultiSelect;
    private DownloadReceiver mReceiver;
    private ArrayList<Integer> positionList = new ArrayList<>();
    private ArrayList<Integer> goList;
    private boolean searching;
    private ArrayList<PDF.PdfBean> newlist;
    private PDFAdapterCat sectionedRecyclerAdapter;
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private boolean initialStage = true;
    private View dialogView;
    private InterstitialAd mInterstitialAd;
    private MaterialDialog audioDialog;

    public ArrayList<PDF.PdfBean> getMultiSelect_list(){
        return multiSelect_list;
    }

    public ArrayList<PDF.PdfBean> downloadingList = new ArrayList<>();

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
                        Toasty.normal(getContext(),"File Reported!").show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.normal(getContext(),"Failed to report!").show();
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

    public PDFListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pdflist, container, false);
        Fonty.setFonts((ViewGroup) rootView);

        mPDFHelper= new PDFHelper(getActivity().getApplicationContext());
        ArrayList<String> arrayList = mPDFHelper.getAlbums();
        multiSelect_list = new ArrayList<>();
        for(int i =0; i<arrayList.size();i++){
            if(position==i){
                album=arrayList.get(i);
            }
        }
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        progressView = (ProgressView) rootView.findViewById(R.id.progress);
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        setRecyclerViewLayoutManager(mRecyclerView);
        goList = new ArrayList<>();
        refresh(album);
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
        position = 0;
        Bundle bundle = this.getArguments();

        if(bundle != null){
            Log.e("Bundle", String.valueOf(bundle));
            position = bundle.getInt("position");
        }
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate( R.menu.toolbar_menu, menu);


        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment selectedFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("what","pdfList");
                selectedFragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, selectedFragment);
                transaction.addToBackStack("pdfList");
                transaction.commit();
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
                sAux = sAux + "http://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share this using"));
            } catch(Exception ignored) {
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void openRatings() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
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
                        new Thread(new Runnable() {
                            public void run() {
                                arrayList = mPDFHelper.getAllPDFS(mainAlbum);
                                final PDF.PdfBean pdfBean = new PDF.PdfBean();
                                pdfBean.setPid(-5);
                                pdfBean.setCat("ZeeAd");
                                if(Utils.isConnected(getContext())) {
                                    if (arrayList.size() > 15) {
                                        arrayList.add(pdfBean);
                                    }
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.addItemDecoration(new CustomDivider(getContext(),arrayList));
                                        Collections.sort(arrayList, new Comparator<PDF.PdfBean>() {
                                            @Override
                                            public int compare(PDF.PdfBean o1, PDF.PdfBean o2) {
                                                return o1.getCat().compareTo(o2.getCat());
                                            }
                                        });
                                        sectionedRecyclerAdapter = new PDFAdapterCat(arrayList,getContext(),PDFListFragment.this);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mRecyclerView.setAdapter(sectionedRecyclerAdapter);
                                                mRecyclerView.setVisibility(View.VISIBLE);
                                                progressView.setVisibility(View.INVISIBLE);
                                            }
                                        },1000);

                                    }

                                });
                            }
                        }).start();



        }catch (NullPointerException ignored){

        }
    }

    private ActionMode.Callback mActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.multiselect, menu);
            sectionedRecyclerAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));
            }
            Home.tabLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            Home.mActivityActionBarToolbar.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
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
                                sectionedRecyclerAdapter.notifyItemChangedAtPosition(arrayList.indexOf(pdfBean));
                            }
                            startDownloading();
                            final Snackbar snackbar = Snackbar
                                    .make(mRecyclerView, "Downloading " + m.size() + " files", Snackbar.LENGTH_SHORT)
                                    .setAction("CANCEL", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            for(PDF.PdfBean p : downloadingList){
                                                p.setStatus(Status.STATUS_NULL);
                                                sectionedRecyclerAdapter.notifyItemChangedAtPosition(arrayList.indexOf(p));
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
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
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
                                        } else {
                                            Utils.addToSpecificList(getContext(),goList, String.valueOf(text));
                                            Utils.showSnack(goList.size() + " pdf added to "+text, "VIEW LIBRARY", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            MainActivity.bottomNavigationView.setSelectedItemId(R.id.navigation_upload);
                                                        }
                                                    });
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
            sectionedRecyclerAdapter.notifyDataSetChanged();
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
                        Utils.showSnack(goList.size() + " pdf added to " + input, "View Library", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.bottomNavigationView.setSelectedItemId(R.id.navigation_upload);
                            }
                        });
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
                                sectionedRecyclerAdapter.notifyItemChangedAtPosition(arrayList.indexOf(pdfBean));
                            }
                        }
                        if(count!=0) {
                            final Snackbar snackbar = Snackbar
                                    .make(MainActivity.bottomNavigationView, count + " files deleted", Snackbar.LENGTH_LONG)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                            snackbar.show();
                        }
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

            sectionedRecyclerAdapter.notifyItemChangedAtPosition(position);
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
                try {
                    newText = newText.toLowerCase();
                    newlist = new ArrayList<>();
                    for (PDF.PdfBean name : arrayList) {
                        String getName = name.getTitle().toLowerCase();
                        if (getName.contains(newText)) {
                            newlist.add(name);
                        }
                    }
                    sectionedRecyclerAdapter.filter(newlist);
                    return true;
                } catch (NullPointerException ignored){
                    return false;
                }
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
            List<String> strings = new ArrayList<>();

            if(pdf.getAudio()!=0){
                strings.add("Play Audio");
            }
            if (pdf.getStatus() == Status.STATUS_DOWNLOADED) {
                strings.add("Add to My Library");
                strings.add("Report");
                strings.add("Share");
                strings.add("Delete file");
            } else {
                strings.add("Download");
                strings.add("Report");
                strings.add("Add to My Library");
            }

            new MaterialDialog.Builder(context)
                    .title("Options")
                    .items(strings)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            if(text.equals("Play Audio")){
                                playAudio(pdf);
                            }
                            else if (text.equals("Download")) {
                                if (Utils.isConnected(context)) {
                                    pdf.setStatus(PDF.STATUS_QUEUED);
                                    sectionedRecyclerAdapter.notifyItemChangedAtPosition(position);
                                    downloadingList.clear();
                                    positionList.clear();
                                    downloadingList.add(pdf);positionList.add(position);
                                    Log.e("Sending position", String.valueOf(position));
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
                            else if(text.equals("Add to My Library")){
                                showDialogListAdd(pdf);
                            }
                        }
                    })
                    .show();
        } else {
            multi_select(position,pdf);
        }
    }

    private void playAudio(PDF.PdfBean pid) {
        mediaPlayer = new MediaPlayer();
        audioDialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.audio_dialog,true)
                .build();
        audioDialog.show();
        audioDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                initialStage = true;
            }
        });
        dialogView = audioDialog.getCustomView();
        Fonty.setFonts((ViewGroup) dialogView);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        if (initialStage)
            new Player()
                    .execute("http://pdf.mumineendownloads.com/audio.php?id="+pid.getAudio());
    }

    private class Player extends AsyncTask<String, Void, Boolean> {
        TextView total,current;
        private Handler mHandler = new Handler();
        PlayPauseView view;
        ProgressView progressView;
        SeekBar seekbar;
        int buffered = 0;
        int bufferPercent;
        private boolean pausedBySystem = false;

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean prepared;
            try {
                mediaPlayer.setDataSource(params[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(!mInterstitialAd.isLoaded()){
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }else {
                            mInterstitialAd.show();
                        }
                        initialStage = true;
                        playPause=false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        audioDialog.dismiss();
                    }
                });
                mediaPlayer.prepare();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mediaPlayer != null){
                            int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            current.setText(Utils.timeFormat(mCurrentPosition));
                            seekbar.setProgress(mCurrentPosition);
                        }
                        mHandler.postDelayed(this, 1000);
                    }
                });
                prepared = true;

            } catch (IllegalArgumentException e) {
                Log.d("IllegalArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException | IllegalStateException | IOException e) {
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            try {
                super.onPostExecute(result);
                mediaPlayer.start();
                total.setText(Utils.timeFormat(mediaPlayer.getDuration() / 1000));
                seekbar.setMax(mediaPlayer.getDuration() / 1000);
                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(progress>0 && progress<=5){
                            showBuffering(false);
                            if(view.isPlay()) {
                                view.toggle(true);
                            }
                        }
                        if(seekbar.getProgress()*1000>buffered*1000) {
                            showBuffering(true);
                        }else {
                            showBuffering(false);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.seekTo(seekBar.getProgress() * 1000);
                        if(seekBar.getProgress()*1000>buffered*1000) {
                            showBuffering(true);
                        }else {
                            showBuffering(false);
                        }
                    }
                });
                initialStage = false;

                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        buffered = (percent * (mp.getDuration() / 1000) / 100);
                        bufferPercent = percent;
                        seekbar.setSecondaryProgress(buffered);
                    }
                });

                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what==MediaPlayer.MEDIA_INFO_BUFFERING_END){
                            showBuffering(false);
                        }
                        else if(what==MediaPlayer.MEDIA_INFO_BUFFERING_START){
                            showBuffering(true);
                        }
                        return true;
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                view.toggle(true);
                            } else {
                                mediaPlayer.start();
                                view.toggle(true);
                            }
                        }
                    }
                });
            }catch (NullPointerException ignored){
                Toasty.normal(getContext(), "Some error occured").show();
            }
        }

        Player() {
           total = (TextView) dialogView.findViewById(R.id.total);
           current = (TextView)dialogView.findViewById(R.id.current);
            view = (PlayPauseView) dialogView.findViewById(R.id.play_pause_view);
            seekbar = (SeekBar) dialogView.findViewById(R.id.seekBar);
            progressView = (ProgressView) dialogView.findViewById(R.id.loading);
        }

        void showBuffering(boolean progressing){
            if(progressing) {
                    view.setVisibility(View.GONE);
                    progressView.setVisibility(View.VISIBLE);

            }else {
                    view.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showBuffering(true);

        }
    }

    private void showDialogListAdd(final PDF.PdfBean pdf) {
        List<String> a = Utils.getSections(getContext());
        a.add("Create new list");
        final ArrayList<Integer> list = new ArrayList<>();
        list.add(pdf.getPid());
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .title("Add "+pdf.getTitle() + " to...")
                .items(a)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if(text.equals("Create new list")){
                            showNewDialog(list);
                        } else {
                            Utils.addToSpecificList(getContext(),list, String.valueOf(text));
                            Utils.showSnack("Added " + list.size() + " PDF to " + text, "View Library", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MainActivity.bottomNavigationView.setSelectedItemId(R.id.navigation_upload);
                                }
                            });
                        }
                    }
                });
        dialog.show();
    }

    private File getFile(int pid) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+pid+".pdf");
    }

    private void delete(final PDF.PdfBean pdf, final Context context, final int position) {
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
                            sectionedRecyclerAdapter.notifyItemChangedAtPosition(position);
                            mPDFHelper.updatePDF(pdf);
                            Toasty.normal(context,"File Deleted Successfully").show();
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

    private PDF.PdfBean getPDF(int pid){
        for(PDF.PdfBean pdfBean : arrayList){
            if(pdfBean.getPid()==pid){
                return pdfBean;
            }
        }
        return arrayList.get(0);
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
            Log.e("Receiving", String.valueOf(position));
                final PDF.PdfBean pdf = getPDF(tmpPdf.getPid());
                final int status = tmpPdf.getStatus();
                if(status!=Status.STATUS_DOWNLOADING){
                    mPDFHelper.updatePDF(tmpPdf);
                }
                if (pdf.getPid() == tmpPdf.getPid()) {
                    if (status == Status.STATUS_LOADING) {
                        pdf.setStatus(Status.STATUS_LOADING);
                        sectionedRecyclerAdapter.notifyItemChangedAtPosition(position);
                    } else if (status == Status.STATUS_DOWNLOADING) {
                        pdf.setStatus(Status.STATUS_DOWNLOADING);
                        Log.e("PDF",pdf.getTitle());
                        pdf.setDownloadPerSize(tmpPdf.getDownloadPerSize());
                        pdf.setProgress(tmpPdf.getProgress());
                        sectionedRecyclerAdapter.notifyDataSetChanged();
                    } else if (status == Status.STATUS_NULL) {
                        pdf.setStatus(Status.STATUS_NULL);
                        sectionedRecyclerAdapter.notifyItemChangedAtPosition(position);
                    } else if (status==Status.STATUS_DOWNLOADED){
                        pdf.setStatus(Status.STATUS_DOWNLOADED);
                        pdf.setPageCount(tmpPdf.getPageCount());
                        sectionedRecyclerAdapter.notifyItemChangedAtPosition(position);
                    } else if (status==Status.STATUS_CONNECTED){
                        pdf.setStatus(Status.STATUS_CONNECTED);
                        sectionedRecyclerAdapter.notifyItemChangedAtPosition(position);
                    }
                    if(searching){
                        sectionedRecyclerAdapter.notifyDataSetChanged();
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