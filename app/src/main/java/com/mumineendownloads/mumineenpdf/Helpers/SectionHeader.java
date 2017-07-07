package com.mumineendownloads.mumineenpdf.Helpers;

import com.intrusoft.sectionedrecyclerview.Section;
import com.mumineendownloads.mumineenpdf.Model.PDF;

import java.util.ArrayList;

public class SectionHeader implements Section<PDF.PdfBean> {

    ArrayList<PDF.PdfBean> childList;
    String sectionText;

    public SectionHeader(ArrayList<PDF.PdfBean> childList, String sectionText) {
        this.childList = childList;
        this.sectionText = sectionText;
    }

    @Override
    public ArrayList<PDF.PdfBean> getChildItems() {
        return childList;
    }

    public String getSectionText() {
        return sectionText;
    }
}
