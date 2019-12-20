package com.svce.sparrowpro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Admin_Tab4 extends Fragment {
    CoordinatorLayout cardLayout;
    CardView cardView;
    MaterialAnimatedSwitch addrswitch;
    String ownerst = "", streetst, areast, cityst, statest, landmarkst = "", address, locationsst;
    String uniquekey;
    View parentView;
    double latitude, longitude;
    LocationManager locationManager;
    LocationListener locationListener;
    EncryptionDecryption encryptionDecryption;
    GoogleApiClient googleApiClient;
    EditText ownertxt, streettxt, areatxt, citytxt, statetxt, landmarktxt;
    Button report;
    TextView addrtext;
    int boxlocation = 0;
    StringBuilder stringBuilder;
    FirebaseDatabase fdatabase;
    DatabaseReference dreference1, locations;
    ProgressDialog progressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.admin_tab4, container, false);
        parentView = getActivity().findViewById(android.R.id.content);
        cardLayout = rootView.findViewById(R.id.cardlayout);
        cardView = rootView.findViewById(R.id.card);
        addrtext = (TextView) rootView.findViewById(R.id.addrtext);
        addrswitch = rootView.findViewById(R.id.addrswitch);
        ownertxt = (EditText) rootView.findViewById(R.id.owner);
        streettxt = (EditText) rootView.findViewById(R.id.street);
        areatxt = (EditText) rootView.findViewById(R.id.area);
        citytxt = (EditText) rootView.findViewById(R.id.city);
        statetxt = (EditText) rootView.findViewById(R.id.state);
        landmarktxt = (EditText) rootView.findViewById(R.id.landmark);
        report = (Button) rootView.findViewById(R.id.report);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                        500.0f, 0.0f);
                animation.setDuration(1200);
                animation.setFillAfter(true);
                cardView.setAnimation(animation);
                cardView.setVisibility(View.VISIBLE);
            }
        }, 100);

        if(!getMobileDataState())
        {
            Snackbar.make(parentView,"Turn On Mobile Data!",Snackbar.LENGTH_LONG).show();
        }

        encryptionDecryption = new EncryptionDecryption();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API).build();
        fdatabase = FirebaseDatabase.getInstance();
        dreference1 = fdatabase.getReference("sparrow_boxes");
        stringBuilder=new StringBuilder();

