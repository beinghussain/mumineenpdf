package com.mumineendownloads.mumineenpdf.Model;


import java.io.Serializable;

public class SelectFile implements Serializable{
    public static final String FILE = "com.mumineendownloads.mumineenpdf.file";
    String filename;
    String fileUrl;
    long fileSize;

    public SelectFile() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
