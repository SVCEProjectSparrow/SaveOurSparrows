package com.svce.sparrowpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SparrowAnalyseMapsActivity extends AppCompatActivity implements OnMapReadyCallback {       //FragmentActivity

    private GoogleMap mMap;
    Bundle bundle,bundle1;
    UserDetails userDetails;
    FirebaseDatabase fdatabase;
    DatabaseReference dreference1;
    String locations,status,address;
    double latitude,longitude;
    EncryptionDecryption encryptionDecryption;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sparrow_analyse_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");

        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);

        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Sparrow Locations");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        };
        t.start();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        encryptionDecryption=new EncryptionDecryption();
        fdatabase= FirebaseDatabase.getInstance();
        dreference1=fdatabase.getReference("sparrow_locations");
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
                    status=encryptionDecryption.decrypt(s.child("status").getValue(String.class));
                    if(latitude!=0.0 && longitude!=0.0 && status!=null && status.equals("Accepted")) {
//                        locations=encryptionDecryption.decrypt(s.child("locations").getValue(String.class));
//                        if(locations!=null && locations.equals("Different Location"))
//                        {
//                            locations="Approximate Location";
//                        }
//                        else
//                        {
//                            locations="Exact Location";
//                        }
                        address=encryptionDecryption.decrypt(s.child("address").getValue(String.class));
                        LatLng latlng = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(address)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 6));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onBackPressed() {
        Intent intent=new Intent(SparrowAnalyseMapsActivity.this,MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        finish();
    }
}
