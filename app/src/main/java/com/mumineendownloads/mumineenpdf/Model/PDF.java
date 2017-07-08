package com.mumineendownloads.mumineenpdf.Model;


import java.io.Serializable;
import java.util.ArrayList;

public class PDF {
    public static final int STATUS_NOT_DOWNLOAD = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECT_ERROR = 2;
    public static final int STATUS_DOWNLOADING = 3;
    public static final int STATUS_PAUSED = 4;
    public static final int STATUS_DOWNLOAD_ERROR = 5;
    public static final int STATUS_COMPLETE = 6;
    public static final int STATUS_INSTALLED = 7;
    public static final int STATUS_QUEUED = 8;


    private ArrayList<PdfBean> pdf;

    public ArrayList<PdfBean> getPdf() {
        return pdf;
    }

    public void setPdf(ArrayList<PdfBean> pdf) {
        this.pdf = pdf;
    }


    public static class PdfBean implements Serializable {
        private int id;
        private String title;
        private String album;
        private String source;
        private String size;
        private int pid;
        boolean downloading;
        int progress;
        int status;
        private int pageCount;
        private boolean isSearchingMode;
        private boolean selected;
        private String go;
        private String downloadPerSize;
        private String cat;

        public String getStatusText() {
            switch (status) {
                case STATUS_NOT_DOWNLOAD:
                    return "Not Download";
                case STATUS_CONNECTING:
                    return "Connecting";
                case STATUS_CONNECT_ERROR:
                    return "Connect Error";
                case STATUS_DOWNLOADING:
                    return "Downloading";
                case STATUS_PAUSED:
                    return "Pause";
                case STATUS_DOWNLOAD_ERROR:
                    return "Download Error";
                case STATUS_COMPLETE:
                    return "Complete";
                case STATUS_INSTALLED:
                    return "Installed";
                default:
                    return "Not Download";
            }
        }

        public PdfBean() {

        }

        public String getDownloadPerSize() {
            return downloadPerSize;
        }

        public void setDownloadPerSize(String downloadPerSize) {
            this.downloadPerSize = downloadPerSize;
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

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getProgress() {
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

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public boolean isSearchingMode() {
            return isSearchingMode;
        }

        public void setSearchingMode(boolean isSearchingMode){
            this.isSearchingMode = isSearchingMode;
        }

        public boolean getSelected(){
           return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public void setGo(String go){
            this.go = go;
        }

        public String getGo() {
            return go;
        }

        public String getCat() {
            return cat;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }
    }
}
