package com.svce.sparrowpro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class AdoptSparrowRespective extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap mMap;
    FirebaseDatabase fdatabase;
    DatabaseReference boxes;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    CoordinatorLayout cardLayout;
    CardView cardView;
    UserDetails userDetails;
    BoxDetails boxDetails;
    Bundle bundle,bundle1;
    Intent intent;
    String boxidst,ownerst,addressst,landmarkst,locationsst,adoptst,adoptbyst;
    Double latitude,longitude;
    TextView boxid,owner,address,landmark,locations,adopt,adoptby;
    Button payment;
    EncryptionDecryption encryptionDecryption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt_sparrow_respective);
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
        boxDetails=(BoxDetails) bundle.getSerializable("boxdetails");
        bundle1=new Bundle();
        bundle1.putSerializable("userdetails", userDetails);
        boxid=findViewById(R.id.boxid);
        owner=findViewById(R.id.ownername);
        address=findViewById(R.id.address);
        landmark=findViewById(R.id.landmark);
        locations=findViewById(R.id.locations);
        adopt=findViewById(R.id.adopt);
        adoptby=findViewById(R.id.adoptby);
        payment=findViewById(R.id.payment);
        boxidst=boxDetails.boxkey;
        addressst=boxDetails.address;
        adoptst=boxDetails.adopt;
        if(adoptst.equals("unadopted"))
        {
            adopt.setText("Adopt\t\t\t\t\t\t|\tNot adopted");
            payment.setVisibility(View.VISIBLE);
            adoptby.setVisibility(View.GONE);
        }
        else
        {
            adopt.setText("Adopt\t\t\t\t\t\t|\tAdopted");
            adoptby.setVisibility(View.VISIBLE);
            payment.setVisibility(View.GONE);
        }


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        fdatabase= FirebaseDatabase.getInstance();
        boxes=fdatabase.getReference("sparrow_boxes").child(boxidst);
        encryptionDecryption=new EncryptionDecryption();

        boxid.setText(boxidst);
        address.setText("Address\t\t\t\t\t|\t"+addressst);


        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getMobileDataState())
                {
                    Snackbar.make(view,"Turn On Mobile Data!!!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
                else {
                    AlertDialog.Builder paydialog = new AlertDialog.Builder(AdoptSparrowRespective.this);
                    paydialog.setTitle("Payment");
                    paydialog.setMessage("Do not cancel process till token generation!");
                    paydialog.setNegativeButton("Cancel", null);
                    paydialog.setCancelable(false);
                    paydialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            bundle1.putSerializable("boxdetails", boxDetails);
                            intent = new Intent(AdoptSparrowRespective.this, PaymentActivity.class);
                            intent.putExtras(bundle1);
                            startActivity(intent);
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                            finish();
                        }
                    });
                    AlertDialog payalertdialog = paydialog.create();
                    payalertdialog.show();
                }
            }
        });
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
        intent=new Intent(AdoptSparrowRespective.this,AdoptSparrow.class);
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
        boxes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latitude = Double.parseDouble(encryptionDecryption.decrypt((String) dataSnapshot.child("latitude").getValue()));
                landmarkst=encryptionDecryption.decrypt((String) dataSnapshot.child("landmark").getValue());
                locationsst=encryptionDecryption.decrypt((String) dataSnapshot.child("locations").getValue());
                longitude = Double.parseDouble(encryptionDecryption.decrypt((String) dataSnapshot.child("longitude").getValue()));
                ownerst=encryptionDecryption.decrypt((String) dataSnapshot.child("ownername").getValue());
                adoptbyst=encryptionDecryption.decrypt((String) dataSnapshot.child("adoptby").getValue());
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
                owner.setText("Owner Name\t|\t"+ownerst);
                locations.setText("Locations\t\t\t\t|\t"+locationsst);
                landmark.setText("Landmark\t\t\t|\t"+landmarkst);
                if(!adoptst.equals("unadopted"))
                {
                    adoptby.setVisibility(View.VISIBLE);
                    payment.setVisibility(View.GONE);
                    adoptby.setText("Adopt By\t\t\t\t|\t"+adoptbyst);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
