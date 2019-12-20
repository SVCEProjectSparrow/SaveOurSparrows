package com.svce.sparrowpro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.HashMap;


public class ForgotPasswordActivity extends AppCompatActivity {
    CoordinatorLayout cardLayout;
    CardView cardView;
    Button sendverify;
    EditText username;
    FirebaseAuth fauth;
    FirebaseDatabase fdatabase;
    DatabaseReference dreference;
    EncryptionDecryption encryptionDecryption;
    HashMap<String, String> logins;
    String user, key, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setSubtitle("Forgot Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        logins = new HashMap<>();
        fauth = FirebaseAuth.getInstance();
        fdatabase = FirebaseDatabase.getInstance();
        dreference = fdatabase.getReference("sparrow_users");
        encryptionDecryption = new EncryptionDecryption();

        dreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchdb(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        cardLayout = findViewById(R.id.cardlayout);
        cardView = findViewById(R.id.card);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.TOP);
                slide.setDuration(2000);
                TransitionManager.beginDelayedTransition(cardLayout, slide);
                cardView.setVisibility(View.VISIBLE);
            }
        }, 10);
        username = (EditText) findViewById(R.id.username);
        sendverify = (Button) this.findViewById(R.id.sendverifycode);
        sendverify.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View view) {
                user = username.getText().toString().trim();


                if ((!user.matches("^(\\.?[A-Za-z0-9_'-])+@[A-Za-z0-9_.-]+\\.[A-Za-z]{2,6}$")) || (user.trim().length() == 0)) {
                    username.setError("Enter Proper Mail ID");
                    username.requestFocus();
                } else if (!isOnline()) {
                    Snackbar.make(view, "Turn On Mobile Data!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    if (logins.isEmpty() || logins.containsKey(user)) {
                        final ProgressDialog dialog = ProgressDialog.show(ForgotPasswordActivity.this, "Password Reset Link", "Sending Link", true);
                        final Handler handler = new Handler() {
                            public void handleMessage(Message msg) {
                                dialog.dismiss();
                            }
                        };
                        fauth.sendPasswordResetEmail(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar snackbar = Snackbar.make(cardLayout, "Password Reset Link is sent to " + user + "!", Snackbar.LENGTH_LONG);
                                    TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                                    Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/helvetica.ttf");
                                    tv.setTypeface(typeface);
                                    snackbar.show();
                                } else if (task.getException().getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                    if (!isOnline()) {
                                        Snackbar.make(view, "Turn On Mobile Data!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    } else {
                                        Snackbar.make(view, "Poor Network Area. Please Retry!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    }
                                } else if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                    Snackbar.make(view, "Invalid User. User does not Exist!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    username.requestFocus();
                                } else if (task.getException().getMessage().equals("We have blocked all requests from this device due to unusual activity. Try again later.")) {
                                    Snackbar.make(view, "Too Many Request Attempts!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
//                                else {
//                                    Log.d("Debug", task.getException().toString());
//                                    Snackbar.make(view, task.getException().toString(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                                }
                                handler.sendEmptyMessage(0);
                            }
                        });

                    } else {
                        Snackbar.make(view, "Invalid User. User does not Exist!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        username.requestFocus();
                    }

                }

            }
        });


    }

    private void searchdb(DataSnapshot dataSnapshot) {
        for (DataSnapshot s : dataSnapshot.getChildren()) {
            key = encryptionDecryption.decrypt((String) s.child("userid").getValue());
            status = encryptionDecryption.decrypt((String) s.child("activestatus").getValue());
            if (status != null && status.equals("1")) {
                logins.put(key, key);
            }
        }
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public boolean getMobileDataState() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

            if (null != getMobileDataEnabledMethod) {
                boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
                return mobileDataEnabled;
            }
        } catch (Exception ex) {
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
        finish();
    }
}


////                    try {
//                        GMailSender sender = new GMailSender("sparrowapppro@gmail.com", "Sparrowsvce1379@hod", ForgotPasswordActivity.this,view);
//                        sender.sendMail("This is Subject",
//                                "This is Body",
//                                "sparrowapppro@gmail.com",
//                                user);
////                        Toast.makeText(ForgotPasswordActivity.this, "Sent Mail", Toast.LENGTH_SHORT).show();
////                    } catch (Exception e) {
////                        Toast.makeText(ForgotPasswordActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
////                    }