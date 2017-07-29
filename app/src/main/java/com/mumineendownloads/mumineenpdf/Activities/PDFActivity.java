package com.mumineendownloads.mumineenpdf.Activities;//package com.mumineendownloads.mumineenpdf.Activities;
//
//import android.app.ActionBar;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.graphics.Canvas;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.annotation.RequiresApi;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.animation.LinearInterpolator;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//import com.github.barteksc.pdfviewer.PDFView;
//import com.github.barteksc.pdfviewer.listener.OnDrawListener;
//import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
//import com.github.barteksc.pdfviewer.listener.OnRenderListener;
//import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
//import com.marcinorlowski.fonty.Fonty;
//import com.mumineendownloads.mumineenpdf.Helpers.CustomScrollHandle;
//import com.mumineendownloads.mumineenpdf.Helpers.Utils;
//import com.mumineendownloads.mumineenpdf.Model.PDF;
//import com.mumineendownloads.mumineenpdf.R;
//import com.mumineendownloads.mumineenpdf.Service.DownloadService;
//
//import org.androidannotations.annotations.EActivity;
//import org.androidannotations.annotations.NonConfigurationInstance;
//
//import java.io.File;
//
//import es.dmoral.toasty.Toasty;
//@EActivity(R.layout.activity_pdf)
//public class PDFActivity extends AppCompatActivity {
//
//    private PDFView pdfView;
//    private InterstitialAd mInterstitialAd;
//
//    @NonConfigurationInstance
//    Integer pageNumber = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        setContentView(R.layout.activity_pdf);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Fonty.setFonts(toolbar);
//        Intent intent = getIntent();
//        String action = intent.getAction();
//        String type = intent.getType();
//        pdfView = (PDFView) findViewById(R.id.pdfView);
//        pdfView.documentFitsView();
//        pdfView.setMinZoom(3f);
//        pdfView.enableAnnotationRendering(true);
//
//        if (Intent.ACTION_DEFAULT.equals(action) && type != null) {
//            if ("application/pdf".equals(type)) {
//                handlePdf(intent);
//            }
//        }
//
//        Intent intent1 = getIntent();
//        int mode = intent1.getIntExtra("mode",0);
//        int id = intent1.getIntExtra("pid",0);
//
//        if(id!=0) {
//            String title = intent1.getStringExtra("title");
//            getSupportActionBar().setTitle(title);
//            if (mode != 1) {
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mumineen/" + id + ".pdf");
//                pdfView.fromFile(file)
//                        .enableSwipe(true)
//                        .enableAnnotationRendering(true)
//                        .onPageChange(new OnPageChangeListener() {
//                            @Override
//                            public void onPageChanged(int page, int pageCount) {
//                            }
//                        })
//                        .spacing(2)
//                        .onRender(new OnRenderListener() {
//                            @Override
//                            public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
//                                  pdfView.fitToWidth();
//                            }
//                        })
//                        .scrollHandle(new DefaultScrollHandle(getApplicationContext()))
//                        .load();
//            }
//        }
//    }
//
//
//
//    private void handlePdf(Intent intent) {
//        pdfView.fromUri(intent.getData())
//                .enableSwipe(true)
//                .onRender(new OnRenderListener() {
//                    @Override
//                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
//                        pdfView.fitToWidth();
//                    }
//                })
//                .scrollHandle(new CustomScrollHandle(this))
//                .load();
//        pdfView.useBestQuality(true);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.pdf_menu, menu);
//        return true;
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        mInterstitialAd.show();
//        finish();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id==android.R.id.home){
//            finish();
//        }
//        if(id==R.id.go_to){
//            pdfView.jumpTo(3);
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    public void toggleFullScreen(boolean isFullscreen){
//        View decorView = getWindow().getDecorView();
//        if (getSupportActionBar().isShowing()) {
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//            getSupportActionBar().hide();
//        } else {
//            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
//            decorView.setSystemUiVisibility(uiOptions);
//            getSupportActionBar().show();
//        }
//    }
//}

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Helpers.CustomScrollHandle;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import es.dmoral.toasty.Toasty;


@EActivity(R.layout.activity_pdf)
@OptionsMenu(R.menu.pdf_menu)
public class PDFActivity extends AppCompatActivity implements OnPageChangeListener {

    private static final String TAG = PDFActivity.class.getSimpleName();
    private InterstitialAd mInterstitialAd;

    @ViewById
    PDFView pdfView;

    @ViewById
    Toolbar toolbar;

    @ViewById
    AppBarLayout appBarPDf;

    @NonConfigurationInstance
    Integer pageNumber = 0;

    @AfterViews
    void afterViews() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String title = getIntent().getStringExtra("title");

        setSupportActionBar(toolbar);
        setTitle(title);
        Fonty.setFonts(toolbar);
        appBarPDf.bringToFront();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        String action = getIntent().getAction();
        Intent intent = getIntent();
        String type = intent.getType();
        if (Intent.ACTION_DEFAULT.equals(action) && type != null) {
            if ("application/pdf".equals(type)) {
                displayFromContent(intent.getData());
            }
        }else if(intent.getIntExtra("pid",-1)!=0){
            int id = intent.getIntExtra("pid",0);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mumineen/" + id + ".pdf");
            displayFromFile(file);
        }


    }

    private void displayFromFile(File file) {
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        pdfView.fitToWidth(pageNumber);
                    }
                })
                .scrollHandle(new CustomScrollHandle(this, (PDFActivity_) PDFActivity.this))
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
                .scrollHandle(new CustomScrollHandle(this, (PDFActivity_) PDFActivity.this))
                .load();
        setTitle(new File(getRealPathFromURI(getApplicationContext(),uri)).getName());
        pdfView.useBestQuality(true);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
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
        Toasty.normal(getApplicationContext(),"Playing").show();
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
    }

    public void showActionBar(){
        ActionBar ab = getSupportActionBar();
        if (ab != null && !ab.isShowing()) {
            ab.show();
            if(appBarPDf != null) {
                appBarPDf.animate().translationY(0).setDuration(100L).start();
            }
        }
    }
}