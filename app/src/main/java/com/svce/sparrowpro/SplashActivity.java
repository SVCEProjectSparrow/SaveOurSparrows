package com.svce.sparrowpro;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class SplashActivity extends AppCompatActivity {

    private RelativeLayout splashlayout;
    private ImageView splashimage;
    SplashLauncher splachlauncher;
    SharedPreferences prefs;
    String result;
    UserDetails userDetails;
    FirebaseAuth fauth;
    FirebaseUser fuser;
    private volatile boolean exit = false;
    ShimmerFrameLayout container;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        container = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
//        container.startShimmer();


//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();


        prefs = getSharedPreferences("currentUser",0);
        result = prefs.getString("currentUsers", "0");
        String json = prefs.getString("userjson", "");
        if (result.equals("1")) {
            fauth=FirebaseAuth.getInstance();
            fuser=fauth.getCurrentUser();
            Gson gson = new Gson();
            userDetails=gson.fromJson(json, UserDetails.class);
            final Bundle bundle=new Bundle();
            bundle.putSerializable("userdetails",userDetails);
            if(fuser.isEmailVerified())
            {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            else {
                intent = new Intent(SplashActivity.this, ActivationAccount.class);
            }
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        else {
            splashlayout = (RelativeLayout) findViewById(R.id.splashlayout);
            splashimage = (ImageView) findViewById(R.id.splashimage);
            splashlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    exit=true;
                    finish();
                }
            });
            splashimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    exit=true;
                    finish();
                }
            });
            splachlauncher = new SplashLauncher();
            splachlauncher.start();
        }

    }
    final Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(!exit) {
                intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
            finish();
        }
    };
    private class SplashLauncher extends Thread{
        public void run(){
            try{
                sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onBackPressed() {
        exit=true;
        finish();
    }
}
