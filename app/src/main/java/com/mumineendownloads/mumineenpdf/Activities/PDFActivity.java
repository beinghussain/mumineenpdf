package com.mumineendownloads.mumineenpdf.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Helpers.CustomScrollHandle;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;

import java.io.File;

import es.dmoral.toasty.Toasty;

public class PDFActivity extends AppCompatActivity {

    private PDFView pdfView;
    private WebView webView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Fonty.setFonts(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        final PDF.PdfBean pdfBean = (PDF.PdfBean) intent.getSerializableExtra(DownloadService.EXTRA_APP_INFO);
        if(pdfBean!=null){
            getSupportActionBar().setTitle(pdfBean.getTitle());
            pdfView = (PDFView) findViewById(R.id.pdfView);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mumineen/" + pdfBean.getPid() + ".pdf");
            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .spacing(25)
                    .scrollHandle(new CustomScrollHandle(this))
                    .load();
            pdfView.useBestQuality(true);
        }


        if (Intent.ACTION_DEFAULT.equals(action) && type != null) {
            if ("application/pdf".equals(type)) {
                handlePdf(intent);
            }
        }

        Intent intent1 = getIntent();
        int mode = intent1.getIntExtra("mode",0);
        int id = intent1.getIntExtra("pid",0);

        if(id!=0) {
            String title = intent1.getStringExtra("title");
            getSupportActionBar().setTitle(title);
            pdfView = (PDFView) findViewById(R.id.pdfView);
            if (mode == 1) {
                String url = intent.getStringExtra("url");
                url = url.replace("http", "https");
                onlinePDF(url);
            } else {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mumineen/" + id + ".pdf");
                pdfView.fromFile(file)
                        .enableSwipe(true)
                        .spacing(25)
                        .scrollHandle(new CustomScrollHandle(this))
                        .load();
                pdfView.useBestQuality(true);
            }

        }
    }

    private void handlePdf(Intent intent) {
        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromUri(intent.getData())
                .enableSwipe(true)
                .spacing(25)
                .scrollHandle(new CustomScrollHandle(this))
                .load();
        pdfView.useBestQuality(true);
    }

    private void onlinePDF(String url) {
        webView.setVisibility(View.VISIBLE);
        final String doc = "http://docs.google.com/gview?embedded=true&url=" + url;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl("http://google.com");
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        if(id==R.id.go_to){
            pdfView.jumpTo(3);
        }
        return super.onOptionsItemSelected(item);
    }
}
