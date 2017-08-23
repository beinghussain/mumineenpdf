package com.mumineendownloads.mumineenpdf.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Adapters.BasePDFAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapter;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapterCat;
import com.mumineendownloads.mumineenpdf.Helpers.CustomDivider;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;
import com.ohoussein.playpause.PlayPauseView;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.mumineendownloads.mumineenpdf.Fragments.Go.mRecyclerView;

public class SearchFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    private String what;
    private PDFAdapter pdfAdapter;
    private RelativeLayout noItemFound;
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
    public ArrayList<PDF.PdfBean> downloadingList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private MaterialDialog audioDialog;
    private boolean initialStage = true;
    private View dialogView;
    private InterstitialAd mInterstitialAd;


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
                    mPDFHelper.updatePDF(tmpPdf);
                }
                if (pdf.getPid() == tmpPdf.getPid()) {
                    if (status == Status.STATUS_LOADING) {
                        pdf.setStatus(Status.STATUS_LOADING);
                        pdfAdapter.notifyItemChanged(position);
                    } else if (status == Status.STATUS_DOWNLOADING) {
                        pdf.setStatus(Status.STATUS_DOWNLOADING);
                        pdf.setDownloadPerSize(tmpPdf.getDownloadPerSize());
                        pdf.setProgress(tmpPdf.getProgress());
                        pdfAdapter.notifyDataSetChanged();
                    } else if (status == Status.STATUS_NULL) {
                        pdf.setStatus(Status.STATUS_NULL);
                        pdfAdapter.notifyItemChanged(position);
                    } else if (status==Status.STATUS_DOWNLOADED){
                        pdf.setStatus(Status.STATUS_DOWNLOADED);
                        pdfAdapter.notifyItemChanged(position);
                    } else if (status==Status.STATUS_CONNECTED){
                        pdf.setStatus(Status.STATUS_CONNECTED);
                        pdfAdapter.notifyItemChanged(position);
                    }
                    if(searching){
                        pdfAdapter.notifyDataSetChanged();
                    }
                }
            }
    }

    private PDF.PdfBean getPDF(int pid){
        for(PDF.PdfBean pdfBean : arrayList){
            if(pdfBean.getPid()==pid){
                return pdfBean;
            }
        }
        return arrayList.get(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        register();
    }

    private void register() {
        mReceiver = new SearchFragment.DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void unRegister() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        }
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

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        MaterialSearchBar searchBar = (MaterialSearchBar) view.findViewById(R.id.searchBar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.searchRecycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new CustomDivider(getContext()));
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        noItemFound = (RelativeLayout) view.findViewById(R.id.noItemFound);
        searchBar.enableSearch();
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = s.toString();
                newText=newText.toLowerCase();
                newlist=new ArrayList<>();
                for(PDF.PdfBean name:arrayList) {
                    String getName=name.getTitle().toLowerCase();
                    if(getName.contains(newText)){
                        newlist.add(name);
                    }
                    if(newlist.size()==0){
                        mRecyclerView.setVisibility(View.GONE);
                        noItemFound.setVisibility(View.VISIBLE);
                    }else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        noItemFound.setVisibility(View.GONE);
                    }
                }
                pdfAdapter.filter(newlist);
            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    FragmentManager transaction = getActivity().getSupportFragmentManager();
                    transaction.popBackStack();
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });
        Fonty.setFonts((ViewGroup) view);
        mPDFHelper = new PDFHelper(getContext());

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if(what.equals("saved")){
                        arrayList  = Utils.getDownloadedFiles(getContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                pdfAdapter = new PDFAdapter(arrayList,getContext(),SearchFragment.this);
                                mRecyclerView.setAdapter(pdfAdapter);
                            }

                        });
                    }else {
                        arrayList = mPDFHelper.getAllPDFS("all");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                pdfAdapter = new PDFAdapter(arrayList, getContext(), SearchFragment.this);
                                mRecyclerView.setAdapter(pdfAdapter);
                            }

                        });
                    }
                } catch (NullPointerException ignored) {

                }
            }
        });

        return view;
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
                    .items(strings)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            if(text.equals("Play Audio")){
                                playAudio(pdf);
                            }
                            if (text.equals("Download")) {
                                if (Utils.isConnected(context)) {
                                    pdf.setStatus(PDF.STATUS_QUEUED);
                                    pdfAdapter.notifyItemChanged(position);
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

                            } if(text.equals("Add to My LibraryFragment")){
                                showDialogListAdd(pdf);
                            }
                            else if(text.equals("Delete file")) {
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
        }
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            this.what = bundle.getString("what");
        }
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
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
                            pdfAdapter.notifyItemChanged(position);
                            mPDFHelper.updatePDF(pdf);
                            Toasty.normal(context,"File Deleted Successfully").show();
                        }
                    }
                })
                .content("Do you really want to delete this file?").build().show();
    }

    private void startDownloading() {
        DownloadService.intentDownload(positionList, downloadingList, getContext());
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

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
                    }
                }).show();
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
                        }
                    }
                });
        dialog.show();
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

    @Override
    public void onButtonClicked(int buttonCode) {
         Log.e("ButtonCode", String.valueOf(buttonCode));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
    }
}
