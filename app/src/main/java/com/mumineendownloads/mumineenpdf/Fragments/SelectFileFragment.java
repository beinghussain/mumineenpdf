package com.mumineendownloads.mumineenpdf.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Adapters.SelectFileAdapter;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.Model.SelectFile;
import com.mumineendownloads.mumineenpdf.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class SelectFileFragment extends DialogFragment {

    public static final int RESULT_FILE = 1;
    private final RequestPage requestPage;
    private ArrayList<SelectFile> files;
    private RecyclerView mRecyclerView;
    private ArrayList<SelectFile> arrayListFiles =new ArrayList<>();
    private RelativeLayout mLoading;
    private RelativeLayout mNoItem;
    private MaterialSearchBar searchBar;
    private ArrayList<SelectFile> newlist;
    private SelectFileAdapter selectAdapter;
    private Toolbar mActivityActionBarToolbar;

    public static SelectFileFragment newInstance(RequestPage requestPage){
        return new SelectFileFragment(requestPage);
    }


    public SelectFileFragment(RequestPage requestPage) {
        this.requestPage = requestPage;
    }


    public ArrayList<SelectFile> walkdir(File dir) {
        String pdfPattern = ".pdf";
        String mumineenFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen";

        if(!dir.getAbsolutePath().equals(mumineenFolder)) {
            File listFile[] = dir.listFiles();

            if (listFile != null) {
                for (File aListFile : listFile) {

                    if (aListFile.isDirectory()) {
                        walkdir(aListFile);
                    } else {
                        if (aListFile.getName().endsWith(pdfPattern) && !isNotFromApp(aListFile.getName())) {
                            SelectFile file = new SelectFile();
                            file.setFilename(aListFile.getName());
                            file.setFileSize(aListFile.length());
                            file.setFileUrl(aListFile.getAbsolutePath());
                            arrayListFiles.add(file);
                        }
                    }
                }
            }
        }

        return arrayListFiles;
    }

    private boolean isNotFromApp(String name) {
        name = name.replace(".pdf","");
        return name.matches("[-+]?\\d*\\.?\\d+") && name.length() == 4;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selectfile_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mLoading = (RelativeLayout) view.findViewById(R.id.loading);
        mNoItem = (RelativeLayout) view.findViewById(R.id.noItemFound);

        mActivityActionBarToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mActivityActionBarToolbar);
        Fonty.setFonts(mActivityActionBarToolbar);
        mActivityActionBarToolbar.setTitle("Select file to upload");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                  final ArrayList<SelectFile> arrayList = walkdir(Environment.getExternalStorageDirectory());
                    if(arrayList.size()==0){
                        mNoItem.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        mLoading.setVisibility(View.GONE);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoading.setVisibility(View.GONE);
                            mNoItem.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            selectAdapter = new SelectFileAdapter(getActivity(),arrayList,SelectFileFragment.this);
                            mRecyclerView.setAdapter(selectAdapter);
                        }
                    });
                } catch (NullPointerException ignored) {

                }
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate( R.menu.select, menu);
        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText=newText.toLowerCase();
                newlist=new ArrayList<>();
                for(SelectFile name:arrayListFiles)
                {
                    String getName=name.getFilename().toLowerCase();
                    if(getName.contains(newText)){
                        newlist.add(name);
                    }
                    if(newlist.size()==0){
                        mRecyclerView.setVisibility(View.GONE);
                        mNoItem.setVisibility(View.VISIBLE);
                    }else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mNoItem.setVisibility(View.GONE);
                    }
                }
                selectAdapter.filter(newlist);
                return true;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void end(SelectFile file) {
        requestPage.setFile(file);
        getDialog().dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }


}
