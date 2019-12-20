package com.svce.sparrowpro;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.CropTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static String username,hierarchy,name,profileurl,userkey,tnc,password;
    UserDetails userDetails;
    EncryptionDecryption encryptionDecryption;
    ImageView nav_profileimage;
    TextView nav_name,nav_username;
    Bundle bundle,bundle1;
    FirebaseAuth fauth;
    FirebaseUser fuser;
    View parentView;
    Typeface typeface;
    GoogleApiClient googleApiClient;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    DatabaseReference users;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        AssetManager am = getApplicationContext().getAssets();
//        typeface = Typeface.createFromAsset(am,
//                String.format(Locale.US, "fonts/%s", "nunito.ttf"));
//        this.setTypeface(typeface);

        LinearLayout toolbarlayout=findViewById(R.id.toolbarlayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        toolbar.setNavigationIcon(R.drawable.ic_menu_camera);
//        toolbar.setLogo(R.drawable.ic_menu_gallery);
        parentView = findViewById(android.R.id.content);
        fauth=FirebaseAuth.getInstance();
        fuser=fauth.getCurrentUser();

        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");

        permissionCheckStorage();
        permissionCheckLocation();
        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API).build();

        if(!getMobileDataState())
        {
            Snackbar.make(parentView,"Turn On Mobile Data!",Snackbar.LENGTH_LONG).setAction("Retry",null).show();
        }
        displayLocationSettingsRequest(MainActivity.this);
        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);

        encryptionDecryption=new EncryptionDecryption();

        userkey=userDetails.userkey;
        username=userDetails.username;
        name=userDetails.name;
        hierarchy=userDetails.hierarchy;
        profileurl=userDetails.profileurl;
        users= FirebaseDatabase.getInstance().getReference("sparrow_users").child(userkey);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                tnc = encryptionDecryption.decrypt( dataSnapshot.child("tnc").getValue().toString());
                hierarchy = encryptionDecryption.decrypt( dataSnapshot.child("hierarchy").getValue().toString());
                password = encryptionDecryption.decrypt( dataSnapshot.child("password").getValue().toString());
                userDetails.setTnc(tnc);
                userDetails.setHierarchy(hierarchy);
                userDetails.setPassword(password);
                sharedpreferences = getSharedPreferences("currentUser", 0);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(userDetails);
                editor.putString("currentUsers", "1");
                editor.putString("userjson", json);
                editor.commit();
                bundle1.putSerializable("userdetails", userDetails);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setBackgroundResource(0);
//        drawer.setStatusBarBackground(getResources().getColor(R.color.colorPrimaryDark));
        drawer.addDrawerListener(toggle);
        toggle.syncState();


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        nav_name=headerView.findViewById(R.id.nav_name);
        nav_username=headerView.findViewById(R.id.nav_username);
        nav_profileimage=headerView.findViewById(R.id.nav_profileimage);
        nav_name.setText(name);
        nav_username.setText(username);
        Picasso.get().load(profileurl)
                .placeholder(R.drawable.ic_account_circle_black_24dp)  //R.drawable.logo || R.mipmap.ic_launcher_round
                .resize(100, 100)
                .transform(new CropCircleTransformation())
                .centerCrop().into(nav_profileimage);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(hierarchy.equals("admins")) {
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        tabLayout.setupWithViewPager(mViewPager);

        if(tabLayout!=null)
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


    }

    private void permissionCheckStorage() {
        String s[]={ android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,};
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,s,1);
        }
    }

    private void permissionCheckLocation() {
        String s[]={android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.INTERNET};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,s,1);
        }
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
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(toggle.onOptionsItemSelected(item))
        {
            Toast.makeText(this, ""+item, Toast.LENGTH_SHORT).show();
//            return true;
        }
        if (id == R.id.action_viewProfile) {
                Thread thread=new Thread(){
                    public void run()
                    {
                        Intent intent=new Intent(MainActivity.this,ViewProfile.class);
                        intent.putExtras(bundle1);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);
                        finish();
                    }
                };
                thread.start();
        }
        if(id==R.id.action_logout){
            SharedPreferences.Editor editor = getSharedPreferences("currentUser", 0).edit();
            editor.putString("currentUsers","0");
            editor.putString("userjson","");
            editor.apply();
            fauth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_out,R.anim.left_in);
            finish();
        }
        if (id == R.id.action_changePassword) {
            Thread thread=new Thread(){
                public void run()
                {
                    Intent intent=new Intent(MainActivity.this,ResetPassword.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    finish();
                }
            };
            thread.start();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.nav_getStarted) {

            Thread thread=new Thread(){
                public void run()
                {
                    Intent intent=new Intent(MainActivity.this,GetStarted.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    finish();
                }
            };
            thread.start();

        } else if(id==R.id.nav_sparrowReport) {
            Thread thread=new Thread(){
                public void run()
                {
                    Intent intent=new Intent(MainActivity.this,SparrowReports.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    finish();
                }
            };
            thread.start();
        } else if (id == R.id.nav_sparrowBox) {
            // Handle the camera action
            Thread thread=new Thread(){
                public void run()
                {
                    Intent intent=new Intent(MainActivity.this,SparrowBoxMapsActivity.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    finish();
                }
            };
            thread.start();
        } else if (id == R.id.nav_sparrows) {
            Thread thread=new Thread(){
                public void run()
                {
                    Intent intent=new Intent(MainActivity.this,SparrowAnalyseMapsActivity.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    finish();
                }
            };
            thread.start();
        } else if (id == R.id.nav_buyBox) {
            if(userDetails.tnc.equals("NotAgreed"))
            {
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(MainActivity.this, TermsConditions.class);
                        intent.putExtras(bundle1);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        finish();
                    }
                };
                thread.start();
            }
            else {
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(MainActivity.this, AdoptSparrow.class);
                        intent.putExtras(bundle1);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        finish();
                    }
                };
                thread.start();
            }
        }
        else if (id == R.id.nav_ourTeam) {
            Thread thread=new Thread(){
                public void run()
                {
                    Intent intent=new Intent(MainActivity.this,TeamActivity.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    finish();
                }
            };
            thread.start();
        }
//        else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager)
    {

        Main_Tab2 main_tab2=new Main_Tab2();
        main_tab2.setArguments(bundle1);
        Main_Tab1 main_tab1=new Main_Tab1();
        main_tab1.setArguments(bundle1);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        if(hierarchy.equals("admins"))
        {
            mSectionsPagerAdapter.add(main_tab2,"Report");
            mSectionsPagerAdapter.add(new Admin_Tab4(),"Box Report");
            mSectionsPagerAdapter.add(main_tab1,"Adopt Process");
//            mSectionsPagerAdapter.add(new Admin_Tab4(),"Users");
        }
        else
        {
            mSectionsPagerAdapter.add(main_tab2,"Report");
            mSectionsPagerAdapter.add(main_tab1,"Sparrow Adopted");
        }
        viewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentlist= new ArrayList<>();
        List<String> fragmenttitle=new ArrayList<>();
        public SectionsPagerAdapter(FragmentManager supportFragmentManager)
        {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentlist.get(position);
        }

        @Override
        public int getCount() {
            return fragmentlist.size();
        }

        public void add(Fragment fragment, String title)
        {
            fragmentlist.add(fragment);
            fragmenttitle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmenttitle.get(position);
        }
    }
}
