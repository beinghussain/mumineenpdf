package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Fragments.RequestPage;
import com.mumineendownloads.mumineenpdf.Fragments.SelectFileFragment;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.Model.PDFReq;
import com.mumineendownloads.mumineenpdf.Model.SelectFile;
import com.mumineendownloads.mumineenpdf.Model.User;
import com.mumineendownloads.mumineenpdf.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ReqViewHolder> {

    private final RequestPage requestPage;
    ArrayList<PDFReq.Request> mRequestArrayList;
    Context mCtx;

    public RequestAdapter(ArrayList<PDFReq.Request> requests, Context context, RequestPage requestPage){
        this.mRequestArrayList = requests;
        this.mCtx = context;
        this.requestPage = requestPage;
    }

    @Override
    public ReqViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item, parent, false);

        Fonty.setFonts((ViewGroup) itemView);
        return new ReqViewHolder(itemView);
    }

    private void setAlignment(ReqViewHolder holder, boolean isMe) {
        if (isMe) {
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);
            holder.contentWithBG.setBackground(ContextCompat.getDrawable(mCtx,R.drawable.card_me));
            RelativeLayout.LayoutParams pdfParams = (RelativeLayout.LayoutParams) holder.pdfView.getLayoutParams();
            pdfParams.addRule(RelativeLayout.LEFT_OF,R.id.content);
            holder.pdfView.setLayoutParams(pdfParams);
            layoutParams.gravity = Gravity.RIGHT;
        } else {
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);
            holder.contentWithBG.setBackground(ContextCompat.getDrawable(mCtx,R.drawable.card_other));

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);
            layoutParams.gravity = Gravity.LEFT;
            RelativeLayout.LayoutParams pdfParams = (RelativeLayout.LayoutParams) holder.pdfView.getLayoutParams();
            pdfParams.addRule(RelativeLayout.RIGHT_OF,R.id.content);
            holder.pdfView.setLayoutParams(pdfParams);
        }
    }

    @Override
    public void onBindViewHolder(final ReqViewHolder holder, final int position) {
        final User user = Utils.getUser(mCtx);
        final PDFReq.Request request = mRequestArrayList.get(position);
        holder.txtMessage.setText(request.getRequest());
        boolean isMe = user.getUserId()==Integer.parseInt(request.getUser_id());
        if(user.getUserId()==Integer.parseInt(request.getUser_id())) {
            holder.status.setVisibility(View.VISIBLE);
            setStatus(Integer.parseInt(request.getStatus()), holder);
            holder.username.setVisibility(View.GONE);
        }else {
            holder.status.setVisibility(View.GONE);
            holder.username.setText("by "+request.getUser_name());
            holder.username.setVisibility(View.VISIBLE);
        }


        setAlignment(holder,isMe);
        if(request.getType()== PDFReq.TYPE_PDF){
            Log.e("TYPE", String.valueOf(request.getType()));
            holder.contentWithBG.setBackground(ContextCompat.getDrawable(mCtx,R.drawable.card_pdf));
            holder.txtMessage.setTextColor(ContextCompat.getColor(mCtx,android.R.color.white));
            holder.time.setTextColor(ContextCompat.getColor(mCtx,android.R.color.white));
            holder.username.setTextColor(ContextCompat.getColor(mCtx,android.R.color.white));
            holder.pdfView.setVisibility(View.VISIBLE);
        }else {
            holder.contentWithBG.setBackground(ContextCompat.getDrawable(mCtx,R.drawable.card_me));
            holder.time.setTextColor(ContextCompat.getColor(mCtx,R.color.cardview_shadow_start_color));
            holder.username.setTextColor(ContextCompat.getColor(mCtx,R.color.cardview_shadow_start_color  ));
            holder.txtMessage.setTextColor(ContextCompat.getColor(mCtx,android.R.color.black  ));
            holder.pdfView.setVisibility(View.GONE);
        }
        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        String ago = prettyTime.format(new Date(request.getDate()));
        holder.time.setText(ago);
        

        holder.contentWithBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getUserId() != Integer.parseInt(request.getUser_id())) {
                    if(request.getType()==PDFReq.TYPE_PDF){
                        int pid = request.getPid();
                        PDFHelper helper = new PDFHelper(mCtx);
                        PDF.PdfBean pdfBean = helper.getPDF(pid);
                        new MaterialDialog.Builder(v.getContext())
                                .title("Download "+ pdfBean.getTitle()+" ?")
                                .positiveText("Download")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        FragmentManager fm = requestPage.getChildFragmentManager();
                                        SelectFileFragment selectFileFragment = SelectFileFragment.newInstance(requestPage);
                                        selectFileFragment.show(fm, "");

                                    }
                                })
                                .negativeText("Cancel")
                                .content("This file was been uploaded by " + user.getName() + " and is been update in the app. You can download it from here as well")
                                .build().show();
                    }else {
                        if (request.getResponse() != 0) {
                            int pi = request.getResponse();
                            int pos = findPos(pi);
                            requestPage.mRecyclerView.scrollToPosition(pos);
                        } else {
                            new MaterialDialog.Builder(v.getContext())
                                    .title("Response to this request")
                                    .positiveText("Upload this file")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            FragmentManager fm = requestPage.getChildFragmentManager();
                                            SelectFileFragment selectFileFragment = SelectFileFragment.newInstance(requestPage);
                                            selectFileFragment.show(fm, "");

                                        }
                                    })
                                    .negativeText("Cancel")
                                    .neutralText("Report")
                                    .content("If you have the requested file. You can upload it here. Once approve it will be updated in the app.")
                                    .build().show();
                        }
                    }
                } else {
                    if(request.getType()==PDFReq.TYPE_PDF){
                        if(Integer.parseInt(request.getStatus())!=PDFReq.APPROVE) {
                            String cont = getStatusStringUploaded(Integer.parseInt(request.getStatus()));
                            new MaterialDialog.Builder(v.getContext()).title("Request" +
                                    " Status").content(cont).positiveText("OK").negativeText("Delete uploaded file")
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            deleteRequest(request, position);
                                        }
                                    }).build().show();
                        }else {
                            String cont = getStatusStringUploaded(Integer.parseInt(request.getStatus()));
                            new MaterialDialog.Builder(v.getContext()).title("Request" +
                                    " Status").content("Your file is been uploaded and updated in the app.").positiveText("OK").build().show();
                        }
                    }else {
                        String cont = getStatusString(Integer.parseInt(request.getStatus()));
                        new MaterialDialog.Builder(v.getContext()).title("Request" +
                                " Status").content(cont).positiveText("OK").negativeText("Delete this request")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        deleteRequest(request, position);
                                    }
                                }).build().show();
                    }
                }
            }
        });
    }

    private int findPos(int pi) {
        for(int i = 0; i<mRequestArrayList.size(); i++){
            if(Integer.parseInt(mRequestArrayList.get(i).getId())==pi){
                return i;
            }
        }
        return 0;
    }

    private String getStatusStringUploaded(int i) {
        String s = "";
        if(i==PDFReq.APPROVE){
            s= "Your uploaded file is being approved and update in the app. All the users can download the file you uploaded";
        }
        if(i==PDFReq.PENDING){
            s= "The file uploaded is pending to be reviewed by admin. Once approved will be updated in the app.";
        }
        if(i==PDFReq.REJECT){
            s= "Your file is being rejected. Please don't upload invalid files.";
        }
        return s;

    }

    private void deleteRequest(final PDFReq.Request request, final int position) {
        final RequestQueue queue = Volley.newRequestQueue(mCtx);
        final String url = "http://mumineendownloads.com/app/deleteRequest.php";
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")) {
                            mRequestArrayList.remove(position);
                            notifyItemRemoved(position);
                            Toasty.normal(mCtx,"Request deleted successfully").show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("request_id", request.getId());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private String getStatusString(int status) {
        String s = "";
        if(status==PDFReq.APPROVE){
            s= "Your request is been shown in the app to all the users.";
        }
        if(status==PDFReq.PENDING){
            s= "Your request is pending. Your request is not yet shown in the app to all users.";
        }
        if(status==PDFReq.REJECT){
            s= "Your request has been rejected due to some reason. Please type in proper name of file you want.";
        }
        return s;
    }

    private void setStatus(int status, ReqViewHolder holder) {
        switch (status){
                case PDFReq.PENDING:
                    holder.status.setTextColor(Color.parseColor("#f1c40f"));
                    break;
                case PDFReq.APPROVE:
                    holder.status.setTextColor(Color.parseColor("#2ecc71"));
                    break;
                case PDFReq.REJECT:
                    holder.status.setTextColor(Color.parseColor("#e74c3c"));
                    break;
        }
    }

    @Override
    public int getItemCount() {
        return mRequestArrayList.size();
    }

    class ReqViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMessage, time, status;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public TextView username;
        public ImageView pdfView;

        ReqViewHolder(View v) {
            super(v);
            username = (TextView) v.findViewById(R.id.username);
            txtMessage = (TextView) v.findViewById(R.id.txtMessage);
            content = (LinearLayout) v.findViewById(R.id.content);
            contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
            time = (TextView) v.findViewById(R.id.time);
            status = (TextView) v.findViewById(R.id.status);
            pdfView = (ImageView) v.findViewById(R.id.pdfView);
        }
    }
}
