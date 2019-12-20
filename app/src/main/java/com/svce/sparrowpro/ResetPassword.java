package com.svce.sparrowpro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
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
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;

public class ResetPassword extends AppCompatActivity {

    CardView cardView;
    CoordinatorLayout cardLayout;
    Bundle bundle, bundle1;
    UserDetails userDetails;
    EditText currentpass, newpass, confirmpass;
    Button resetpass;
    String currentp, newp, confirmp;
    SharedPreferences sharedpreferences;
    String userkey, userid;
    EncryptionDecryption encryptionDecryption;
    FirebaseDatabase fDatabase;
    DatabaseReference users;
    FirebaseAuth fauth;
    FirebaseUser fuser;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        cardLayout = findViewById(R.id.cardlayout);
        cardView = findViewById(R.id.card);
        currentpass = findViewById(R.id.currentpass);
        newpass = findViewById(R.id.newpass);
        confirmpass = findViewById(R.id.confirmpass);
        resetpass = findViewById(R.id.resetpass);
        bundle = getIntent().getExtras();
        userDetails = (UserDetails) bundle.getSerializable("userdetails");
        encryptionDecryption = new EncryptionDecryption();
        userkey = userDetails.userkey;
        userid = userDetails.username;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.TOP);
                slide.setDuration(2000);
                TransitionManager.beginDelayedTransition(cardLayout, slide);
                cardView.setVisibility(View.VISIBLE);
            }
        }, 100);
        bundle1 = new Bundle();
        bundle1.putSerializable("userdetails", userDetails);

        Thread t = new Thread() {
            public void run() {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Change Password");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        };
        t.start();

        fDatabase = FirebaseDatabase.getInstance();
        users = fDatabase.getReference("sparrow_users").child(userkey);
        fauth = FirebaseAuth.getInstance();
        fuser = fauth.getCurrentUser();
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                currentp = currentpass.getText().toString();
                newp = newpass.getText().toString();
                confirmp = confirmpass.getText().toString();

                if ((!currentp.matches("[a-zA-Z0-9\\W]{8,25}$")) || (currentp.trim().length() == 0)) {
                    currentpass.setError("Enter Proper Password!");
                    Snackbar.make(view, "Password Length should be 8 to 25.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    currentpass.requestFocus();
                } else if ((!newp.matches("[a-zA-Z0-9\\W]{8,25}$")) || (newp.trim().length() == 0)) {
                    newpass.setError("Enter Proper Password!");
                    Snackbar.make(view, "Password Length should be 8 to 25.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    newpass.requestFocus();
                } else if (!confirmp.equals(newp)) {
                    confirmpass.setError("Password and Confirm Password should be same!");
                    confirmpass.requestFocus();
                } else if (!getMobileDataState()) {
                    Snackbar.make(view, "Turn On Mobile Data!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {

                    final ProgressDialog dialog = ProgressDialog.show(ResetPassword.this, "Reset Password",
                            "Changing Password!", true);
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(userid, currentp);
                    fuser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        fuser.updatePassword(newp)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            users.child("password").setValue(encryptionDecryption.encrypt(currentp));
                                                            userDetails.setPassword(newp);
                                                            sharedpreferences = getSharedPreferences("currentUser", 0);
                                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                                            Gson gson = new Gson();
                                                            String json = gson.toJson(userDetails);
                                                            editor.putString("currentUsers", "1");
                                                            editor.putString("userjson", json);
                                                            editor.commit();
                                                            bundle1.putSerializable("userdetails", userDetails);
                                                            currentpass.setText("");
                                                            newpass.setText("");
                                                            confirmpass.setText("");
                                                            currentpass.requestFocus();
                                                            dialog.dismiss();
                                                            Snackbar.make(view, "Password Updated Successfully!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                                        } else {
                                                            dialog.dismiss();
                                                            Snackbar snackbar = Snackbar.make(cardLayout, "Something went wrong! Try again Later!", Snackbar.LENGTH_LONG).setAction("Action", null);
                                                            TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                                                            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/helvetica.ttf");
                                                            tv.setTypeface(typeface);
                                                            snackbar.show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        dialog.dismiss();
                                        Snackbar.make(view, "Current Password is Wrong!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    }
                                }
                            });


                }
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

    public boolean getMobileDataState() {
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
        Intent intent = new Intent(ResetPassword.this, MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
        ResetPassword.this.finish();
    }

}
