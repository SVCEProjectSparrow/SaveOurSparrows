package com.svce.sparrowpro;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    CardView cardView;
    EditText user,pass;
    Button logbtn,regbtn,logphn;
    FirebaseDatabase fdatabase;
    FirebaseAuth fauth;
    FirebaseUser fuser;
    TextView forgotpass;
    DatabaseReference dreference,users;
    HashMap<String,String> logins=new HashMap<>();
    String substr1,substr2,username,password,userkey;
    String key,value,status;
    Intent intent;
    String actcreated,city,district,dob,gender,hierarchy,mobile,name,profileurl,state,tnc;
    StringBuilder stringBuilder;
    UserDetails userDetails;
    SharedPreferences sharedpreferences;
    private volatile boolean entry = false;
    EncryptionDecryption encryptionDecryption;
    ImageView showpass,userImageProfile;
    RelativeLayout cardLayout;
    int vis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        permissionCheck();

//        getSupportActionBar().setSubtitle("Login");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardLayout=findViewById(R.id.cardlayout);
        cardView=findViewById(R.id.card);
        userImageProfile=findViewById(R.id.userImageProfile);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Slide slide=new Slide();
                slide.setSlideEdge(Gravity.TOP);
                slide.setDuration(2000);
                TransitionManager.beginDelayedTransition(cardLayout,slide);
                cardView.setVisibility(View.VISIBLE);
            }
        },100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                userImageProfile.animate().alpha(1f).setDuration(1000);
            }
        },2000);

        showpass=(ImageView)findViewById(R.id.showpass);
        user=(EditText)findViewById(R.id.username);
        pass=(EditText)findViewById(R.id.password);
        logbtn=(Button)findViewById(R.id.logbtn);
        regbtn=(Button)findViewById(R.id.regbtn);
        logphn=(Button)findViewById(R.id.phoneno);
        forgotpass=findViewById(R.id.forgotpass);
        stringBuilder=new StringBuilder();
        vis=0;
        pass.setTransformationMethod(new PasswordTransformationMethod());
        encryptionDecryption=new EncryptionDecryption();
        fauth=FirebaseAuth.getInstance();
        fdatabase=FirebaseDatabase.getInstance();
        dreference=fdatabase.getReference("sparrow_users");
        if(fauth.getCurrentUser()!=null)
        {
            fuser=fauth.getCurrentUser();
        }
        dreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchdb(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        showpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vis==0) {
                    pass.setTransformationMethod(null);
                    showpass.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    pass.setSelection(pass.getText().length());
                    vis=1;
                }else {
                    vis=0;
                    pass.setTransformationMethod(new PasswordTransformationMethod());
                    showpass.setImageResource(R.drawable.ic_visibility_black_24dp);
                    pass.setSelection(pass.getText().length());
                }
            }
        });
        logphn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,LoginPhone.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                finish();
            }
        });

        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                username=user.getText().toString().toLowerCase();
                password=pass.getText().toString();
                if((!username.matches("^(\\.?[A-Za-z0-9_'-])+@[A-Za-z0-9_.-]+\\.[A-Za-z]{2,6}$")) || (username.trim().length()==0))
                {
                    user.setError("Enter Proper UserName!");
                    user.requestFocus();
                }
                else if ((!password.matches("[a-zA-Z0-9\\W]{8,25}$")) || (password.trim().length() == 0)) {
                    pass.setError("Enter Proper Password!");
                    Snackbar.make(view, "Password Length should be 8 to 25.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    pass.requestFocus();
                }
                else if(!getMobileDataState())
                {
                    Snackbar.make(view,"Turn On Mobile Data!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
                else {
                    if (logins.containsKey(username) || logins.isEmpty()) {         //logins.size()==0 or logins.isEmpty()
                        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "Validating",
                                "Please Wait!", true);
//                        dialog.setCancelable(false);
//                        dialog.setCanceledOnTouchOutside(false);
                        fauth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final Handler handler=new Handler()
                                    {
                                        public void handleMessage(Message msg)
                                        {
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.right_in,R.anim.left_out);
                                            LoginActivity.this.finish();
                                        }
                                    };
                                    final Thread thread2=new Thread()
                                    {
                                        public void run()
                                        {
                                            try
                                            {
                                                dialog.dismiss();
                                                Thread.sleep(2000);
                                                handler.sendEmptyMessage(0);
                                            }catch (Exception e){}
                                        }
                                    };
                                    final Thread thread1=new Thread()
                                    {
                                        public void run()
                                        {
                                            fuser=fauth.getCurrentUser();
                                            int j = username.indexOf('@');
                                            substr1 = username.substring(0, j);
                                            substr1 = substr1.replace(".", "&");
                                            substr2 = username.substring(j + 1, username.length());
                                            String[] substr2arr = substr2.split("\\.");
                                            substr2 = "";
                                            stringBuilder.append("user_"+substr1);
                                            for (j = 0; j < substr2arr.length; j++) {
                                                stringBuilder.append(substr2arr[j]);
                                            }

                                            users=dreference.child(stringBuilder.toString().trim());
                                            users.child("password").setValue(encryptionDecryption.encrypt(password));
//                                            users.child("userkey").setValue(encryptionDecryption.encrypt(stringBuilder.toString().trim()));

                                            users.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(username!=null && username.equals(encryptionDecryption.decrypt(dataSnapshot.child("userid").getValue().toString()))) {
                                                        profileurl = encryptionDecryption.decrypt(dataSnapshot.child("profilepicurl").getValue().toString());
                                                        actcreated = encryptionDecryption.decrypt(dataSnapshot.child("accountcreated").getValue().toString());
                                                        city = encryptionDecryption.decrypt(dataSnapshot.child("city").getValue().toString());
                                                        district =  encryptionDecryption.decrypt(dataSnapshot.child("district").getValue().toString());
                                                        dob =  encryptionDecryption.decrypt(dataSnapshot.child("dob").getValue().toString());
                                                        gender = encryptionDecryption.decrypt( dataSnapshot.child("gender").getValue().toString());
                                                        hierarchy =  encryptionDecryption.decrypt(dataSnapshot.child("hierarchy").getValue().toString());
                                                        mobile =  encryptionDecryption.decrypt(dataSnapshot.child("mobile").getValue().toString());
                                                        name =  encryptionDecryption.decrypt(dataSnapshot.child("name").getValue().toString());
                                                        state = encryptionDecryption.decrypt( dataSnapshot.child("state").getValue().toString());
                                                        tnc = encryptionDecryption.decrypt( dataSnapshot.child("tnc").getValue().toString());
                                                        userkey=encryptionDecryption.decrypt( dataSnapshot.child("userkey").getValue().toString());
                                                        userDetails = new UserDetails(username, password, actcreated,city, district, dob, gender, hierarchy, mobile, name, profileurl, state, tnc,userkey);
                                                        sharedpreferences = getSharedPreferences("currentUser", 0);
                                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                                        Gson gson = new Gson();
                                                        String json = gson.toJson(userDetails);
                                                        editor.putString("currentUsers", "1");
                                                        editor.putString("userjson", json);
                                                        editor.commit();
                                                        Bundle bundle = new Bundle();
                                                        bundle.putSerializable("userdetails", userDetails);
                                                        logbtn.setEnabled(false);
                                                        regbtn.setEnabled(false);
                                                        forgotpass.setEnabled(false);
                                                        user.setEnabled(false);
                                                        pass.setEnabled(false);
                                                        showpass.setEnabled(false);
                                                        Snackbar.make(view, "Login Successful!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                                        if (fuser.isEmailVerified()) {
                                                            intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        } else {
                                                            intent = new Intent(LoginActivity.this, ActivationAccount.class);
                                                        }
                                                        intent.putExtras(bundle);
                                                        if (entry) {
                                                            thread2.start();
                                                            entry = false;
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    };
                                    entry=true;
                                    thread1.start();
                                } else {
                                    dialog.dismiss();
                                    if(task.getException().getMessage().toString().equals("The password is invalid or the user does not have a password."))
                                    {
                                        Snackbar.make(view,"Password is Wrong!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                        pass.setError("Password is Wrong!");
                                        pass.requestFocus();
                                        pass.setSelection(password.length());
                                    }
                                    else if(task.getException().getMessage().toString().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred."))
                                    {
                                        if(!getMobileDataState())
                                        {
                                            Snackbar.make(view,"Turn On Mobile Data!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                        }
                                        else {
                                            Snackbar.make(view, "Poor Network Area. Please Retry!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                        }
                                    }
                                    else if(task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted."))
                                    {
                                        Snackbar.make(view,"User does not Exit! Invalid User!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                        user.setError("Invalid User!");
                                        user.requestFocus();
                                        user.setSelection(username.length());
                                    }
                                    else if(task.getException().getMessage().equals("We have blocked all requests from this device due to unusual activity. Try again later. [ Too many unsuccessful login attempts.  Please include reCaptcha verification or try again later ]"))
                                    {
                                        Snackbar.make(view,"Too Many Unsuccessful Login Attempts!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                    }
                                    else
                                    {
                                        Snackbar.make(view,task.getException().getMessage().toString(),Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                    }
                                }
                            }
                        });

//                        if (logins.get(username).toString().equals(password)) {} else {
//                            userstatus.setText("Password is wrong");
//                            pass.requestFocus();
//                        }
                    } else {
                        Snackbar.make(view,"User does not Exit! Invalid User!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                        user.setError("Invalid User!");
                        user.requestFocus();
                        user.setSelection(username.length());
                    }
                }
            }
        });
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                finish();
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                finish();
            }
        });

    }

    private void permissionCheck() {
        String s[]={ android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,s,1);
        }
    }

//    public void setMobileDataState(boolean mobileDataEnabled)
//    {
//        try
//        {
//            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
//            if (null != setMobileDataEnabledMethod)
//            {
//                setMobileDataEnabledMethod.invoke(telephonyService, mobileDataEnabled);
//            }
//        }
//        catch (Exception ex)
//        {
//            Toast.makeText(this, "Error setting mobile data state", Toast.LENGTH_SHORT).show();
//        }
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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


    private void searchdb(DataSnapshot dataSnapshot) {
        for(DataSnapshot s:dataSnapshot.getChildren())
        {
            key=encryptionDecryption.decrypt((String)s.child("userid").getValue());
            value=encryptionDecryption.decrypt((String)s.child("password").getValue());
            status=encryptionDecryption.decrypt((String)s.child("activestatus").getValue());
            if(status!=null && status.equals("1")) {
                logins.put(key, value);
            }
//            Toast.makeText(this, ""+logins.get(value), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {
        finish();
    }

//    public static void hideKeyboardFrom(Context context, View view) {
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

}
