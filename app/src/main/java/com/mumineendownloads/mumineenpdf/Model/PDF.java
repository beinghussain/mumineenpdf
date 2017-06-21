package com.mumineendownloads.mumineenpdf.Model;


import java.util.ArrayList;

public class PDF {

    private ArrayList<PdfBean> pdf;

    public ArrayList<PdfBean> getPdf() {
        return pdf;
    }

    public void setPdf(ArrayList<PdfBean> pdf) {
        this.pdf = pdf;
    }

    public static class PdfBean {
        private int id;
        private String title;
        private String album;
        private String source;
        private String size;
        private int pid;
        boolean downloading;
        long progress;
        int status;

        public PdfBean() {

        }

        public PdfBean(int id, String title, String album, String source, String size, int pid, int status) {
            this.id = id;
            this.title = title;
            this.album = album;
            this.source = source;
            this.size = size;
            this.pid = pid;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public void setDownloading(boolean downloading) {
            this.downloading = downloading;
        }

        boolean isDownloading() {
            return downloading;
        }

        public void setProgress(long progress) {
            this.progress = progress;
        }

        public long getProgress() {
            return progress;
        }

        public boolean getDownloading() {
            return downloading;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus(){
            return status;
        }
    }
}
