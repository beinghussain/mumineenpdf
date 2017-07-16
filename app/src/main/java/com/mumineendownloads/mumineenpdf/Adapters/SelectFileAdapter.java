package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mumineendownloads.mumineenpdf.Fragments.SelectFileFragment;
import com.mumineendownloads.mumineenpdf.Model.SelectFile;
import com.mumineendownloads.mumineenpdf.R;

import java.util.ArrayList;


public class SelectFileAdapter extends RecyclerView.Adapter<SelectFileAdapter.ViewHolder> {

    private ArrayList<SelectFile> mValues;
    private Context context;
    private SelectFileFragment fileFragment;
    public SelectFileAdapter(Context context, ArrayList<SelectFile> files, SelectFileFragment fileFragment) {
        mValues = files;
        this.fileFragment =  fileFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_selectfile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SelectFile file = mValues.get(position);
        long size = file.getFileSize();
        size = size/1024;
        String s;
        if(size<1024){
            s=size+" KB";
        }else {
            s = (size/1024) + " MB";
        }
        holder.mLoc.setText(s);
        holder.mTitle.setText(file.getFilename());
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileFragment.end(file);
            }
        });
    }




    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void filter(ArrayList<SelectFile> newlist) {
        mValues=new ArrayList<>();
        mValues.addAll(newlist);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mTitle;
        final TextView mLoc;
        SelectFile mItem;
        public RelativeLayout mainView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.name);
            mLoc = (TextView) view.findViewById(R.id.location);
            mainView = (RelativeLayout)view.findViewById(R.id.mainView);
        }
    }
}
