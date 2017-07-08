package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Fragments.Go;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;

import java.util.ArrayList;
import java.util.List;

import static com.mumineendownloads.mumineenpdf.R.id.sectionHeader;

/**
 * Created by Hussain on 7/8/2017.
 */


public class PDFAdapterCat extends BasePDFAdapter {

    private final Context context;
    private ArrayList<PDF.PdfBean> pdfBeanArrayList;
    private String splitBy;

    public PDFAdapterCat(ArrayList<PDF.PdfBean> itemList, String splitBy, Context context) {
        super(itemList);
        this.pdfBeanArrayList = itemList;
        this.splitBy = splitBy;
        this.context = context;
    }

    @Override
    public boolean onPlaceSubheaderBetweenItems(int position) {
        final PDF.PdfBean pdf = pdfBeanArrayList.get(position);
        final PDF.PdfBean nextPdf = pdfBeanArrayList.get(position + 1);
        return !pdf.getCat().equals(nextPdf.getCat());
    }

    @Override
    public void onBindItemViewHolder(PDFViewHolder holder, int itemPosition) {
        final PDF.PdfBean pdf = pdfBeanArrayList.get(itemPosition);
    }

    @Override
    public void onBindSubheaderViewHolder(SubHeaderHolder subheaderHolder, int nextItemPosition) {
        final PDF.PdfBean nextPDF = pdfBeanArrayList.get(nextItemPosition);
    }
}
