package com.mumineendownloads.mumineenpdf.Helpers;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Adapters.PDFAdapter;
import com.mumineendownloads.mumineenpdf.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * The configuration screen for the {@link OnTheWidget OnTheWidget} AppWidget.
 */
public class OnTheWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.mumineendownloads.mumineenpdf.Helpers.OnTheWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    RecyclerView mRecyclerView;

    public void itemClick(String title){
        saveTitlePref(getApplicationContext(),mAppWidgetId,title);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        OnTheWidget.updateAppWidget(getApplicationContext(), appWidgetManager, mAppWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public OnTheWidgetConfigureActivity() {
        super();
    }

    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    class WidgetRAdapter extends RecyclerView.Adapter<WidgetRAdapter.WidgetRViewHolder> {
        List<String> list = new ArrayList<>();
        Context mCtx;

        WidgetRAdapter(List<String> list, Context mCtx) {
            this.list = list;
            this.mCtx = mCtx;
        }

        @Override
        public WidgetRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_chooser, parent, false);

            Fonty.setFonts((ViewGroup) itemView);


            return new WidgetRAdapter.WidgetRViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(WidgetRViewHolder holder, int position) {
            final String title = list.get(position);
            String size = Utils.getPDFCount(getApplicationContext(),title) + " PDF Files in these list";
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toasty.normal(v.getContext(),title).show();
                    itemClick(title);
                }
            });
            holder.title.setText(title);
            holder.size.setText(size);
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        class WidgetRViewHolder extends RecyclerView.ViewHolder{

            public TextView title,size;

            WidgetRViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                size = (TextView) itemView.findViewById(R.id.size);
            }
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);


        setContentView(R.layout.on_the_widget_configure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Choose a list");

        Fonty.setFonts(toolbar);

                mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_widger);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new CustomDivider(getApplicationContext()));
        List<String> list = Utils.getSections(getApplicationContext());
        mRecyclerView.setAdapter(new WidgetRAdapter(list,getApplicationContext()));

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            }
    }
}

