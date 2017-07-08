package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Fragments.PDFSavedListFragment;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.rey.material.widget.LinearLayout;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SavedPDFAdapter extends RecyclerView.Adapter<SavedPDFAdapter.MyViewHolder>  {

    private Context context;
    private PDFSavedListFragment pdfListFragment;
    private ArrayList<PDF.PdfBean> pdfBeanArrayList;
    private PDFHelper pdfHelper;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton imageButton;
        public TextView title,size;
        public RelativeLayout mainView;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            size = (TextView) view.findViewById(R.id.size);
            imageButton = (ImageButton) view.findViewById(R.id.cancelButton);
            mainView  = (RelativeLayout) view.findViewById(R.id.mainView);
        }
    }

    public void filter(ArrayList<PDF.PdfBean>newList) {
        pdfBeanArrayList=new ArrayList<>();
        pdfBeanArrayList.addAll(newList);
        notifyDataSetChanged();
    }

    public SavedPDFAdapter(ArrayList<PDF.PdfBean> pdfList, Context applicationContext, PDFSavedListFragment pdfListFragment) {
        pdfHelper = new PDFHelper(applicationContext);
        this.pdfBeanArrayList = pdfList;
        this.context = applicationContext;
        this.pdfListFragment = pdfListFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_pdf_item, parent, false);

        Fonty.setFonts((ViewGroup) itemView);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PDF.PdfBean pdf = pdfBeanArrayList.get(position);
        holder.title.setText(pdf.getTitle());
        String al = "";
        holder.size.setText(Utils.fileSize(pdf.getSize()) + al);
        holder.title.setText(pdf.getTitle());

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDF(pdf);
            }
        });

        holder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(v.getContext(),pdf,position);
                return true;
            }
        });
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showDialog(v.getContext(),pdf,position);
            }
        });
    }

    private void openPDF(PDF.PdfBean pdf) {
        Intent intent = new Intent(pdfListFragment.getActivity(), PDFActivity.class);
        intent.putExtra("mode", 0);
        intent.putExtra("pid", pdf.getPid());
        intent.putExtra("title", pdf.getTitle());
        pdfListFragment.startActivity(intent);
    }

    private void sendReport(final int pid, final CharSequence text) {
        final RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://mumineendownloads.com/app/pdf_error.php";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams()
            {
                String deviceId = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Map<String, String>  params = new HashMap<String, String>();
                params.put("pdf_id", String.valueOf(pid));
                params.put("error", (String) text);
                params.put("device_id", deviceId);

                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void reportApp(final PDF.PdfBean pdfBean, Context context){
        new MaterialDialog.Builder(context)
                .title("Report "+pdfBean.getTitle())
                .items(R.array.reportItems)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        sendReport(pdfBean.getPid(), text);
                        return true;
                    }
                })
                .positiveText("Choose")
                .show();
    }

    private void delete(final PDF.PdfBean pdf, final Context context, final int position) {
        new MaterialDialog.Builder(context)
                .title("Delete file")
                .negativeText("Cancel")
                .positiveText("Delete")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mumineen/" + pdf.getPid() + ".pdf");
                        if (file.exists()) {
                            file.delete();
                            pdf.setStatus(Status.STATUS_NULL);
                            Toasty.normal(context,pdf.getTitle()+" deleted").show();
                            pdfHelper.updatePDF(pdf);
                            pdfBeanArrayList.remove(position);
                            notifyItemRemoved(position);
                        }
                    }
                })
                .content("Do you really want to delete this file?").build().show();
    }

    private File getFile(int pid) {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Mumineen/"+pid+".pdf");
    }

    private void showDialog(final Context context, final PDF.PdfBean pdfBean, final int position) {
        new MaterialDialog.Builder(context)
                .items(R.array.saveItemList)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if(text.equals("Share")){
                            if(getFile(pdfBean.getPid()).exists()) {
                                Uri uri = FileProvider.getUriForFile(context,
                                        context.getPackageName() + ".provider", getFile(pdfBean.getPid()));
                                Intent share = new Intent();
                                share.setAction(Intent.ACTION_SEND);
                                share.setType("application/pdf");
                                share.putExtra(Intent.EXTRA_STREAM, uri);
                                context.startActivity(Intent.createChooser(share, "Share File"));
                            } else {
                                Toasty.normal(context, "File not downloaded yet").show();
                            }
                        }
                        else if(text.equals("Report")){
                            reportApp(pdfBean,view.getContext());
                        }
                        else if(text.equals("Delete")){
                          dialog.dismiss();
                            delete(pdfBean, context, position);
                        }
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return pdfBeanArrayList.size();
    }
}
