package com.svce.sparrowpro;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class SparrowReportsRespective extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap mMap;
    FirebaseDatabase fdatabase;
    DatabaseReference sparrows,users,locationsdr;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    ImageView report_image;
    TextView report_id,report_by,report_mail,report_mobile,report_date,statustxt;
    TextView descrpitiontxt,gendertxt,numsparrowstxt,nestingtxt,boxtxt,boxrequesttxt,address,landmarktxt,locations;
    Bundle bundle,bundle1;
    String reportid,name,reporteddate,sparrowimageurl,mobile,userid,body;
    String genderst, nestingst, numsparrows, boxst, requestst,locationsst,landmark,description;
    String addressst,userkey,status,hierarchy,locationkey;
    android.widget.ProgressBar progressBar;
    LinearLayout adminselect;
    MaterialSpinner selectstatus;
    Button submit,share;
    double latitude, longitude;
    int i;
    EncryptionDecryption encryptionDecryption;
    RelativeLayout cardLayout;
    CardView cardView;
    SparrowReportDetails sparrowReportDetails;
    UserDetails userDetails;
    Intent intent;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sparrow_reports_respective);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Report");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        };
        t.start();
        View parentView = findViewById(android.R.id.content);
        cardLayout=findViewById(R.id.cardlayout);
        cardView=findViewById(R.id.card);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Slide slide=new Slide();
                slide.setSlideEdge(Gravity.BOTTOM);
                slide.setDuration(2000);
                TransitionManager.beginDelayedTransition(cardLayout,slide);
                cardView.setVisibility(View.VISIBLE);
            }
        },100);
        if(!getMobileDataState())
        {
            Snackbar.make(parentView,"Turn On Mobile Data!!!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
        }
        adminselect=findViewById(R.id.adminselect);
        adminselect.setVisibility(View.GONE);
        selectstatus=findViewById(R.id.selectstatus);
        submit=findViewById(R.id.submit);
        report_image=findViewById(R.id.report_image);
        report_id=findViewById(R.id.report_id);
        report_mail=findViewById(R.id.report_mail);
        report_mobile=findViewById(R.id.report_mobile);
        report_by=findViewById(R.id.report_by);
        report_date=findViewById(R.id.report_date);
        statustxt=findViewById(R.id.statustxt);
        address=findViewById(R.id.address);
        share=findViewById(R.id.share);
        descrpitiontxt=findViewById(R.id.description);
        gendertxt=findViewById(R.id.gender);
        numsparrowstxt=findViewById(R.id.numbersparrows);
        nestingtxt=findViewById(R.id.nesting);
        boxtxt=findViewById(R.id.box);
        boxrequesttxt=findViewById(R.id.boxrequest);
        landmarktxt=findViewById(R.id.landmark);
        locations=findViewById(R.id.locations);
        progressBar=(android.widget.ProgressBar)findViewById(R.id.progress_image);
        progressBar.setVisibility(View.VISIBLE);

//        progressBar.getProgressDrawable().setColorFilter(
//                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        encryptionDecryption=new EncryptionDecryption();
        bundle=getIntent().getExtras();
        sparrowReportDetails=(SparrowReportDetails)bundle.getSerializable("sparrowreportdetails");
        userDetails=(UserDetails)bundle.getSerializable("userdetails");
        userkey=sparrowReportDetails.userkey;
        reportid=sparrowReportDetails.reportid;
        reporteddate=sparrowReportDetails.reporteddate;
        sparrowimageurl=sparrowReportDetails.sparrowimageurl;
        status=sparrowReportDetails.status;
        locationkey=sparrowReportDetails.locationkey;
        hierarchy=bundle.getString("hierarchy");

        Picasso.get().load(sparrowimageurl).placeholder(R.drawable.ic_photo_library_black_24dp).fit().centerCrop().into(report_image,new Callback() {
            @Override
            public void onSuccess() {
                report_image.setAlpha((float)1);
                progressBar.setVisibility(View.INVISIBLE);
                report_image.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        fdatabase= FirebaseDatabase.getInstance();
        sparrows=fdatabase.getReference("sparrow_uploads").child(userkey).child(reportid);
        users=fdatabase.getReference("sparrow_users").child(userkey);
        locationsdr=fdatabase.getReference("sparrow_locations").child(locationkey);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userid=encryptionDecryption.decrypt((String)dataSnapshot.child("userid").getValue());
                name=encryptionDecryption.decrypt((String)dataSnapshot.child("name").getValue());
                mobile=encryptionDecryption.decrypt((String)dataSnapshot.child("mobile").getValue());
                report_by.setText("Name\t\t|\t"+name);
                report_mobile.setText("Mobile\t\t|\t"+mobile);
                report_mail.setText("UserID\t|\t"+userid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        report_id.setText(reportid);
        report_date.setText("Date\t\t\t|\t"+reporteddate);
        statustxt.setText("Status\t\t|\t"+status);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if(hierarchy.equals("admins"))
        {
            adminselect.setVisibility(View.VISIBLE);
            selectstatus.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
            selectstatus.setItems("Accept","Deny");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i=selectstatus.getSelectedIndex();
                    if(i==0)
                    {
//                        Toast.makeText(SparrowReportsRespective.this, "Accepted", Toast.LENGTH_SHORT).show();
                        sparrows.child("status").setValue(encryptionDecryption.encrypt("Accepted"));
                        locationsdr.child("status").setValue(encryptionDecryption.encrypt("Accepted"));
                        sparrowReportDetails.setStatus("Accepted");
                        status="Accepted";
                        statustxt.setText("Status\t\t|\t"+"Accepted");
                    }
                    else
                    {
//                        Toast.makeText(SparrowReportsRespective.this, "Denied", Toast.LENGTH_SHORT).show();
                        sparrows.child("status").setValue(encryptionDecryption.encrypt("Denied"));
                        locationsdr.child("status").setValue(encryptionDecryption.encrypt("Denied"));
                        sparrowReportDetails.setStatus("Denied");

                        statustxt.setText("Status\t\t|\t"+"Denied");
                    }
                    selectstatus.setSelected(true);
                }
            });
        }
        else
        {
            adminselect.setVisibility(View.GONE);
            selectstatus.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
        }
        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog=ProgressDialog.show(SparrowReportsRespective.this,"Report Sharing","Preparing for Sharing",true);
                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        dialog.dismiss();
                        startActivity(intent);
                    }
                };
                Thread thread=new Thread()
                {
                    public void run()
                    {
                        body = "REPORT DETAILS\n\nReport ID\t\t\t|\t"+reportid+"\n\n"+"Name\t\t|\t"+name+"\n"+"Mobile\t\t|\t"+mobile+"\n"+"Status\t\t|\t"+status
                                +"\n\n\nSPARROW DETAILS\n\nDescription\t\t\t\t|\t"+description+"\n"+"Gender\t\t\t\t\t\t|\t"+genderst+"\n"
                                +"Sparrows Count\t|\t"+numsparrows+"\n"+"Nesting\t\t\t\t\t\t|\t"+nestingst+"\n"+"Box Provided\t\t|\t"+boxst+"\n"
                                +"Box Request\t\t\t|\t"+requestst+"\n"+"Address\t\t\t\t\t|\t"+addressst+"."+"\n"+"Landmark\t\t\t\t|\t"+landmark+"\n"
                                +"Latitude\t\t\t\t\t|\t"+latitude+"\n"+"Longitude\t\t\t\t|\t"+longitude+"\n"+"Location\t\t\t\t\t|\t"+locationsst;
                        intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setType("text/img");
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "REPORT");
                        intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
                        handler.sendEmptyMessage(0);
                    }
                };
                thread.start();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        sparrows.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                description=encryptionDecryption.decrypt((String) dataSnapshot.child("description").getValue());
                if(description!=null) {
                    addressst=encryptionDecryption.decrypt((String) dataSnapshot.child("address").getValue());
                    genderst=encryptionDecryption.decrypt((String) dataSnapshot.child("gender").getValue());
                    latitude = Double.parseDouble(encryptionDecryption.decrypt((String) dataSnapshot.child("latitude").getValue()));
                    landmark=encryptionDecryption.decrypt((String) dataSnapshot.child("landmark").getValue());
                    locationsst=encryptionDecryption.decrypt((String) dataSnapshot.child("locations").getValue());
                    longitude = Double.parseDouble(encryptionDecryption.decrypt((String) dataSnapshot.child("longitude").getValue()));
                    nestingst=encryptionDecryption.decrypt((String) dataSnapshot.child("nesting").getValue());
                    numsparrows=encryptionDecryption.decrypt((String) dataSnapshot.child("numbersparrows").getValue());
                    boxst=encryptionDecryption.decrypt((String) dataSnapshot.child("sparrowbox").getValue());
                    requestst=encryptionDecryption.decrypt((String) dataSnapshot.child("sparrowboxrequest").getValue());
                    descrpitiontxt.setText("Description\t\t\t\t|\t"+description);
                    gendertxt.setText("Gender\t\t\t\t\t\t\t|\t"+genderst);
                    numsparrowstxt.setText("Sparrows Count\t|\t"+numsparrows);
                    nestingtxt.setText("Nesting\t\t\t\t\t\t\t|\t"+nestingst);
                    boxtxt.setText("Box Provided\t\t\t|\t"+boxst);
                    boxrequesttxt.setText("Box Request\t\t\t|\t"+requestst);
                    address.setText("Address\t\t\t\t\t\t\t|\t"+addressst+".");
                    landmarktxt.setText("Landmark\t\t\t\t\t|\t"+landmark);
                    locations.setText("Location\t\t\t\t\t\t|\t"+locationsst);
                }
                if(latitude!=0.0 && longitude!=0.0) {
                    LatLng latlng = new LatLng(latitude, longitude);
//                    gmap.setMinZoomPreference(12);
//                    gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
                    mMap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(addressst)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    @Override
    public void onBackPressed() {
        intent=new Intent(SparrowReportsRespective.this,SparrowReports.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        finish();
    }
}
