package com.svce.sparrowpro;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SparrowBoxMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Bundle bundle,bundle1;
    UserDetails userDetails;
    FirebaseDatabase fdatabase;
    DatabaseReference dreference1;
    String address,locations,adopt,landmark,owner;
    double latitude,longitude;
    EncryptionDecryption encryptionDecryption;
    HashMap<Marker,HashMap> markermap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sparrow_box_maps);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Sparrow Boxes");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        };
        t.start();

        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");

        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markermap=new HashMap<>();
        encryptionDecryption=new EncryptionDecryption();
        fdatabase= FirebaseDatabase.getInstance();
        dreference1=fdatabase.getReference("sparrow_boxes");

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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        dreference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot s:dataSnapshot.getChildren())
                {
                    latitude = Double.parseDouble(encryptionDecryption.decrypt((String) s.child("latitude").getValue()));
                    longitude = Double.parseDouble(encryptionDecryption.decrypt((String) s.child("longitude").getValue()));
                    if(latitude!=0.0 && longitude!=0.0)
                    {
                        address=encryptionDecryption.decrypt(s.child("address").getValue(String.class));
                        adopt=encryptionDecryption.decrypt(s.child("adopt").getValue(String.class));
                        landmark=encryptionDecryption.decrypt(s.child("landmark").getValue(String.class));
                        locations=encryptionDecryption.decrypt(s.child("locations").getValue(String.class));
                        owner=encryptionDecryption.decrypt(s.child("ownername").getValue(String.class));
                        if(locations!=null && locations.equals("Different Location"))
                        {
                            locations="Approximate Location";
                        }
                        else
                        {
                            locations="Exact Location";
                        }
                        LatLng latlng = new LatLng(latitude, longitude);
                        Marker marker=mMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                        final HashMap<String,String> mapDetails=new HashMap<>();
                        mapDetails.put("address",address);
                        mapDetails.put("adopt",adopt);
                        mapDetails.put("landmark",landmark);
                        mapDetails.put("locations",locations);
                        mapDetails.put("owner",owner);
                        markermap.put(marker,mapDetails);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 6));


                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                HashMap<String,String> mapdet=markermap.get(marker);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SparrowBoxMapsActivity.this);
                LayoutInflater inflater = SparrowBoxMapsActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.main_tab3, null);
                dialogBuilder.setView(dialogView);
                TextView ownertxt =  dialogView.findViewById(R.id.owner);
                TextView addresstxt =  dialogView.findViewById(R.id.address);
                TextView landmarktxt =  dialogView.findViewById(R.id.landmark);
                TextView locationstxt =  dialogView.findViewById(R.id.locations);
                TextView adopttxt =  dialogView.findViewById(R.id.adopt);
                ownertxt.setText("Owner Name : "+mapdet.get("owner"));
                addresstxt.setText("Address : "+mapdet.get("address"));
                landmarktxt.setText("Landmark : "+mapdet.get("landmark"));
                locationstxt.setText("Locations : "+mapdet.get("locations")+".");
                if(mapdet.get("adopt").equals("unadopted")) {
                    adopttxt.setText("Not Adopted.");
                    adopttxt.setTextColor(getResources().getColor(R.color.denied));
                }
                else
                {
                    adopttxt.setText("Adopted.");
                    adopttxt.setTextColor(getResources().getColor(R.color.accepted));
                }
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SparrowBoxMapsActivity.this,MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        finish();
    }
}
