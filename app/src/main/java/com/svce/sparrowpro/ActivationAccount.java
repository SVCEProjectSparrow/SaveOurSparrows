package com.svce.sparrowpro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Method;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ActivationAccount extends AppCompatActivity {
    Bundle bundle,bundle1;
    UserDetails userDetails;
    Button sendverify,logout;
    FirebaseAuth fauth=FirebaseAuth.getInstance();
    FirebaseUser fuser=fauth.getCurrentUser();
    View btnview;
    String userid,profileurl;
    TextView usertxt;
    CoordinatorLayout cardLayout;
    CardView cardView;
    ImageView nav_profileimage;

    @Override
    protected void onStart() {
        super.onStart();

        if(fuser!=null && fuser.isEmailVerified())
        {
            getBundles();
            if(userDetails.userkey!=null)
                gotoMainActivity();
        }
    }

    public void getBundles()
    {
        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation_account);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Activate Account");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        };
        t.start();
        getBundles();

        usertxt=(TextView)findViewById(R.id.usertxt);
        userid=userDetails.username;
        profileurl=userDetails.profileurl;
        usertxt.setText(userid+"?");
        final Thread thread=new Thread()
        {
            public void run()
            {
                try {
                    Thread.sleep(2000);
                    setLogout();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        nav_profileimage=findViewById(R.id.nav_profileimage);
        Picasso.get().load(profileurl)
                .placeholder(R.drawable.ic_account_circle_black_24dp)  //R.drawable.logo || R.mipmap.ic_launcher_round
                .resize(100, 100)
                .transform(new CropCircleTransformation())
                .centerCrop().into(nav_profileimage);
        sendverify=(Button)findViewById(R.id.sendverify);
        logout = (Button) findViewById(R.id.btn_logout);
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

        sendverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                btnview=view;
                if(!isOnline())
                {
                    Snackbar.make(cardLayout,"Turn On Mobile Data!!!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
                else
                {
                    if(fuser.isEmailVerified())
                    {
                        gotoMainActivity();
                    }
                    else
                    {
                        fuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar snackbar=Snackbar.make(cardLayout,"Verification Link is Sent to "+userid+"! Verify and SignIn Again!",Snackbar.LENGTH_LONG).setAction("Action",null);
                                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                                Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/helvetica.ttf");
                                tv.setTypeface(typeface);
                                snackbar.show();
                                thread.start();
//                                Intent i = getBaseContext().getPackageManager()
//                                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
//                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(i);
                            }
                        });
                    }
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogout();
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

    public void setLogout()
    {
        SharedPreferences.Editor editor = getSharedPreferences("currentUser", 0).edit();
        editor.putString("currentUsers","0");
        editor.putString("userjson","");
        editor.apply();
        fauth.signOut();
        Intent intent = new Intent(ActivationAccount.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        finish();
    }

    public void gotoMainActivity()
    {
        bundle1 = new Bundle();
        bundle1.putSerializable("userdetails", userDetails);
        Intent intent=new Intent(ActivationAccount.this,MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
        finish();
    }

//    public boolean getMobileDataState()
//    {
//        try
//        {
//            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");
//
//            if (null != getMobileDataEnabledMethod)
//            {
//                boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
//                return mobileDataEnabled;
//            }
//        }
//        catch (Exception ex)
//        {
//        }
//        return false;
//    }

    public boolean isOnline() {
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
        finish();
    }
}
