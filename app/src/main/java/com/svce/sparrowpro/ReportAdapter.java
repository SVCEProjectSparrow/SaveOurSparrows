package com.svce.sparrowpro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ImageViewHolder> {

    ArrayList<SparrowReportDetails> sparrowreportlist;
    Context context;
    SparrowReportDetails sparrowReportDetails;
    Bundle bundle;
    String hierarchy;
    Intent intent;
    UserDetails userDetails;

    public ReportAdapter(FragmentActivity activity, ArrayList<SparrowReportDetails> sparrowreportlist, String hierarchy, UserDetails userdetails) {
        context = activity;
        this.sparrowreportlist = sparrowreportlist;
        this.hierarchy = hierarchy;
        this.userDetails = userdetails;
    }

    @NonNull
    @Override
    public ReportAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View reportView = LayoutInflater.from(context).inflate(R.layout.report_view_item, parent, false);
        return new ImageViewHolder(reportView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ReportAdapter.ImageViewHolder holder, int position) {
        sparrowReportDetails = sparrowreportlist.get(position);
        holder.spinner.setVisibility(View.VISIBLE);
//        holder.spinner.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        holder.report_id.setText(sparrowReportDetails.reportid);
        holder.report_date.setText(sparrowReportDetails.reporteddate);
        if (sparrowReportDetails.status.equals("Accepted")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.accepted));
        } else if (sparrowReportDetails.status.equals("Denied")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.denied));
        } else if (sparrowReportDetails.status.equals("Pending...")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.pending));
        }
        holder.status.setText(sparrowReportDetails.status);
        Picasso.get().load(sparrowReportDetails.sparrowimageurl).fit().centerCrop().into(holder.report_image, new Callback() {
            @Override
            public void onSuccess() {
                holder.cardView.setAlpha(1);
                holder.spinner.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.spinner.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public int getItemCount() {
        return sparrowreportlist.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView report_image;
        TextView report_id, report_date, status;
        final android.widget.ProgressBar spinner;
        android.support.v7.widget.CardView cardView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
//            spinner = new android.widget.ProgressBar(
//                    context,
//                    null,
//                    android.R.attr.progressBarStyle);
            cardView = itemView.findViewById(R.id.card);
            spinner = itemView.findViewById(R.id.spinner);
            report_image = itemView.findViewById(R.id.report_image);
            report_id = itemView.findViewById(R.id.report_id);
            report_date = itemView.findViewById(R.id.report_date);
            status = itemView.findViewById(R.id.status);
        }

        @Override
        public void onClick(View view) {
            final ProgressDialog dialog = ProgressDialog.show(context, "Report",
                    "Fetching!", true);
            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    dialog.setIndeterminate(true);
                    dialog.dismiss();
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    ((Activity) context).finish();
                }
            };
            Thread thread = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            };
            int position = getAdapterPosition();
            sparrowReportDetails = sparrowreportlist.get(position);
            bundle = new Bundle();
            intent = new Intent(context, SparrowReportsRespective.class);
            bundle.putSerializable("sparrowreportdetails", sparrowReportDetails);
            bundle.putSerializable("userdetails", userDetails);
            bundle.putString("hierarchy", hierarchy);
            intent.putExtras(bundle);
            thread.start();

        }
    }


}
