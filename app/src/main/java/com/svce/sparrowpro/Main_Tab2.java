package com.svce.sparrowpro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.transition.Slide;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitchState;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class Main_Tab2 extends Fragment {
    CardView cardView;
    //    MaterialAnimatedSwitch addrswitch, nestingswitch, boxswitch;
    TextView gendertext, addrtext, boxtext;
    RadioGroup gendergroup, boxgroup, boxprovided, nestinggroup, locationgroup;
    RadioButton unknown, male, female, requestyes, requestno, locationyes, locationno, nestingyes, nestingno, providedyes, providedno;
    LinearLayout numsparrowlayout;
    EditText streettxt, areatxt, citytxt, statetxt, landmark, description;
    Spinner numyng;
    int temp, profimagestatus = 0, sparrowlocation = 0, sparrownesting = 0, sparrowyoungones = 0, spaarrowbox = 0;
    Button report;
    static final int PICK_IMAGE_REQUEST = 1;
    ImageButton sparrowimage;
    Uri sparrowimageuri;
    String genderst, descriptionst, dateuploaded, nestingst, numofsparrows, boxst, requestst;
    String streetst, areast, cityst, statest, landmarkst, address, locationsst;
    double latitude, longitude;
    RadioButton radioButton;
    LocationManager locationManager;
    LocationListener locationListener;
    ProgressDialog progressDialog;
    UserDetails userDetails;
    public static String username, mobile, name, rootkey;
    String substr1, substr2, userkey, lat, lng;
    FirebaseDatabase fdatabase;
    DatabaseReference dreference1, dreference2, sparrows, locations;
    FirebaseStorage fstorage;
    StorageReference sreference, userimages;
    StorageTask fuploadtask;
    EncryptionDecryption encryptionDecryption;
    GoogleApiClient googleApiClient;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.main_tab2, container, false);

        final String numarr[] = new String[10];
        for (temp = 0; temp < 10; temp++) {
            numarr[temp] = String.valueOf(temp + 1);
        }

        cardView = rootView.findViewById(R.id.card);
        numyng = (Spinner) rootView.findViewById(R.id.numyng);
        ArrayAdapter<String> NumAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, numarr);
        NumAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        numyng.setAdapter(NumAdapter);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        dateuploaded = df.format(c);

        userDetails = (UserDetails) getArguments().getSerializable("userdetails");
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API).build();
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

        username = userDetails.username;
        mobile = userDetails.mobile;
        name = userDetails.name;
        rootkey = userDetails.userkey;

        encryptionDecryption = new EncryptionDecryption();
        fdatabase = FirebaseDatabase.getInstance();
        dreference1 = fdatabase.getReference("sparrow_uploads");
        dreference2 = fdatabase.getReference("sparrow_locations");
        fstorage = FirebaseStorage.getInstance();
        sreference = fstorage.getReference("sparrow_uploads");

        gendertext = (TextView) rootView.findViewById(R.id.gendertext);
        boxtext = (TextView) rootView.findViewById(R.id.boxtext);
        addrtext = (TextView) rootView.findViewById(R.id.addrtext);
        gendergroup = (RadioGroup) rootView.findViewById(R.id.gendergroup);
        locationgroup = (RadioGroup) rootView.findViewById(R.id.locationgroup);
        nestinggroup = (RadioGroup) rootView.findViewById(R.id.nestgroup);
        boxprovided = (RadioGroup) rootView.findViewById(R.id.boxprovided);
        boxgroup = (RadioGroup) rootView.findViewById(R.id.boxgroup);
        unknown = (RadioButton) rootView.findViewById(R.id.unknown);
        male = (RadioButton) rootView.findViewById(R.id.male);
        female = (RadioButton) rootView.findViewById(R.id.female);
        requestyes = (RadioButton) rootView.findViewById(R.id.requestyes);
        requestno = (RadioButton) rootView.findViewById(R.id.requestno);
        locationyes = (RadioButton) rootView.findViewById(R.id.locyes);
        locationno = (RadioButton) rootView.findViewById(R.id.locno);
        nestingyes = (RadioButton) rootView.findViewById(R.id.nestyes);
        nestingno = (RadioButton) rootView.findViewById(R.id.nestno);
        providedyes = (RadioButton) rootView.findViewById(R.id.providedyes);
        providedno = (RadioButton) rootView.findViewById(R.id.providedno);
        streettxt = (EditText) rootView.findViewById(R.id.street);
        areatxt = (EditText) rootView.findViewById(R.id.area);
        citytxt = (EditText) rootView.findViewById(R.id.city);
        statetxt = (EditText) rootView.findViewById(R.id.state);
        landmark = (EditText) rootView.findViewById(R.id.landmark);
        description = (EditText) rootView.findViewById(R.id.description);
        numsparrowlayout = (LinearLayout) rootView.findViewById(R.id.numsparrowlayout);
        report = (Button) rootView.findViewById(R.id.report);
        sparrowimage = rootView.findViewById(R.id.sparrowimage);


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
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 2000);


        locationgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.locyes: {
                        sparrowlocation = 1;
                        addrtext.setVisibility(View.GONE);
                        streettxt.setVisibility(View.GONE);
                        areatxt.setVisibility(View.GONE);
                        citytxt.setVisibility(View.GONE);
                        statetxt.setVisibility(View.GONE);
                        landmark.setVisibility(View.GONE);
                        callLocationListener();
                        break;
                    }
                    case R.id.locno: {
                        locationManager.removeUpdates(locationListener);
                        sparrowlocation = 0;
                        addrtext.setVisibility(View.VISIBLE);
                        streettxt.setVisibility(View.VISIBLE);
                        areatxt.setVisibility(View.VISIBLE);
                        citytxt.setVisibility(View.VISIBLE);
                        statetxt.setVisibility(View.VISIBLE);
                        landmark.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });

        nestinggroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.nestyes:
                        sparrownesting = 1;
                        break;
                    case R.id.nestno:
                        sparrownesting = 0;
                        break;
                }
            }
        });

        boxprovided.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.providedyes:
                        spaarrowbox = 1;
                        boxtext.setVisibility(View.GONE);
                        boxgroup.setVisibility(View.GONE);
                        requestyes.setChecked(true);
                        break;
                    case R.id.providedno:
                        spaarrowbox = 0;
                        boxtext.setVisibility(View.VISIBLE);
                        boxgroup.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        sparrowimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
