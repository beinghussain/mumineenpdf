package com.mumineendownloads.mumineenpdf.Model;

public class PDFDownload {
    private int pid;
    private int status;


    public PDFDownload(){

    }

    public PDFDownload(int pid, int status) {
        this.pid = pid;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
