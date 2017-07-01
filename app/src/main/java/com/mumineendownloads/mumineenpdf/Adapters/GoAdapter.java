package com.mumineendownloads.mumineenpdf.Adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
public class GoAdapter extends RecyclerView.Adapter<GoAdapter.MyViewHolder>  {

    private Context context;
    private ArrayList<PDF.PdfBean> pdfBeanArrayList;
    private PDFHelper pdfHelper;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton imageButton;
        public TextView title,size;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            size = (TextView) view.findViewById(R.id.size);
            imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        }
    }

    public void filter(ArrayList<PDF.PdfBean>newList) {
        pdfBeanArrayList=new ArrayList<>();
        pdfBeanArrayList.addAll(newList);
        notifyDataSetChanged();
    }

    public GoAdapter(ArrayList<PDF.PdfBean> pdfList, Context applicationContext) {
        pdfHelper = new PDFHelper(applicationContext);
        this.pdfBeanArrayList = pdfList;
        this.context = applicationContext;
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
        final String t;
        if (Integer.parseInt(pdf.getSize()) < 1024) {
            t = pdf.getSize() + " KB";
        } else {
            Float size = Float.valueOf(pdf.getSize()) / 1024;
            t = new DecimalFormat("##.##").format(size) + " MB";
        }
        String al = "";
        holder.size.setText(t + al);
        holder.title.setText(pdf.getTitle());
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context,holder.imageButton);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(context,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfBeanArrayList.size();
    }
}
