package com.mumineendownloads.mumineenpdf.Fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.mumineendownloads.mumineenpdf.R;

import java.io.File;

public class PDFDialogFragment extends DialogFragment {
    private String mNum;
    private PDFView pdfView;


    public PDFDialogFragment() {
    }

    public static PDFDialogFragment newInstance(String title) {
        PDFDialogFragment frag = new PDFDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getString("title");
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_NoActionBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pdf_dialog, container, false);

        pdfView = (PDFView) v.findViewById(R.id.pdfView);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+mNum+".pdf");
        pdfView.fromFile(file)
                .enableSwipe(true)
                .spacing(25)
                .load();
        pdfView.useBestQuality(true);

        return v;
    }
}
