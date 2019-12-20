package com.svce.sparrowpro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.ImageViewHolder> {

    ArrayList<BoxDetails> boxDetailsList;
    Context context;
    BoxDetails boxDetails;
    Bundle bundle;
    Intent intent;
    UserDetails userDetails;

    public BoxAdapter(Activity activity, ArrayList<BoxDetails> boxDetailsList, UserDetails userdetails) {
        context=activity;
        this.boxDetailsList=boxDetailsList;
        this.userDetails=userdetails;
    }

    @NonNull
    @Override
    public BoxAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adoptView = LayoutInflater.from(context).inflate(R.layout.adoptview, parent, false);
        return new BoxAdapter.ImageViewHolder(adoptView);
    }


    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        boxDetails=boxDetailsList.get(position);
        holder.box_id.setText(boxDetails.boxkey);
        holder.address_id.setText(boxDetails.address);
        if(boxDetails.adopt.equals("unadopted"))
        {
            holder.adopt_id.setText("Not Adopted");
            holder.adopt_id.setTextColor(context.getResources().getColor(R.color.denied));
        }
        else if(boxDetails.adopt.equals("adopted"))
        {
            holder.adopt_id.setText("Adopted");
            holder.adopt_id.setTextColor(context.getResources().getColor(R.color.accepted));
        }
        holder.cardView.setAlpha(1);
    }


    @Override
    public int getItemCount() {
        return boxDetailsList.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView box_id,address_id,adopt_id;
        CardView cardView;
        public ImageViewHolder(View boxView) {
            super(boxView);
            boxView.setOnClickListener(this);
            address_id=boxView.findViewById(R.id.address);
            box_id=boxView.findViewById(R.id.boxkey);
            adopt_id=boxView.findViewById(R.id.ownername);
            cardView=boxView.findViewById(R.id.card);
        }

        @Override
        public void onClick(View view) {
            final ProgressDialog dialog = ProgressDialog.show(context, "Box Details",
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
            int position=getAdapterPosition();
            boxDetails=boxDetailsList.get(position);
            bundle = new Bundle();
            intent = new Intent(context, AdoptSparrowRespective.class);
            bundle.putSerializable("boxdetails", boxDetails);
            bundle.putSerializable("userdetails", userDetails);
            intent.putExtras(bundle);
            thread.start();
        }
    }
}
