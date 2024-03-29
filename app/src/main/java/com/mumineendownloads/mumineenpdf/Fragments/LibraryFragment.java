package com.mumineendownloads.mumineenpdf.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Adapters.LibraryAdapter;
import com.mumineendownloads.mumineenpdf.Helpers.CstTabLayout;
import com.mumineendownloads.mumineenpdf.Helpers.CustomDivider;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.Library;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;
import com.ohoussein.playpause.PlayPauseView;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class LibraryFragment extends Fragment {
    private MainActivity activity;
    private ArrayList<PDF.PdfBean> arrayList = new ArrayList<>();
    public static RecyclerView mRecyclerView;
    private LibraryAdapter goSectionAdapter;
    public static CstTabLayout tabLayout;
    private SearchView searchView;
    private ArrayList<Library> libraries;
    private PDFHelper pdfHelper;
    private static CardView empty;
    public static ProgressView progress;
    private DownloadReceiver mReceiver;
    private boolean initialStage = true;
    private View dialogView;
    private MaterialDialog audioDialog;
    private MediaPlayer mediaPlayer;
    private InterstitialAd mInterstitialAd;

    public LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    public LibraryFragment() {
    }

    public static Toolbar mActivityActionBarToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_go, container, false);
        mActivityActionBarToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mActivityActionBarToolbar);
        mActivityActionBarToolbar.setTitle("Libraries");
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


        Fonty.setFonts(tabLayout);

        getRequest();
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
                final PDF.PdfBean pdf = getPDF(tmpPdf.getPid());
                final int status = tmpPdf.getStatus();
                if(status!=Status.STATUS_DOWNLOADING){
                    pdfHelper.updatePDF(tmpPdf);
                }
                if (pdf.getPid() == tmpPdf.getPid()) {
                    if (status == Status.STATUS_LOADING) {
                        pdf.setStatus(Status.STATUS_LOADING);
                        goSectionAdapter.notifyItemChangedAtPosition(position);
                    } else if (status == Status.STATUS_DOWNLOADING) {
                        pdf.setStatus(Status.STATUS_DOWNLOADING);
                        Log.e(tmpPdf.getTitle(), String.valueOf(position));
                        pdf.setDownloadPerSize(tmpPdf.getDownloadPerSize());
                        pdf.setProgress(tmpPdf.getProgress());
                        goSectionAdapter.notifyItemChangedAtPosition(position);
                    } else if (status == Status.STATUS_NULL) {
                        pdf.setStatus(Status.STATUS_NULL);
                        goSectionAdapter.notifyItemChangedAtPosition(position);
                    } else if (status==Status.STATUS_DOWNLOADED){
                        pdf.setStatus(Status.STATUS_DOWNLOADED);
                        goSectionAdapter.notifyItemChangedAtPosition(position);
                    } else if (status==Status.STATUS_CONNECTED){
                        pdf.setStatus(Status.STATUS_CONNECTED);
                        goSectionAdapter.notifyItemChangedAtPosition(position);
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
        mReceiver = new LibraryFragment.DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        }
    }

    public void getRequest(){
        if(Utils.isConnected(getContext())) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            String url = "http://www.pdf.mumineendownloads.com/api/libr/getLibraries.php";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progress.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            parseData(response);
                            Utils.saveCurrentLibrary(response, getContext());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(stringRequest);
        } else {
            String response = Utils.getLastLibFile(getContext());
            parseData(response);
            Toasty.normal(getContext(),"No Internet Connection!").show();
        }
    }

    private void parseData(final String response) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    libraries = gson.fromJson(response, new TypeToken<ArrayList<Library>>() {
                    }.getType());
                    for(Library library : libraries){
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(Long.parseLong(library.getCreated()));
                        getPdfs(library.getData(),library.getName(),cal.getTime());
                    }
                    PDF.PdfBean ad = new PDF.PdfBean();
                    ad.setPid(-5);
                    ad.setDate(new Date(SystemClock.currentThreadTimeMillis()));
                    ad.setGo("ZeeAd");
                    if(Utils.isConnected(getContext())){
                        arrayList.add(ad);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Collections.sort(arrayList, new Comparator<PDF.PdfBean>() {
                                @Override
                                public int compare(PDF.PdfBean o1, PDF.PdfBean o2) {
                                    return o2.getDate().compareTo(o1.getDate());
                                }
                            });
                            goSectionAdapter = new LibraryAdapter(arrayList, getContext(), LibraryFragment.this);
                            mRecyclerView.setAdapter(goSectionAdapter);
                            progress.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }

                    });
                } catch (InstantiationException | NullPointerException ignored) {
                }
            }
        });
    }

    private void getPdfs(String data, String name, Date time) {
        try {
            Gson gson = new Gson();
            List<Integer> pids;
            pids = gson.fromJson(data, new TypeToken<List<Integer>>() {
            }.getType());

            for (Integer i : pids) {
                PDF.PdfBean p = pdfHelper.getPDF(i);
                p.setGo(name);
                p.setDate(time);
                arrayList.add(p);
            }
        } catch (NullPointerException ignored){}
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
                            Utils.showSnack(list.size() + " pdf added to "+text, "VIEW LIBRARY", new View.OnClickListener() {
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

    private void showNewDialog(final ArrayList<Integer> pids) {
        new MaterialDialog.Builder(getActivity())
                .title("Enter list name")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Example: Daris Hafti", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Utils.addSectionToList(getContext(),String.valueOf(input));
                        Utils.addToSpecificList(getContext(),pids, String.valueOf(input));
                        Utils.showSnack(pids.size() + " pdf added to "+input, "VIEW LIBRARY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.bottomNavigationView.setSelectedItemId(R.id.navigation_upload);
                            }
                        });
                    }
                }).show();
    }

    public void showOptionDialog(final Context context, final PDF.PdfBean pdfBean, final int position) {
        List<String> string = new ArrayList<>();
        if(pdfBean.getAudio()!=0){
            string.add("Play Audio");
        }
        if(pdfBean.getStatus()!=Status.STATUS_DOWNLOADED){
            string.add("Download");
        }
        string.add("Add to My Library");
        string.add("Report");
        new MaterialDialog.Builder(context)
                .title("Options")
                .items(string)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int pos, CharSequence text) {
                        if(text.equals("Add to My Library")){
                            showDialogListAdd(pdfBean);
                        }
                        else  if(text.equals("Report")){
                            reportApp(pdfBean);
                        }
                        else if(text.equals("Play Audio")){
                            playAudio(pdfBean);
                        }
                        else if(text.equals("Download")){
                            if (Utils.isConnected(context)) {
                                ArrayList<PDF.PdfBean> arrayList = new ArrayList<PDF.PdfBean>();
                                ArrayList<Integer> positionList = new ArrayList<Integer>();
                                arrayList.add(pdfBean);
                                positionList.add(position);
                                DownloadService.intentDownload(positionList, arrayList, context);
                                pdfBean.setStatus(PDF.STATUS_QUEUED);
                            } else {
                                Toasty.normal(context, "No Internet Connection").show();
                            }
                        }
                    }
                }).build().show();
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

    private class Player extends AsyncTask<String, Void, Boolean> {
        TextView total,current;
        private Handler mHandler = new Handler();
        PlayPauseView view;
        ProgressView progressView;
        SeekBar seekbar;
        int buffered = 0;
        int bufferPercent;

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean prepared;
            try {
                mediaPlayer.setDataSource(params[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        audioDialog.dismiss();
                        if(!mInterstitialAd.isLoaded()){
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }else {
                            mInterstitialAd.show();
                        }
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
                        Log.e(""+seekBar.getProgress()*1000, String.valueOf(buffered));
                        if(seekBar.getProgress()*1000>buffered*1000) {
                            showBuffering(true);
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
}
