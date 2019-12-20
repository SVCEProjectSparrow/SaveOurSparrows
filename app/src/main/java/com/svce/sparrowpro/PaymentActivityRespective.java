package com.svce.sparrowpro;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

public class PaymentActivityRespective extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    FirebaseDatabase fdatabase;
    DatabaseReference transactions,boxes,users;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    CoordinatorLayout cardLayout;
    CardView cardView;
    UserDetails userDetails;
    PaymentDetails paymentDetails;
    Bundle bundle,bundle1;
    String paymentid,boxid,userkey,userid,transdate,processstatus;
    Intent intent;
    EncryptionDecryption encryptionDecryption;
    Double latitude,longitude;
    String landmarkst,locationsst,addressst,ownerst,adoptst,adoptname,adoptmobile,appointmentdatest,adoptboxfixdatest,orderidst,amountst;
    TextView boxidtxt,addresstxt,landmarktxt,locationstxt,adoptbytxt,adoptnametxt,adoptmobiletxt,ownertxt,paymentidtxt,orderidtxt,amount,appointmentdatetxt;
    TextView processstatustxt,boxfixdatetxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_respective);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Adopt Details");
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
        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");
        paymentDetails=(PaymentDetails) bundle.getSerializable("paydetails");
        bundle1=new Bundle();
        bundle1.putSerializable("userdetails", userDetails);
        paymentid=paymentDetails.paymentid;
        boxid=paymentDetails.boxid;
        userkey=paymentDetails.userkey;
        userid=paymentDetails.userid;
        transdate=paymentDetails.transdate;
        processstatus=paymentDetails.processstatus;
        fdatabase=FirebaseDatabase.getInstance();
        transactions=fdatabase.getReference("boxes_transactions").child(paymentid);
        boxes=fdatabase.getReference("sparrow_boxes").child(boxid);
        users=fdatabase.getReference("sparrow_users").child(userkey);
        encryptionDecryption=new EncryptionDecryption();
        boxidtxt=findViewById(R.id.boxid);
        addresstxt=findViewById(R.id.address);
        landmarktxt=findViewById(R.id.landmark);
        locationstxt=findViewById(R.id.locations);
        ownertxt=findViewById(R.id.ownername);
        adoptbytxt=findViewById(R.id.adoptby);
        adoptnametxt=findViewById(R.id.adoptname);
        adoptmobiletxt=findViewById(R.id.mobile);
        orderidtxt=findViewById(R.id.orderid);
        amount=findViewById(R.id.amount);
        processstatustxt=findViewById(R.id.processstatus);
        paymentidtxt=findViewById(R.id.paymentid);
        appointmentdatetxt=findViewById(R.id.appointmentdate);
        boxfixdatetxt=findViewById(R.id.boxfixdate);
        boxidtxt.setText(boxid);
        adoptbytxt.setText("Adopt By\t\t\t\t|\t"+userid);
        paymentidtxt.setText(paymentid);
        processstatustxt.setText("Process\t\t\t\t\t|\t"+processstatus);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
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
        intent=new Intent(PaymentActivityRespective.this,MainActivity.class);
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
        transactions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appointmentdatest=(String) dataSnapshot.child("appointmentdate").getValue();
                adoptboxfixdatest=(String) dataSnapshot.child("adoptboxfixdate").getValue();
                orderidst=(String) dataSnapshot.child("orderId").getValue();
                amountst=(String) dataSnapshot.child("amount").getValue();
                orderidtxt.setText("Order ID\t\t\t\t|\t"+orderidst);
                amount.setText("Amount\t\t\t\t\t|\t"+amountst);
                if(!appointmentdatest.equals("Not Fixed")) {
                    appointmentdatetxt.setText("Appointment\t|\t" + appointmentdatest);
                    appointmentdatetxt.setVisibility(View.VISIBLE);
                }
                if(!adoptboxfixdatest.equals("Not Fixed")) {
                    boxfixdatetxt.setText("Box Fixed\t\t\t|\t" + adoptboxfixdatest);
                    boxfixdatetxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adoptname=encryptionDecryption.decrypt((String)dataSnapshot.child("name").getValue());
                adoptmobile=encryptionDecryption.decrypt((String)dataSnapshot.child("mobile").getValue());
                adoptnametxt.setText("Name\t\t\t\t\t\t|\t"+adoptname);
                adoptmobiletxt.setText("Mobile\t\t\t\t\t\t|\t"+adoptmobile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        boxes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latitude = Double.parseDouble(encryptionDecryption.decrypt((String) dataSnapshot.child("latitude").getValue()));
                landmarkst=encryptionDecryption.decrypt((String) dataSnapshot.child("landmark").getValue());
                locationsst=encryptionDecryption.decrypt((String) dataSnapshot.child("locations").getValue());
                longitude = Double.parseDouble(encryptionDecryption.decrypt((String) dataSnapshot.child("longitude").getValue()));
                addressst=encryptionDecryption.decrypt((String) dataSnapshot.child("address").getValue());
                adoptst=encryptionDecryption.decrypt((String) dataSnapshot.child("adopt").getValue());
                ownerst=encryptionDecryption.decrypt((String) dataSnapshot.child("ownername").getValue());
                if (latitude != 0.0 && longitude != 0.0) {
                    LatLng latlng = new LatLng(latitude, longitude);
//                    gmap.setMinZoomPreference(12);
//                    gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
                    mMap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(addressst)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
                }
                addresstxt.setText("Address\t\t\t\t\t|\t"+addressst+".");
                ownertxt.setText("Owner Name\t|\t"+ownerst);
                locationstxt.setText("Locations\t\t\t\t|\t"+locationsst);
                landmarktxt.setText("Landmark\t\t\t|\t"+landmarkst);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
