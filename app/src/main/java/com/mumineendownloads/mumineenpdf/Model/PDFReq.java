package com.mumineendownloads.mumineenpdf.Model;

import java.util.Date;
import java.util.List;


public class PDFReq {

    public static final int PENDING = 0;
    public static final int APPROVE = 1;
    public static final int REJECT = -1;
    public static final int TYPE_PDF = 1;

    private List<Request> requests;

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public static class Request {

        private String id;
        private String user_id;
        private String user_name;
        private String request;
        private String status;
        private long date;
        private int type;
        private int pid;
        private int response;

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getRequest() {
            return request;
        }

        public void setRequest(String request) {
            this.request = request;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public int getResponse() {
            return response;
        }
    }
}
