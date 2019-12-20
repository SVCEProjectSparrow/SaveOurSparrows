package com.svce.sparrowpro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class TermsConditions extends AppCompatActivity {

    View parentView;
    Bundle bundle,bundle1;
    UserDetails userDetails;
    android.widget.ProgressBar progressBar;
    TextView termstext;
    CheckBox acceptbox;
    Button acceptbutton;
    DatabaseReference terms,users;
    String userkey;
    EncryptionDecryption encryptionDecryption;
    SharedPreferences sharedpreferences;
    Intent intent;
    CardView cardView;
    CoordinatorLayout cardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        parentView = findViewById(android.R.id.content);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Terms and Conditions");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        };
        t.start();


        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");

        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);
        encryptionDecryption=new EncryptionDecryption();
        userkey=userDetails.userkey;
        cardLayout=findViewById(R.id.cardlayout);
        cardView=findViewById(R.id.card);
        termstext=findViewById(R.id.termstext);
        progressBar=findViewById(R.id.progress_image);
        acceptbox=findViewById(R.id.termscheck);
        acceptbutton=findViewById(R.id.acceptbtn);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Slide slide=new Slide();
                slide.setSlideEdge(Gravity.TOP);
                slide.setDuration(1000);
                TransitionManager.beginDelayedTransition(cardLayout,slide);
                cardView.setVisibility(View.VISIBLE);
            }
        },100);
        terms= FirebaseDatabase.getInstance().getReference("terms_conditions");
        users= FirebaseDatabase.getInstance().getReference("sparrow_users").child(userkey);
        progressBar.setVisibility(View.VISIBLE);
        if(!getMobileDataState())
        {
            Snackbar.make(parentView,"Turn On Mobile Data!!!",Snackbar.LENGTH_LONG).show();
            progressBar.setVisibility(View.VISIBLE);
            acceptbox.setVisibility(View.GONE);
            acceptbutton.setVisibility(View.GONE);
        }
        terms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tncs=(String)dataSnapshot.child("terms").getValue();
                if(tncs!=null)
                {
                    termstext.setText(tncs);
                    acceptbox.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        acceptbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ProgressDialog dialog=ProgressDialog.show(TermsConditions.this,"Terms and Conditions","Accepting",true);
                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);
                        TermsConditions.this.finish();
                    }
                };
                final Thread thread=new Thread()
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
                users.child("tnc").setValue(encryptionDecryption.encrypt("Agreed")).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userDetails.setTnc("Agreed");
                        sharedpreferences = getSharedPreferences("currentUser", 0);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(userDetails);
                        editor.putString("currentUsers", "1");
                        editor.putString("userjson", json);
                        editor.commit();
                        bundle1.putSerializable("userdetails",userDetails);
                        acceptbutton.setEnabled(false);
                        intent=new Intent(TermsConditions.this,AdoptSparrow.class);
                        intent.putExtras(bundle1);
                        Snackbar snackbar=Snackbar.make(view,"Terms and Conditions Accepted!",Snackbar.LENGTH_LONG).setAction("Action",null);
                        TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/helvetica.ttf");
                        tv.setTypeface(typeface);
                        snackbar.show();
                        thread.start();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed(){
        intent=new Intent(TermsConditions.this,MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        finish();
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

    public void acceptReject(View view)
    {
        acceptbox=(CheckBox)view;
        if(acceptbox.isChecked())
        {
            acceptbutton.setVisibility(View.VISIBLE);
        }
        else
        {
            acceptbutton.setVisibility(View.GONE);
        }
    }
}
