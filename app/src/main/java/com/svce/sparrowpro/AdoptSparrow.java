package com.svce.sparrowpro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

public class AdoptSparrow extends AppCompatActivity {
    UserDetails userDetails;
    Bundle bundle,bundle1;
    String tnc;
    View parentView;
    String hierarchy,username;
    RecyclerView adopt_recycler_view;
    android.widget.ProgressBar progress_reports;
    MaterialSpinner filter,filter2;
    EncryptionDecryption encryptionDecryption;
    FirebaseDatabase fdatabase;
    DatabaseReference dreference1, dreference2;
    String boxkey,adopt,address;
    int j,temp=0;
    ArrayList<String> boxkeylist,addresslist,adoptlist;
    ArrayList<BoxDetails> boxreportlist;
    BoxAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt_sparrow);
        parentView = findViewById(android.R.id.content);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Adopt Sparrow");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        };
        t.start();
        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");

        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);
        hierarchy=userDetails.hierarchy;

        boxkeylist=new ArrayList<>();
        addresslist=new ArrayList<>();
        adoptlist=new ArrayList<>();
        boxreportlist=new ArrayList<>();
        encryptionDecryption=new EncryptionDecryption();
        adopt_recycler_view=findViewById(R.id.adoptrecyclerview);
        adopt_recycler_view.setHasFixedSize(true);
        adopt_recycler_view.setLayoutManager(new LinearLayoutManager(AdoptSparrow.this));
        progress_reports=(android.widget.ProgressBar) findViewById(R.id.progress_reports);
        progress_reports.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
        progress_reports.setVisibility(View.VISIBLE);
        boxAdapter=new BoxAdapter(AdoptSparrow.this,boxreportlist,userDetails);
        adopt_recycler_view.setAdapter(boxAdapter);
        filter=(MaterialSpinner)findViewById(R.id.filter);
        filter2=(MaterialSpinner)findViewById(R.id.filter2);
        filter.setItems("All","Adopted","Not Adopted");
        filter2.setItems("Box Fixed","Box Not Fixed");
        tnc=userDetails.tnc;
        if(!getMobileDataState())
        {
            Snackbar.make(parentView,"Turn On Mobile Data!!!",Snackbar.LENGTH_LONG).show();
        }

        fdatabase=FirebaseDatabase.getInstance();
        dreference1 = fdatabase.getReference("sparrow_boxes");
        if(hierarchy.equals("admins")) {
            filter.setVisibility(View.VISIBLE);
            filter.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                    boxreportlist.clear();
                    progress_reports.setVisibility(View.VISIBLE);
                    switch (position)
                    {
                        case 0:
                            if(temp==1)
                            {
                                for(j=0;j<adoptlist.size();j++)
                                {
                                    boxreportlist.add(new BoxDetails(boxkeylist.get(j),addresslist.get(j),adoptlist.get(j)));
                                }
                            }
                            break;

                        case 1:
                            for(j=0;j<adoptlist.size();j++)
                            {
                                if(adoptlist.get(j).equals("adopted"))
                                {
                                    boxreportlist.add(new BoxDetails(boxkeylist.get(j),addresslist.get(j),adoptlist.get(j)));
                                }
                            }
                            break;

                        case 2:
                            for(j=0;j<adoptlist.size();j++)
                            {
                                if(adoptlist.get(j).equals("unadopted"))
                                {
                                    boxreportlist.add(new BoxDetails(boxkeylist.get(j),addresslist.get(j),adoptlist.get(j)));
                                }
                            }
                            break;

                    }
                    if(temp==1) {
                        boxAdapter.notifyDataSetChanged();
                        progress_reports.setVisibility(View.INVISIBLE);
                    }
                }
            });
            dreference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boxreportlist.clear();
                    progress_reports.setVisibility(View.VISIBLE);
                    temp = 0;
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        boxkey = encryptionDecryption.decrypt((String) s.child("boxkey").getValue());
                        if(boxkey!=null)
                        {
                            address = encryptionDecryption.decrypt((String) s.child("address").getValue());
                            adopt = encryptionDecryption.decrypt((String) s.child("adopt").getValue());
                            boxkeylist.add(boxkey);
                            addresslist.add(address);
                            adoptlist.add(adopt);
                            boxreportlist.add(new BoxDetails(boxkey,address,adopt));
                        }
                    }
                    boxAdapter.notifyDataSetChanged();
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
            fdatabase=FirebaseDatabase.getInstance();
            dreference1 = fdatabase.getReference("sparrow_boxes");
            dreference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progress_reports.setVisibility(View.VISIBLE);
                    boxreportlist.clear();
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        boxkey = encryptionDecryption.decrypt((String) s.child("boxkey").getValue());
                        adopt = encryptionDecryption.decrypt((String) s.child("adopt").getValue());
                        if(boxkey!=null && adopt!=null && adopt.equals("unadopted"))
                        {
                            address = encryptionDecryption.decrypt((String) s.child("address").getValue());
                            boxkeylist.add(boxkey);
                            addresslist.add(address);
                            adoptlist.add(adopt);
                            boxreportlist.add(new BoxDetails(boxkey,address,adopt));
                        }
                    }
                    boxAdapter.notifyDataSetChanged();
                    progress_reports.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onBackPressed(){
        Intent intent=new Intent(AdoptSparrow.this,MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        finish();
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


}
