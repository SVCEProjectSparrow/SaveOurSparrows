package com.svce.sparrowpro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashSet;

public class SparrowReports extends AppCompatActivity {
    RecyclerView recycler_reports_view;
    ReportAdapter reportAdapter;
    android.widget.ProgressBar progress_reports;
    MaterialSpinner filter;
    UserDetails userDetails;
    FirebaseDatabase fdatabase;
    DatabaseReference dreference1;
    String hierarchy,username,userkey;
    String userid="",reportid="",sparrowimageurl="",name="",reporteddate="",status="",locationkey="";
    ArrayList<String> useridlist,reportidlist,reportdatelist,imageurllist,userkeylist,statuslist,locationkeylist;
    ArrayList<SparrowReportDetails> sparrowreportlist;
    Bundle bundle,bundle1;
    EncryptionDecryption encryptionDecryption;
    int j,temp=0;
    View parentView;
    HashSet<String> statusset;
    HashSet<SparrowReportDetails> sparrowset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sparrow_reports);
        parentView = findViewById(android.R.id.content);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Sparrow Reports");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        };
        t.start();
        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");
        username=userDetails.username;
        hierarchy=userDetails.hierarchy;
        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);
        encryptionDecryption=new EncryptionDecryption();
        recycler_reports_view=findViewById(R.id.recycler_reports_view);
        recycler_reports_view.setHasFixedSize(true);
        recycler_reports_view.setLayoutManager(new LinearLayoutManager(SparrowReports.this));
        progress_reports=(android.widget.ProgressBar) findViewById(R.id.progress_reports);
        progress_reports.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
        progress_reports.setVisibility(View.VISIBLE);
        filter=(MaterialSpinner)findViewById(R.id.filter);