//                startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
                openFileChooser();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(final View view) {
                displayLocationSettingsRequest(getActivity());


                if (sparrownesting == 0) {
                    nestingst = "No";
                } else {
                    nestingst = "Yes";
                }

                if (spaarrowbox == 0) {
                    boxst = "Not Provided";
                    temp = boxgroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) rootView.findViewById(temp);
                    requestst = radioButton.getText().toString();
                } else {
                    boxst = "Provided";
                    requestst = "-";
                }
                temp = gendergroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) rootView.findViewById(temp);
                genderst = radioButton.getText().toString();
                descriptionst = description.getText().toString();
                temp = numyng.getSelectedItemPosition();
                numofsparrows = numarr[temp];

                if ((descriptionst.trim().length() == 0)) {
                    description.setError("Sparrow's Short Description");
                    description.requestFocus();
                } else if (profimagestatus == 0) {
                    Snackbar.make(view, "Upload Sparrow Image!!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    if (sparrowlocation == 0) {
                        streetst = streettxt.getText().toString();
                        areast = areatxt.getText().toString();
                        cityst = citytxt.getText().toString();
                        statest = statetxt.getText().toString();
                        landmarkst = landmark.getText().toString();
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
                        } else if (!landmarkst.matches("[a-zA-Z ]+$")) {
                            landmark.setError("Enter Proper Landmark");
                            landmark.requestFocus();
                        } else if (!getMobileDataState()) {
                            Snackbar.make(view, "Turn On Mobile Data!!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        } else {

                            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Location",
                                    "Fetching Sparrow's Location Data", true);
                            final Handler handler = new Handler() {
                                public void handleMessage(Message msg) {
                                    dialog.dismiss();
                                    UploadSparrow uploadSparrow = new UploadSparrow(getActivity(), view);
                                    uploadSparrow.execute("");
//                                Toast.makeText(getActivity(), "" + latitude+longitude, Toast.LENGTH_SHORT).show();
//                                Snackbar.make(view,""+latitude+longitude,Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                }
                            };
                            Thread t = new Thread() {
                                public void run() {
                                    address = streetst + "," + areast + "," + cityst + "," + statest + "," + "India";
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
                                    UploadSparrow uploadSparrow = new UploadSparrow(getActivity(), view);
                                    uploadSparrow.execute("");
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
                                            address = addresses.get(0).getAddressLine(0);
                                            streetst = "-";
                                            areast = "-";
                                            cityst = addresses.get(0).getLocality();
                                            statest = addresses.get(0).getAdminArea();
                                            landmarkst = "-";
                                            locationsst = "Current Location";
                                            handler.sendEmptyMessage(0);
                                        } else {
                                            dialog.dismiss();
                                            Snackbar.make(view, "Poor Network Area!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                        }
                                    } catch (SecurityException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
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
            }
        });


        return rootView;

    }

    private void callLocationListener() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// TODO: Consider calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
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
        if (mobileiinfo != null && mobileiinfo.isAvailable() && mobileiinfo.isConnectedOrConnecting()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        } else if (wifiinfo != null && wifiinfo.isAvailable() && wifiinfo.isConnectedOrConnecting()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }
    }


    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = CropImage.getPickImageResultUri(getActivity(), data);
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {
                sparrowimageuri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCropImageActivity(imageUri);
            }

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                sparrowimageuri = result.getUri();
                Glide.with(getActivity()).load(sparrowimageuri).into(sparrowimage);
                sparrowimage.setScaleType(ImageView.ScaleType.FIT_XY);
                profimagestatus = 1;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (sparrowimageuri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(sparrowimageuri);
        } else {
            Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    public void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 2)
                .setMultiTouchEnabled(true)
                .start(getActivity(), this);
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

    public class UploadSparrow extends AsyncTask<String, String, String> {
        Context context;
        View view;

        UploadSparrow(Context context, View view) {
            this.view = view;
            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            int j = username.indexOf('@');
            substr1 = username.substring(0, j);
            substr1 = substr1.replace(".", "&");
            substr2 = username.substring(j + 1, username.length());
            String[] substr2arr = substr2.split("\\.");
            substr2 = "";
            for (j = 0; j < substr2arr.length; j++) {
                substr2 = substr2 + substr2arr[j];
            }
            substr1 = (substr1 + substr2).trim();
            userkey = substr1;
            substr1 = String.valueOf(latitude).replace(".", "&");
            substr2 = String.valueOf(longitude).replace(".", "&");
            substr1 += "|" + substr2;
            userkey += substr1;
            lat = String.valueOf(latitude);
            lng = String.valueOf(longitude);
            userimages = sreference.child(rootkey).child(userkey);
            sparrows = dreference1.child(rootkey).child(userkey);
            locations = dreference2.child(substr1);


//                    Toast.makeText(RegistrationActivity.this, ""+userkey, Toast.LENGTH_SHORT).show();


            fuploadtask = userimages.putFile(sparrowimageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            Thread thread1 = new Thread() {
                                public void run() {
                                    HashMap<String, Object> reportmap = new HashMap<>();
                                    reportmap.put("sparrowimageurl", encryptionDecryption.encrypt(taskSnapshot.getDownloadUrl().toString()));
                                    reportmap.put("userid", encryptionDecryption.encrypt(username));
                                    reportmap.put("userkey", encryptionDecryption.encrypt(rootkey));
                                    reportmap.put("reportid", encryptionDecryption.encrypt(userkey));
                                    reportmap.put("reportuploaded", encryptionDecryption.encrypt(dateuploaded));
                                    reportmap.put("address", encryptionDecryption.encrypt(address));
                                    reportmap.put("area", encryptionDecryption.encrypt(areast));
                                    reportmap.put("city", encryptionDecryption.encrypt(cityst));
                                    reportmap.put("description", encryptionDecryption.encrypt(descriptionst));
                                    reportmap.put("gender", encryptionDecryption.encrypt(genderst.trim()));
                                    reportmap.put("landmark", encryptionDecryption.encrypt(landmarkst));
                                    reportmap.put("latitude", encryptionDecryption.encrypt(lat));
                                    reportmap.put("locations", encryptionDecryption.encrypt(locationsst));
                                    reportmap.put("longitude", encryptionDecryption.encrypt(lng));
                                    reportmap.put("nesting", encryptionDecryption.encrypt(nestingst));
                                    reportmap.put("numbersparrows", encryptionDecryption.encrypt(numofsparrows));
                                    reportmap.put("sparrowbox", encryptionDecryption.encrypt(boxst));
                                    reportmap.put("sparrowboxrequest", encryptionDecryption.encrypt(requestst));
                                    reportmap.put("state", encryptionDecryption.encrypt(statest));
                                    reportmap.put("street", encryptionDecryption.encrypt(streetst));
                                    reportmap.put("locationkey", encryptionDecryption.encrypt(substr1));
                                    reportmap.put("status", encryptionDecryption.encrypt("Pending..."));
                                    sparrows.updateChildren(reportmap);
                                }
                            };
                            Thread thread2 = new Thread() {
                                public void run() {
                                    HashMap<String, Object> locationmap = new HashMap<>();
                                    locationmap.put("locationkey", encryptionDecryption.encrypt(substr1));
                                    locationmap.put("address", encryptionDecryption.encrypt(address));
                                    locationmap.put("latitude", encryptionDecryption.encrypt(lat));
                                    locationmap.put("locations", encryptionDecryption.encrypt(locationsst));
                                    locationmap.put("longitude", encryptionDecryption.encrypt(lng));
                                    locationmap.put("sparrowbox", encryptionDecryption.encrypt(boxst));
                                    locationmap.put("sparrowboxrequest", encryptionDecryption.encrypt(requestst));
                                    locationmap.put("adopt", encryptionDecryption.encrypt("unadopted"));
                                    locationmap.put("status", encryptionDecryption.encrypt("Pending..."));
                                    locations.updateChildren(locationmap);
                                }
                            };
                            sparrowimageuri = null;
                            sparrowimage.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_library_black_24dp));
                            sparrowimage.setScaleType(ImageView.ScaleType.CENTER);
                            profimagestatus = 0;
//                            if (addrswitch.isChecked()) {
//                                addrswitch.toggle();
//                            }
//                            if (nestingswitch.isChecked()) {
//                                nestingswitch.toggle();
//                            }
//                            if (boxswitch.isChecked()) {
//                                boxswitch.toggle();
//                            }
                            numyng.setSelection(0);
                            streettxt.setText("");
                            areatxt.setText("");
                            citytxt.setText("");
                            statetxt.setText("");
                            landmark.setText("");
                            description.setText("");
                            latitude = 0.0;
                            longitude = 0.0;
                            unknown.setChecked(true);
                            locationno.setChecked(true);
                            nestingno.setChecked(true);
                            providedno.setChecked(true);
                            requestyes.setChecked(true);
                            sparrowlocation = 0;
                            sparrownesting = 0;
                            sparrowyoungones = 0;
                            spaarrowbox = 0;
                            thread1.start();
                            thread2.start();
                            progressDialog.dismiss();
                            Snackbar.make(view, "Upload Successful!!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            streettxt.requestFocus();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            if (e.getMessage().toString().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                Toast.makeText(getActivity(), "Please Check Your Internet Connection!!!", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setIndeterminate(true);
                            progressDialog.setTitle("Uploading Sparrow Data....");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Submitting..Please Wait....");
                            progressDialog.show();
                        }
                    });
            ;

            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String s) {
        }
    }
}
