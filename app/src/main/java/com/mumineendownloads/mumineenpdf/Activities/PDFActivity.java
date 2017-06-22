package com.mumineendownloads.mumineenpdf.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.R;

import java.io.File;

public class PDFActivity extends AppCompatActivity {

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String value = intent.getStringExtra("title");
        getSupportActionBar().setTitle(value);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Fonty.setFonts(toolbar);



        pdfView = (PDFView) findViewById(R.id.pdfView);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+value+".pdf");
        pdfView.fromFile(file)
                .enableSwipe(true)
                .spacing(25)
                 .scrollHandle(new DefaultScrollHandle(this))
                .load();
        pdfView.useBestQuality(true);

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