//        final String filterarr[]=new String[5];
//        filterarr[0]="All Reports";
//        filterarr[1]="Not Visited";
//        filterarr[2]="Visited";
//        filterarr[3]="Accepted";
//        filterarr[4]="Denied";
//        ArrayAdapter<String> FilterAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,filterarr);
//        FilterAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        filter.setAdapter(FilterAdapter);
        filter.setItems("All Reports","Not Visited","Visited","Accepted","Denied");
        useridlist=new ArrayList<String>();
        reportidlist=new ArrayList<String>();
        reportdatelist=new ArrayList<String>();
        imageurllist=new ArrayList<String>();
        userkeylist=new ArrayList<String>();
        statuslist=new ArrayList<String>();
        locationkeylist=new ArrayList<String>();
        sparrowreportlist=new ArrayList<SparrowReportDetails>();
        statusset=new HashSet<String>();
        sparrowset=new HashSet<SparrowReportDetails>();
        reportAdapter=new ReportAdapter(SparrowReports.this,sparrowreportlist,hierarchy,userDetails);
        recycler_reports_view.setAdapter(reportAdapter);
        fdatabase=FirebaseDatabase.getInstance();
        if(!getMobileDataState())
        {
//            Snackbar.make(parentView,"Turn On Mobile Data!!!",Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mainFunction();
//                }
//            }).show();

            Snackbar.make(parentView,"Turn On Mobile Data!!!",Snackbar.LENGTH_LONG).setAction("Retry",null).show();
        }
        if(hierarchy.equals("admins")) {
            filter.setVisibility(View.VISIBLE);
            filter.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                    sparrowreportlist.clear();
                    progress_reports.setVisibility(View.VISIBLE);
                    switch (position) {
                        case 0: {
                            if(temp==1) {
                                for (j = 0; j < statuslist.size(); j++) {
                                    sparrowreportlist.add(new SparrowReportDetails(userkeylist.get(j), useridlist.get(j), reportidlist.get(j), reportdatelist.get(j),
                                            imageurllist.get(j), statuslist.get(j),locationkeylist.get(j)));
                                }
                            }
                            break;
                        }


                        case 1: {
                            for (j = 0; j < statuslist.size(); j++) {
                                if (statuslist.get(j).equals("Pending...")) {
                                    sparrowreportlist.add(new SparrowReportDetails(userkeylist.get(j), useridlist.get(j), reportidlist.get(j), reportdatelist.get(j),
                                            imageurllist.get(j), statuslist.get(j),locationkeylist.get(j)));
                                }
                            }
                            break;
                        }

                        case 2: {
                            for (j = 0; j < statuslist.size(); j++) {
                                if (statuslist.get(j).equals("Accepted") || statuslist.get(j).equals("Denied")) {
                                    sparrowreportlist.add(new SparrowReportDetails(userkeylist.get(j), useridlist.get(j), reportidlist.get(j), reportdatelist.get(j),
                                            imageurllist.get(j), statuslist.get(j),locationkeylist.get(j)));
                                }
                            }
                            break;
                        }

                        case 3: {
                            for (j = 0; j < statuslist.size(); j++) {
                                if (statuslist.get(j).equals("Accepted")) {
                                    sparrowreportlist.add(new SparrowReportDetails(userkeylist.get(j), useridlist.get(j), reportidlist.get(j), reportdatelist.get(j),
                                            imageurllist.get(j), statuslist.get(j),locationkeylist.get(j)));
                                }
                            }
                            break;
                        }

                        case 4: {
                            for (j = 0; j < statuslist.size(); j++) {
                                if (statuslist.get(j).equals("Denied")) {
                                    sparrowreportlist.add(new SparrowReportDetails(userkeylist.get(j), useridlist.get(j), reportidlist.get(j), reportdatelist.get(j),
                                            imageurllist.get(j), statuslist.get(j),locationkeylist.get(j)));
                                }
                            }
                            break;
                        }
                    }
                    if(temp==1) {
                        reportAdapter.notifyDataSetChanged();
                        progress_reports.setVisibility(View.INVISIBLE);
                    }
                }
            });
            dreference1 = fdatabase.getReference("sparrow_uploads");
            dreference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    sparrowreportlist.clear();
                    progress_reports.setVisibility(View.VISIBLE);
                    temp=0;
                    for(DataSnapshot base:dataSnapshot.getChildren()) {
                        for(DataSnapshot s:base.getChildren()) {
                            userid = encryptionDecryption.decrypt((String) s.child("userid").getValue());
                            if(userid!=null) {
                                userkey = encryptionDecryption.decrypt((String) s.child("userkey").getValue());
                                status = encryptionDecryption.decrypt((String) s.child("status").getValue());
                                reportid = encryptionDecryption.decrypt((String) s.child("reportid").getValue());
                                reporteddate = encryptionDecryption.decrypt((String) s.child("reportuploaded").getValue());
                                sparrowimageurl = encryptionDecryption.decrypt((String) s.child("sparrowimageurl").getValue());
                                locationkey=encryptionDecryption.decrypt((String)s.child("locationkey").getValue());

                                userkeylist.add(userkey);
                                useridlist.add(userid);
                                reportidlist.add(reportid);
                                reportdatelist.add(reporteddate);
                                imageurllist.add(sparrowimageurl);
                                statuslist.add(status);
                                locationkeylist.add(locationkey);
                                sparrowreportlist.add(new SparrowReportDetails(userkey, userid, reportid, reporteddate, sparrowimageurl, status,locationkey));
                            }
                        }
                    }
                    reportAdapter.notifyDataSetChanged();
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
            userkey=userDetails.userkey;
            dreference1 = fdatabase.getReference("sparrow_uploads").child(userkey);
            dreference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    sparrowreportlist.clear();
                    progress_reports.setVisibility(View.VISIBLE);
                    for(DataSnapshot s: dataSnapshot.getChildren())
                    {
                        userid=encryptionDecryption.decrypt((String)s.child("userid").getValue());
                        status=encryptionDecryption.decrypt((String)s.child("status").getValue());
                        if((username.equals(userid)) && !(status.equals("Denied"))) {
                            reportid=encryptionDecryption.decrypt((String)s.child("reportid").getValue());
                            reporteddate=encryptionDecryption.decrypt((String)s.child("reportuploaded").getValue());
                            sparrowimageurl=encryptionDecryption.decrypt((String)s.child("sparrowimageurl").getValue());
                            locationkey=encryptionDecryption.decrypt((String)s.child("locationkey").getValue());
                            userkeylist.add(userkey);
                            useridlist.add(userid);
                            reportidlist.add(reportid);
                            reportdatelist.add(reporteddate);
                            imageurllist.add(sparrowimageurl);
                            statuslist.add(status);
                            locationkeylist.add(locationkey);
                            sparrowreportlist.add(new SparrowReportDetails(userkey,userid,reportid,reporteddate,sparrowimageurl,status,locationkey));
                        }
                    }
                    reportAdapter.notifyDataSetChanged();
                    progress_reports.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        recycler_reports_view.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean getMobileDataState()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void onBackPressed() {
        Intent intent=new Intent(SparrowReports.this,MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        finish();
    }
}
