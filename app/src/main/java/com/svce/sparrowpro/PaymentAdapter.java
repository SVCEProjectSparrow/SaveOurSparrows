package com.svce.sparrowpro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ImageViewHolder> {
    ArrayList<PaymentDetails> paymentboxlist;
    Context context;
    PaymentDetails paymentDetails;
    Bundle bundle;
    Intent intent;
    UserDetails userDetails;


    public PaymentAdapter(Activity activity, ArrayList<PaymentDetails> paymentboxlist, UserDetails userdetails) {
        context=activity;
        this.paymentboxlist=paymentboxlist;
        this.userDetails=userdetails;
    }

    @NonNull
    @Override
    public PaymentAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View paymentView = LayoutInflater.from(context).inflate(R.layout.payment_view_item, parent, false);
        return new PaymentAdapter.ImageViewHolder(paymentView);
    }


    @Override
    public void onBindViewHolder(@NonNull PaymentAdapter.ImageViewHolder holder, int position) {
        paymentDetails=paymentboxlist.get(position);
        holder.paymentid.setText(paymentDetails.paymentid);
        holder.boxid.setText(paymentDetails.boxid);
        holder.userid.setText(paymentDetails.userid);
        holder.transactiondate.setText(paymentDetails.transdate);
        holder.processstatus.setText(paymentDetails.processstatus);
        holder.processstatus.setTextColor(context.getResources().getColor(R.color.accepted));
    }


    @Override
    public int getItemCount() {
        return paymentboxlist.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView paymentid, boxid, userid, transactiondate, processstatus;
        CardView cardView;

        public ImageViewHolder(View paymentView) {
            super(paymentView);
            paymentView.setOnClickListener(this);
            paymentid = paymentView.findViewById(R.id.paymentid);
            boxid = paymentView.findViewById(R.id.boxid);
            userid = paymentView.findViewById(R.id.userid);
            transactiondate = paymentView.findViewById(R.id.transactiondate);
            processstatus = paymentView.findViewById(R.id.processstatus);
            cardView = paymentView.findViewById(R.id.card);
        }

        @Override
        public void onClick(View view) {
            final ProgressDialog dialog = ProgressDialog.show(context, "Payment Details",
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
            paymentDetails = paymentboxlist.get(position);
            bundle = new Bundle();
            intent = new Intent(context, PaymentActivityRespective.class);
            bundle.putSerializable("paydetails", paymentDetails);
            bundle.putSerializable("userdetails", userDetails);
            intent.putExtras(bundle);
            thread.start();
        }
    }
}
