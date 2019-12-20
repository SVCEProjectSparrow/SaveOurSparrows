package com.svce.sparrowpro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;

import instamojo.library.InstapayListener;
import instamojo.library.InstamojoPay;
import instamojo.library.Config;

import org.json.JSONObject;
import org.json.JSONException;

import android.content.IntentFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity {

    Bundle bundle, bundle1;
    UserDetails userDetails;
    BoxDetails boxDetails;
    String userkey,userid, mobile, name,boxid;
    Intent intent;
    volatile boolean running = false;
    InstamojoPay instamojoPay;

    TextView transid,boxidtxt,useridtxt,transdate,transstatus;
    Button adoptdetails;
    FirebaseDatabase fdatabase;
    DatabaseReference boxes,transactions,transchild;
    EncryptionDecryption encryptionDecryption;
    Calendar cal;
    SimpleDateFormat df;
    Date currentdate;
    String datetransaction,transidst;
    PaymentDetails paymentDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Thread t=new Thread()
        {
            public void run()
            {
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                getSupportActionBar().setSubtitle("Transaction Details");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        };
        t.start();
        bundle = getIntent().getExtras();
        userDetails = (UserDetails) bundle.getSerializable("userdetails");
        boxDetails = (BoxDetails) bundle.getSerializable("boxdetails");
        // Call the function callInstamojo to start payment here

        bundle1 = new Bundle();
        bundle1.putSerializable("userdetails", userDetails);
        userkey=userDetails.userkey;
        userid = userDetails.username;
        name = userDetails.name;
        mobile = userDetails.mobile;
        boxid=boxDetails.boxkey;
        running = true;
        encryptionDecryption=new EncryptionDecryption();
        fdatabase=FirebaseDatabase.getInstance();
        boxes=fdatabase.getReference("sparrow_boxes").child(boxid);
        transactions=fdatabase.getReference("boxes_transactions");
        cal = Calendar.getInstance();
        currentdate = cal.getTime();
        df = new SimpleDateFormat("dd MMMM yyyy");
        datetransaction = df.format(currentdate);
        transid=findViewById(R.id.transid);
        boxidtxt=findViewById(R.id.boxid);
        useridtxt=findViewById(R.id.userid);
        transdate=findViewById(R.id.transdate);
        transstatus=findViewById(R.id.transstatus);
        adoptdetails=findViewById(R.id.adoptdetails);
        boxidtxt.setText(boxid);
        useridtxt.setText(userid);
        transstatus.setText("Processing...");

        InstamojoPayment payment = new InstamojoPayment(PaymentActivity.this,"Adopt Sparrow","350");
        payment.execute("");

    }


    public class InstamojoPayment extends AsyncTask<String, String, String> {
        Context context;
        String purpose,amount;
        InstamojoPayment(Context context,String purpose,String amount) {
            this.context = context;
            this.purpose=purpose;
            this.amount=amount;
        }
//        private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername, Context context) {
//
//
//        }



        @Override
        protected String doInBackground(String... strings) {
            instamojoPay = new InstamojoPay();
            IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
            registerReceiver(instamojoPay, filter);
            JSONObject pay = new JSONObject();
            try {
                pay.put("email", userid);
                pay.put("phone", mobile);
                pay.put("purpose", purpose);
                pay.put("amount", amount);
                pay.put("name", name);
                pay.put("send_sms", false);
                pay.put("send_email", false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            initListener();
            instamojoPay.start((Activity) context, pay, listener);


            return null;
        }

        InstapayListener listener;

        private void initListener() {
            listener = new InstapayListener() {
                @Override
                public void onSuccess(final String response) {
                    Thread thread=new Thread(){
                        public void run()
                        {
                            final HashMap<String,Object> trans=new HashMap<>();
                            String arr[]=response.split(":");
                            for (int i=0;i<arr.length;i++)
                            {
                                String arr2[]=arr[i].split("=");
                                trans.put(arr2[0],arr2[1]);
                                if(arr2[0].equals("paymentId"))
                                {
                                    transchild=transactions.child(arr2[1]);
                                    transidst=arr2[1];
                                }
                                trans.put("boxid",boxid);
                                trans.put("adoptby",userid);
                                trans.put("userkey",userkey);
                                trans.put("processstatus","Started");
                                trans.put("appointmentdate","Not Fixed");
                                trans.put("adoptboxfixdate","Not Fixed");
                                trans.put("transactiondate",datetransaction);
                                trans.put("amount",amount);

                            }
                            transchild.updateChildren(trans).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    transid.setText(transidst);
                                    transdate.setText(datetransaction);
                                    transstatus.setText("Transaction Success!");
                                    transstatus.setBackgroundColor(getResources().getColor(R.color.accepted));
                                    transstatus.setTextColor(getResources().getColor(R.color.white));
                                    paymentDetails=new PaymentDetails(transidst,boxid,userkey,userid,datetransaction,"Started");
                                    adoptdetails.setVisibility(View.VISIBLE);
                                }
                            });
                            adoptdetails.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    intent = new Intent(context, PaymentActivityRespective.class);
                                    bundle1.putSerializable("paydetails", paymentDetails);
                                    intent.putExtras(bundle1);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                                    PaymentActivity.this.finish();
                                }
                            });
                        }
                    };
                    Thread thread2=new Thread(){
                        public void run()
                        {
                            HashMap<String,Object> box=new HashMap<>();
                            box.put("adopt",encryptionDecryption.encrypt("adopted"));
                            box.put("adoptby",encryptionDecryption.encrypt(userid));
                            boxes.updateChildren(box);
                        }
                    };
                    thread.start();
                    thread2.start();
                    running = false;
                }

                @Override
                public void onFailure(int code, String reason) {
                    transstatus.setText("Transaction Failed!");
                    transstatus.setBackgroundColor(getResources().getColor(R.color.denied));
                    transstatus.setTextColor(getResources().getColor(R.color.white));
                    transid.setVisibility(View.GONE);
                    transdate.setVisibility(View.GONE);
                    running = false;
                }
            };
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
    @Override
    public void onBackPressed() {
        if (!running) {
            intent = new Intent(PaymentActivity.this, AdoptSparrow.class);
            intent.putExtras(bundle1);
            startActivity(intent);
            overridePendingTransition(R.anim.right_out, R.anim.left_in);
            finish();
        }
    }
}