//        displayLocationSettingsRequest(getActivity());
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                displayLocationSettingsRequest(getActivity());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        addrswitch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean b) {
                if (b) {
                    boxlocation = 1;
                    addrtext.setVisibility(View.GONE);
                    streettxt.setVisibility(View.GONE);
                    areatxt.setVisibility(View.GONE);
                    citytxt.setVisibility(View.GONE);
                    statetxt.setVisibility(View.GONE);
                    callLocationListener();
                } else {
                    locationManager.removeUpdates(locationListener);
                    boxlocation = 0;
                    addrtext.setVisibility(View.VISIBLE);
                    streettxt.setVisibility(View.VISIBLE);
                    areatxt.setVisibility(View.VISIBLE);
                    citytxt.setVisibility(View.VISIBLE);
                    statetxt.setVisibility(View.VISIBLE);
                }
            }
        });


        report.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(final View view) {
                displayLocationSettingsRequest(getActivity());
                ownerst = ownertxt.getText().toString();
                landmarkst = landmarktxt.getText().toString();
                if (TextUtils.isEmpty(ownerst) || ownerst == null || ownerst.trim().length() == 0)
                    ownerst = "Not Provided.";
                if (TextUtils.isEmpty(landmarkst) || landmarkst == null || landmarkst.trim().length() == 0)
                    landmarkst = "Not Provided.";

                if (boxlocation == 0) {
                    streetst = streettxt.getText().toString();
                    areast = areatxt.getText().toString();
                    cityst = citytxt.getText().toString();
                    statest = statetxt.getText().toString();
                    if (!streetst.matches("[a-zA-Z0-9\\W ]+$")) {
                        streettxt.setError("Enter Proper Street");
                        streettxt.requestFocus();
                    } else if (!areast.matches("[a-zA-Z ]+$")) {
                        areatxt.setError("Enter Proper Area");
                        areatxt.requestFocus();
                    } else if (!cityst.matches("[a-zA-Z ]+$")) {
                        citytxt.setError("Enter Proper City");
                        citytxt.requestFocus();
                    } else if (!statest.matches("[a-zA-Z ]+$")) {
                        statetxt.setError("Enter Proper State");
                        statetxt.requestFocus();
                    } else if (!getMobileDataState()) {
                        Snackbar.make(view, "Turn On Mobile Data!!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {

                        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Location",
                                "Fetching Sparrow's Location Data", true);
                        final Handler handler = new Handler() {
                            public void handleMessage(Message msg) {
                                dialog.dismiss();
                                UploadBox uploadBox = new UploadBox(getActivity(), view);
                                uploadBox.execute("");
                            }
                        };
                        Thread t = new Thread() {
                            public void run() {
                                address = streetst + ", " + areast + ", " + cityst + ", " + statest + ", " + "India.";
                                Geocoder coder = new Geocoder(getActivity());
                                List<Address> addresslist;
                                try {
                                    addresslist = coder.getFromLocationName(address, 5);
                                    Address location = addresslist.get(0);
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    locationsst = "Different Location";
                                } catch (Exception e) {
                                }
                                handler.sendEmptyMessage(0);
                            }
                        };
                        t.start();
                    }

                } else {
                    if (!getMobileDataState()) {
                        Snackbar.make(view, "Turn On Mobile Data!!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (latitude != 0.0 && longitude != 0.0) {
                        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Location",
                                "Fetching Current Location Data", true);
                        final Handler handler = new Handler() {
                            public void handleMessage(Message msg) {
                                dialog.dismiss();
                                UploadBox uploadBox = new UploadBox(getActivity(), view);
                                uploadBox.execute("");
                            }
                        };
                        Thread t = new Thread() {
                            public void run() {
                                try {
                                    Geocoder geocoder;
                                    List<Address> addresses;
                                    geocoder = new Geocoder(getActivity(), Locale.getDefault());

                                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    if (addresses != null && addresses.size() > 0) {
                                        address = addresses.get(0).getAddressLine(0)+".";
                                        locationsst = "Current Location";
                                    }
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                handler.sendEmptyMessage(0);
                            }
                        };
                        t.start();
                    } else {
                        final Handler handler = new Handler() {
                            public void handleMessage(Message msg) {
                                Snackbar.make(view, "Sorry!Unable to Find Location. Retry Again!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        };
                        Thread t = new Thread() {
                            public void run() {
                                callLocationListener();
                                handler.sendEmptyMessage(0);
                            }
                        };
                        t.start();
                    }
                }

            }
        });


        return rootView;
    }

    private void callLocationListener() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
                displayLocationSettingsRequest(getActivity());
            }
            return;
        } else {
            displayLocationSettingsRequest(getActivity());
        }
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiinfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileiinfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(mobileiinfo!=null && mobileiinfo.isAvailable()&& mobileiinfo.isConnectedOrConnecting())
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }
        else if(wifiinfo!=null && wifiinfo.isAvailable() && wifiinfo.isConnectedOrConnecting()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }
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


    private void displayLocationSettingsRequest(final Context context) {
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(LocationSettingsResult result) {
                final int REQUEST_LOCATION = 199;
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
//                        Toast.makeText(context, "All location settings are satisfied.", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        Toast.makeText(context, "Location settings are not satisfied. Show the user a dialog to upgrade location settings", Toast.LENGTH_SHORT).show();
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            Toast.makeText(context, "Unable to TurnOn Location.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(context, "Location settings are inadequate, and cannot be fixed here.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public class UploadBox extends AsyncTask<String, String, String> {
        Context context;
        View view;

        UploadBox(Context context, View view) {
            this.view = view;
            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            uniquekey=dreference1.push().getKey();
            locations = dreference1.child("box"+uniquekey);
            HashMap<String, Object> locationmap = new HashMap<>();
            locationmap.put("address", encryptionDecryption.encrypt(address));
            locationmap.put("latitude", encryptionDecryption.encrypt(String.valueOf(latitude)));
            locationmap.put("locations", encryptionDecryption.encrypt(locationsst));
            locationmap.put("longitude", encryptionDecryption.encrypt(String.valueOf(longitude)));
            locationmap.put("ownername", encryptionDecryption.encrypt(ownerst));
            locationmap.put("landmark", encryptionDecryption.encrypt(landmarkst));
            locationmap.put("boxkey", encryptionDecryption.encrypt("box"+uniquekey));
            locationmap.put("adopt", encryptionDecryption.encrypt("unadopted"));
            locationmap.put("adoptby", encryptionDecryption.encrypt("-"));
            locations.updateChildren(locationmap);


            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Uploading Box Data....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Submitting..Please Wait....");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            if (addrswitch.isChecked()) {
                addrswitch.toggle();
                boxlocation = 0;
            }
            streettxt.setText("");
            areatxt.setText("");
            citytxt.setText("");
            statetxt.setText("");
            landmarktxt.setText("");
            ownertxt.setText("");
            latitude = 0.0;
            longitude = 0.0;
            progressDialog.dismiss();
            Snackbar.make(view, "Upload Successful!!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            ownertxt.requestFocus();
        }
    }
}
