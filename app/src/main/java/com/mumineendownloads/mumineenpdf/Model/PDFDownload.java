package com.mumineendownloads.mumineenpdf.Model;

public class PDFDownload {
    PDF.PdfBean pdfBean;
    long progress;
    long finish;
    long total;
    int id;

    public PDFDownload(PDF.PdfBean pdfBean, long progress, long finish, long total, int id) {
        this.progress = progress;
        this.id = id;
    }

    public PDF.PdfBean getPdfBean() {
        return pdfBean;
    }

    public void setPdfBean(PDF.PdfBean pdfBean) {
        this.pdfBean = pdfBean;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getFinish() {
        return finish;
    }

    public void setFinish(long finish) {
        this.finish = finish;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
