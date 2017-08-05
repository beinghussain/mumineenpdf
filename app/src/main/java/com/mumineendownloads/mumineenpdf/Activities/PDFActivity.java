package com.mumineendownloads.mumineenpdf.Activities;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.ohoussein.playpause.PlayPauseView;
import com.rey.material.widget.ProgressView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;

import es.dmoral.toasty.Toasty;
@EActivity(R.layout.activity_pdf)
@OptionsMenu(R.menu.pdf_menu)
public class PDFActivity extends AppCompatActivity implements OnPageChangeListener {

    private static final String TAG = PDFActivity.class.getSimpleName();
    private InterstitialAd mInterstitialAd;
    PDF.PdfBean pdfBean;

    @ViewById
    PDFView pdfView;

    @ViewById
    Toolbar toolbar;

    @ViewById
    AppBarLayout appBarPDf;

    @ViewById
    LinearLayout player;

    @NonConfigurationInstance
    Integer pageNumber = 0;
    private MenuItem menuItem;
    private MediaPlayer mediaPlayer;
    private boolean initialStage = true;
    private boolean isPlaying= false;

    @AfterViews
    void afterViews() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String title = getIntent().getStringExtra("title");

        setSupportActionBar(toolbar);
        setTitle(title);
        Fonty.setFonts(toolbar);
        player.bringToFront();
        appBarPDf.bringToFront();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSupportActionBar().isShowing()){
                    hideActionBar();
                }else {
                    showActionBar();
                }
            }
        });

        String action = getIntent().getAction();
        Intent intent = getIntent();
        String type = intent.getType();
        if (Intent.ACTION_DEFAULT.equals(action) && type != null) {
            if ("application/pdf".equals(type)) {
                displayFromContent(intent.getData());
            }
        }else if(intent.getIntExtra("pid",-1)!=0){
            int id = intent.getIntExtra("pid",0);
            PDFHelper pdfHelper = new PDFHelper(getApplicationContext());
            pdfBean = pdfHelper.getPDF(id);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mumineen/" + id + ".pdf");
            displayFromFile(file);
        }

        if(pdfBean.getAudio()!=1){
            player.setVisibility(View.GONE);
        }
    }

    private void displayFromFile(File file) {
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        pdfView.fitToWidth(pageNumber);
                    }
                })
                .spacing(2)
                .load();
    }

    private void displayFromContent(Uri uri){
        pdfView.fromUri(uri)
                .enableSwipe(true)
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        pdfView.fitToWidth();
                    }
                })
                .load();
        setTitle(new File(getRealPathFromURI(getApplicationContext(),uri)).getName());
        pdfView.useBestQuality(true);
        pdfView.enableSwipe(true);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = { MediaStore.Images.Media.DATA };
//            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
////            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
        return "";
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        showToast();
        pageNumber = page;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.play);
        try {
            if (pdfBean.getAudio() == 1) {
                menuItem.setVisible(true);
            } else {
                menuItem.setVisible(false);
            }
        }catch (NullPointerException ignored){
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void showToast() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("PAGE "+(pdfView.getCurrentPage()+ 1) +" / "+pdfView.getPageCount());

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM,0, 15);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1200 );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
          //  mInterstitialAd.show();
            if(mediaPlayer!=null){
                mediaPlayer.reset();
            }
        }catch (Fragment.InstantiationException ignored){

        }
    }

    @OptionsItem(R.id.play)
    void playAudioClick(){
        int pid = getIntent().getIntExtra("pid",0);
        PDFHelper helper = new PDFHelper(getApplicationContext());
        PDF.PdfBean pdfBean = helper.getPDF(pid);
        if(pdfBean.getAudio()==1){
            playAudio();
        }
    }

    private void playAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                menuItem.setIcon(R.drawable.ic_play_circle_outline_black_24dp);
            } else {
                mediaPlayer.start();
                menuItem.setIcon(R.drawable.ic_pause_circle_outline_black_24dp);
            }
        }
        else if (initialStage) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            new Player()
                    .execute("http://pdf.mumineendownloads.com/audio.php?id=" + pdfBean.getPid());
        }
    }

    private class Player extends AsyncTask<String, Void, Boolean> {
        TextView total,current;
        private Handler mHandler = new Handler();
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
                        initialStage = true;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        isPlaying = false;
                        mediaPlayer = null;
                        player.setVisibility(View.GONE);
                        menuItem.setIcon(R.drawable.ic_play_circle_outline_black_24dp);
                    }
                });
                mediaPlayer.prepare();
                runOnUiThread(new Runnable() {
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
                isPlaying = true;
                mediaPlayer.start();
                player.setVisibility(View.VISIBLE);
                total.setText(Utils.timeFormat(mediaPlayer.getDuration() / 1000));
                seekbar.setMax(mediaPlayer.getDuration() / 1000);
                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(progress>0 && progress<=5){
                            showBuffering(false);
                            menuItem.setIcon(R.drawable.ic_pause_circle_outline_black_24dp);
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

            }catch (NullPointerException ignored){
                Toasty.normal(getApplicationContext(), "Some error occured").show();
            }
        }

        Player() {
            total = (TextView) findViewById(R.id.total);
            current = (TextView)findViewById(R.id.current);
            seekbar = (SeekBar) findViewById(R.id.seekBar);
        }

        void showBuffering(boolean progressing){
            if(progressing) {
                menuItem.setIcon(R.drawable.ic_hourglass_full_black_24dp);
            }else {
                if(mediaPlayer.isPlaying()){
                    menuItem.setIcon(R.drawable.ic_pause_circle_outline_black_24dp);
                }else {
                    menuItem.setIcon(R.drawable.ic_play_circle_outline_black_24dp);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showBuffering(true);

        }
    }

    public void hideActionBar(){
        final ActionBar ab = getSupportActionBar();
        if (ab != null && ab.isShowing()) {
            if(appBarPDf != null) {
                appBarPDf.animate().translationY(-appBarPDf.getHeight()).setDuration(100L)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                ab.hide();
                            }
                        }).start();
            } else {
                ab.hide();
            }
        }

        if(isPlaying) {
            player.animate().translationY(player.getHeight()).setDuration(100L)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            ab.hide();
                        }
                    }).start();
        }
    }

    public void showActionBar(){
        ActionBar ab = getSupportActionBar();
        if (ab != null && !ab.isShowing()) {
            ab.show();
            if(appBarPDf != null) {
                appBarPDf.animate().translationY(0).setDuration(100L).start();
            }
        }

        if(isPlaying) {
            player.animate().translationY(0).setDuration(100L).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(mediaPlayer!=null){
//            mediaPlayer.stop();
//            mediaPlayer = null;
//        }
    }
}