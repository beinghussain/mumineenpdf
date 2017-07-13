package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDFReq;
import com.mumineendownloads.mumineenpdf.Model.User;
import com.mumineendownloads.mumineenpdf.R;

import java.util.ArrayList;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ReqViewHolder> {

    ArrayList<PDFReq.Request> mRequestArrayList;
    Context mCtx;

    public RequestAdapter(ArrayList<PDFReq.Request> requests,Context context){
        this.mRequestArrayList = requests;
        this.mCtx = context;
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
        }
    }

    @Override
    public void onBindViewHolder(ReqViewHolder holder, int position) {
        final User user = Utils.getUser(mCtx);
        final PDFReq.Request request = mRequestArrayList.get(position);
        holder.txtMessage.setText(request.getRequest());
        boolean isMe = user.getUserId()==Integer.parseInt(request.getUser_id());
        if(user.getUserId()==Integer.parseInt(request.getUser_id())) {
            holder.status.setVisibility(View.VISIBLE);
            setStatus(Integer.parseInt(request.getStatus()), holder);
        }else {
            holder.status.setVisibility(View.GONE);
        }
        setAlignment(holder,isMe);

        holder.contentWithBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getUserId() != Integer.parseInt(request.getUser_id())) {
                    new MaterialDialog.Builder(v.getContext())
                            .title("Response to this request")
                            .positiveText("Upload this file")
                            .negativeText("Cancel")
                            .neutralText("Report")
                            .content("If you have the requested file. You can upload it here. Once approve it will be updated in the app.")
                            .build().show();
                } else {
                    String cont = getStatusString(Integer.parseInt(request.getStatus()));
                    new MaterialDialog.Builder(v.getContext()).content(cont).positiveText("OK").build().show();
                }
            }
        });
    }

    private String getStatusString(int status) {
        String s = "";
        if(status==PDFReq.APPROVE){
            s= "Your request is been shown in the app to all the users";
        }
        if(status==PDFReq.PENDING){
            s= "Your request is pending";
        }
        if(status==PDFReq.REJECT){
            s= "Your request has been rejected due to some reason. Please type in proper name of file you want!";
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
        ReqViewHolder(View v) {
            super(v);
            txtMessage = (TextView) v.findViewById(R.id.txtMessage);
            content = (LinearLayout) v.findViewById(R.id.content);
            contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
            time = (TextView) v.findViewById(R.id.time);
            status = (TextView) v.findViewById(R.id.status);
        }
    }
}
