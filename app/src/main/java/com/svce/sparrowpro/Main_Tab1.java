package com.svce.sparrowpro;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;


public class Main_Tab1 extends Fragment {
    UserDetails userDetails;
    EncryptionDecryption encryptionDecryption;
    View parentView;
    ArrayList<String> paymentidlist,boxidlist,useridlist,transdatelist,processstatuslist;
    ArrayList<PaymentDetails> paymentboxlist;
    RecyclerView payment_recycler_view;
    android.widget.ProgressBar progress_reports;
    PaymentAdapter paymentAdapter;
    String hierarchy,user;
    FirebaseDatabase fdatabase;
    DatabaseReference transactions;
    MaterialSpinner filter;
    int temp=0;
    String paymentid,boxid,userkey,userid,transdate,processstatus;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_tab1,container, false);
        parentView = getActivity().findViewById(android.R.id.content);
        userDetails = (UserDetails) getArguments().getSerializable("userdetails");
        encryptionDecryption=new EncryptionDecryption();
        hierarchy=userDetails.hierarchy;
        user=userDetails.username;
        paymentidlist=new ArrayList<>();
        boxidlist=new ArrayList<>();
        useridlist=new ArrayList<>();
        transdatelist=new ArrayList<>();
        processstatuslist=new ArrayList<>();
        paymentboxlist=new ArrayList<>();
        payment_recycler_view=rootView.findViewById(R.id.paymentrecyclerview);
        payment_recycler_view.setHasFixedSize(true);
        payment_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        progress_reports=(android.widget.ProgressBar) rootView.findViewById(R.id.progress_reports);
        progress_reports.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
        progress_reports.setVisibility(View.VISIBLE);
        paymentAdapter=new PaymentAdapter(getActivity(),paymentboxlist,userDetails);
        payment_recycler_view.setAdapter(paymentAdapter);
        filter=(MaterialSpinner)rootView.findViewById(R.id.filter);
        filter.setItems("All","Started","Box Ready","Box Fixed");


        if(!getMobileDataState())
        {
            Snackbar.make(parentView,"Turn On Mobile Data!",Snackbar.LENGTH_LONG).show();
        }

        fdatabase=FirebaseDatabase.getInstance();
        transactions=fdatabase.getReference("boxes_transactions");
        if(hierarchy.equals("admins"))
        {
            filter.setVisibility(View.VISIBLE);
            filter.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                    paymentboxlist.clear();
                    progress_reports.setVisibility(View.VISIBLE);
                    switch (position)
                    {

                    }
                    if(temp==1) {
                        paymentAdapter.notifyDataSetChanged();
                        progress_reports.setVisibility(View.INVISIBLE);
                    }
                }
            });
            transactions.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    paymentboxlist.clear();
                    progress_reports.setVisibility(View.VISIBLE);
                    temp=0;
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        userid = (String) s.child("adoptby").getValue();
                        if(userid!=null)
                        {
                            userkey = (String) s.child("userkey").getValue();
                            paymentid = (String) s.child("paymentId").getValue();
                            boxid = (String) s.child("boxid").getValue();
                            transdate = (String) s.child("transactiondate").getValue();
                            processstatus = (String) s.child("processstatus").getValue();
                            paymentidlist.add(paymentid);
                            boxidlist.add(boxid);
                            useridlist.add(userid);
                            transdatelist.add(transdate);
                            processstatuslist.add(processstatus);
                            paymentboxlist.add(new PaymentDetails(paymentid,boxid,userkey,userid,transdate,processstatus));
                        }
                    }
                    paymentAdapter.notifyDataSetChanged();
                    progress_reports.setVisibility(View.INVISIBLE);
                    temp = 1;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            filter.setVisibility(View.GONE);
            transactions.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    paymentboxlist.clear();
                    progress_reports.setVisibility(View.VISIBLE);
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        userid = (String) s.child("adoptby").getValue();
                        if(user.equals(userid))
                        {
                            userkey = (String) s.child("userkey").getValue();
                            paymentid = (String) s.child("paymentId").getValue();
                            boxid = (String) s.child("boxid").getValue();
                            transdate = (String) s.child("transactiondate").getValue();
                            processstatus = (String) s.child("processstatus").getValue();
                            paymentidlist.add(paymentid);
                            boxidlist.add(boxid);
                            useridlist.add(userid);
                            transdatelist.add(transdate);
                            processstatuslist.add(processstatus);
                            paymentboxlist.add(new PaymentDetails(paymentid,boxid,userkey,userid,transdate,processstatus));
                        }
                    }
                    paymentAdapter.notifyDataSetChanged();
                    progress_reports.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return rootView;

    }

    public boolean getMobileDataState() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }



}
