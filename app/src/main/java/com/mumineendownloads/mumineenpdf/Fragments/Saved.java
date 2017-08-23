package com.mumineendownloads.mumineenpdf.Fragments;


import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aspsine.multithreaddownload.DownloadManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
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
import com.ohoussein.playpause.PlayPauseView;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class Saved extends Fragment {
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
    private RelativeLayout noItemFound;
    private Toolbar mActivityActionBarToolbar;
    private MediaPlayer mediaPlayer;
    private boolean initialStage = true;
    private View dialogView;
    private MaterialDialog audioDialog;
    private InterstitialAd mInterstitialAd;

    public Saved() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_savedlist, container, false);
        Fonty.setFonts((ViewGroup) rootView);

        mPDFHelper= new PDFHelper(getActivity().getApplicationContext());
        mActivityActionBarToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mActivityActionBarToolbar);
        Fonty.setFonts(mActivityActionBarToolbar);
        mActivityActionBarToolbar.setTitle("Saved PDF Files");
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        progressView = (ProgressView) rootView.findViewById(R.id.progress);

        mRecyclerView.addItemDecoration(new CustomDivider(getContext()));
        mRecyclerView.getItemAnimator().setChangeDuration(0);

        noItemFound = (RelativeLayout) rootView.findViewById(R.id.noItemFound);


        SharedPreferences settings = getContext().getSharedPreferences("settings", 0);
        refresh(album);


        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate( R.menu.saved_menu, menu);
        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment selectedFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("what","saved");
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
                sAux = sAux +"http://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share this using"));
            } catch(Exception ignored) {
            }
        }
        return super.onOptionsItemSelected(item);

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
        mRecyclerView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    arrayList = Utils.getDownloadedFiles(getContext());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(arrayList.size()==0){
                                noItemFound.setVisibility(View.VISIBLE);
                                progressView.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.GONE);
                            }else {
                                noItemFound.setVisibility(View.GONE);
                                progressView.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                Collections.sort(arrayList, new Comparator<PDF.PdfBean>() {
                                    @Override
                                    public int compare(PDF.PdfBean o1, PDF.PdfBean o2) {
                                        return o1.getCat().compareTo(o2.getCat());
                                    }
                                });
                                mPDFAdapter = new SavedPDFAdapter(arrayList, getActivity().getApplicationContext(), Saved.this);
                                mRecyclerView.setAdapter(mPDFAdapter);
                            }

                        }

                    });
                }catch (NullPointerException ignored){

                }
            }
        });
    }

    public Saved newInstance() {
       return new Saved();
    }

    public void openDialog(final PDF.PdfBean pdf, final int position) {
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

        new MaterialDialog.Builder(getContext())
                .items(strings)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if(text.equals("Play Audio")){
                            playAudio(pdf);
                        }
                        else if(text.equals("Delete file")) {
                            dialog.dismiss();
                            delete(pdf, getContext());
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
                            Toasty.normal(getContext(),"Added " + list.size() + " PDF to " + text).show();
                        }
                    }
                });
        dialog.show();
    }

    private File getFile(int pid) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+pid+".pdf");
    }

    private void delete(final PDF.PdfBean pdf, final Context context) {
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
                            int position = arrayList.indexOf(pdf);
                            arrayList.remove(position);
                            pdf.setStatus(Status.STATUS_NULL);
                            mPDFAdapter.notifyItemRemovedAtPosition(position);
                            mPDFHelper.updatePDF(pdf);
                            Toasty.normal(context,"File Deleted Successfully").show();
                        }
                    }
                })
                .content("Do you really want to delete this file?").build().show();
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
                        Toasty.normal(getContext(),"Added " + pids.size() + " PDF to " + String.valueOf(input)).show();
                    }
                }).show();
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

}

