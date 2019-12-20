package com.svce.sparrowpro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class RegistrationActivity extends AppCompatActivity {
    CardView cardView;
    EditText name,mobno,user,pass,cpass,city,district,state;
    RadioGroup gendergroup;
    RadioButton male,female,transgender,genderbtn;
    ImageView profimage;
    Button regbtn;
    TextView dobtxt;
    DatePickerDialog.OnDateSetListener dateSetListener;
    String namest=null,mobnost=null,mailst=null,passst=null,cpassst=null,cityst=null,districtst=null,statest=null,dobst=null,genderst=null;
    String dateregistered,curdate;
    int selectedgender=0,profimagestatus=0;
    Uri profimageuri;
    String substr1,substr2,userkey;
    String key,value,status;

    static final int PICK_IMAGE_REQUEST = 1;
    ProgressDialog dialogcreate,dialog;



    StringBuilder stringBuilder;
    Calendar cal;
    DatePickerDialog datedialog;
    FirebaseAuth fauth;
    FirebaseUser fuser;
    FirebaseDatabase fdatabase;
    DatabaseReference dreference,users;
    FirebaseStorage fstorage;
    StorageReference sreference,userimages;
    StorageTask fuploadtask;
    HashMap<String,String> logins=new HashMap<>();
    EncryptionDecryption encryptionDecryption;
    Date currentdate,dobdate;
    SimpleDateFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        String s[]={ android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,s,1);
        }

//        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final View parentView=findViewById(android.R.id.content);
        cardView=findViewById(R.id.card);
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
        },100);
        int i;
        dobst="";
        name=(EditText)findViewById(R.id.name);
        mobno=(EditText)findViewById(R.id.mobno);
        user=(EditText)findViewById(R.id.user);
        pass=(EditText)findViewById(R.id.pass);
        cpass=(EditText)findViewById(R.id.cpass);
        city=(EditText)findViewById(R.id.city);
        district=(EditText)findViewById(R.id.district);
        state=(EditText)findViewById(R.id.state);
        gendergroup=(RadioGroup)findViewById(R.id.gendergroup);
        male=(RadioButton)findViewById(R.id.male);
        female=(RadioButton)findViewById(R.id.female);
        transgender=(RadioButton)findViewById(R.id.transgender);
        profimage=(ImageView)findViewById(R.id.profimage);
        regbtn=(Button)findViewById(R.id.regbtn);
        dobtxt=(TextView)findViewById(R.id.dobtxt);

        stringBuilder=new StringBuilder();


//        final String datearr[]=new String[31];
//        for(i=0;i<31;i++)
//        {
//            datearr[i]=String.valueOf(i+1);
//        }
//        ArrayAdapter<String> DateAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,datearr);
//        DateAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        date.setAdapter(DateAdapter);
//
//        final String montharr[]=new String[12];
//        montharr[0]="January";
//        montharr[1]="February";
//        montharr[2]="March";
//        montharr[3]="April";
//        montharr[4]="May";
//        montharr[5]="June";
//        montharr[6]="July";
//        montharr[7]="August";
//        montharr[8]="September";
//        montharr[9]="October";
//        montharr[10]="November";
//        montharr[11]="December";
//        ArrayAdapter<String> MonthAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,montharr);
//        MonthAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        month.setAdapter(MonthAdapter);
//
//        final Date dt=new Date();
//        int yeardt=dt.getYear();
//        yeardt=yeardt+1900;
//        final String yeararr[]=new String[100];
//        for(i=0;i<100;i++)
//        {
//            yeararr[i]=""+yeardt;
//            yeardt--;
//        }
//        ArrayAdapter<String> YearAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,yeararr);
//        YearAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        year.setAdapter(YearAdapter);

        cal = Calendar.getInstance();
        currentdate = cal.getTime();
        df = new SimpleDateFormat("dd MMMM yyyy");
        dateregistered = df.format(currentdate);

        dobtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                datedialog = new DatePickerDialog(
                        RegistrationActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                datedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datedialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                dobst=day+"/";
                if (String.valueOf(month).length() == 1)  //String.value(month).length(); (int)Math.log10(month)+1==1
                {
                    dobst = dobst + "0" + month + "/" + year;
                } else {
                    dobst = dobst + month + "/" + year;
                }

                try {
                    dobdate=new SimpleDateFormat("dd/MM/yyyy").parse(dobst);
                    if(dobdate.after(currentdate))
                    {
                        dobst="";
                        Snackbar.make(parentView,"Invalid Date of Birth !",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    dobtxt.setText(dobst);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        fauth=FirebaseAuth.getInstance();
        fdatabase= FirebaseDatabase.getInstance();
        dreference=fdatabase.getReference("sparrow_users");
        fstorage=FirebaseStorage.getInstance();
        sreference=fstorage.getReference("sparrow_users");
        encryptionDecryption=new EncryptionDecryption();




        dreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchdb(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                namest = name.getText().toString();
                mobnost = mobno.getText().toString();
                mailst = user.getText().toString().toLowerCase();
                cityst = city.getText().toString();
                districtst = district.getText().toString();
                statest = state.getText().toString();
                passst = pass.getText().toString();
                cpassst = cpass.getText().toString();
                selectedgender = gendergroup.getCheckedRadioButtonId();
                genderbtn = (RadioButton) findViewById(selectedgender);
                genderst = genderbtn.getText().toString();
//                dttemp = date.getSelectedItemPosition();
//                datest = datearr[dttemp];
//                dttemp = month.getSelectedItemPosition();
//                monthst = String.valueOf(dttemp + 1);
//                dttemp = year.getSelectedItemPosition();
//                yearst = yeararr[dttemp];
//                dobst = datest + "/" + monthst + "/" + yearst;

                if ((!namest.matches("[a-zA-Z0-9 ]+$")) || (namest.trim().length() == 0)) {
                    name.setError("Only Alphabets");
                    name.requestFocus();
                } else if ((!mailst.matches("^(\\.?[A-Za-z0-9_'-])+@[A-Za-z0-9_.-]+\\.[A-Za-z]{2,6}$")) || (mailst.trim().length() == 0)) {
                    user.setError("Enter Proper Mail ID");
                    user.requestFocus();
                }  else if ((!passst.matches("[a-zA-Z0-9\\W]{8,25}$")) || (passst.trim().length() == 0)) {
                    pass.setError("Enter Proper Password");
                    Snackbar.make(view,"Password Length should be 8 to 25!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    pass.requestFocus();
                } else if (!cpassst.equals(passst)) {
                    cpass.setError("Password and Confirm Password should be same!");
                    cpass.requestFocus();
                }else if ((!mobnost.matches("[0-9]{10}$")) || (mobnost.trim().length() == 0)) {
                    mobno.setError("Only Numbers with 10 digits");
                    mobno.requestFocus();
                } else if (dobst==null|| dobst.equals("")||dobst.trim().length()==0) {
                    Snackbar.make(view,"Date of Birth is Mandatory!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
                else if (!cityst.matches("[a-zA-Z ]+$")) {
                    city.setError("Enter Proper City");
                    city.requestFocus();
                } else if (!districtst.matches("[a-zA-Z ]+$")) {
                    district.setError("Enter Proper District");
                    district.requestFocus();
                } else if (!statest.matches("[a-zA-Z ]+$")) {
                    state.setError("Enter Proper State");
                    state.requestFocus();
                }
                else if(!getMobileDataState())
                {
                    Snackbar.make(view,"Turn On Mobile Data!!!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
                else
                {
                    if (logins.containsKey(mailst)) {
                        user.setError("Existing User!!!");
                        user.requestFocus();
                        Snackbar.make(view,"User Already Exists!!!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    } else {
                        UploadFirebase uploadFirebase=new UploadFirebase(RegistrationActivity.this,view);
                        uploadFirebase.execute("");
                    }
                }
            }
        });

        profimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
//                startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
                openFileChooser();
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
//        logins.put(null,null);
        for(DataSnapshot s:dataSnapshot.getChildren()) {
            key = encryptionDecryption.decrypt((String) s.child("userid").getValue());
            status=encryptionDecryption.decrypt((String)s.child("activestatus").getValue());
//            Toast.makeText(this, ""+key, Toast.LENGTH_SHORT).show();
            if(status!=null && status.equals("1")) {
                logins.put(key, key);
            }
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData() != null) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                profimageuri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCropImageActivity(imageUri);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profimageuri=result.getUri();
                Glide.with(RegistrationActivity.this).load(profimageuri).into(profimage);
                profimagestatus=1;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(RegistrationActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (profimageuri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(profimageuri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(2,2)
                .setMultiTouchEnabled(true)
                .start(this);
    }



    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }




    @Override
    public void onBackPressed() {
        Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        RegistrationActivity.this.finish();
    }





    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public class UploadFirebase extends AsyncTask<String,String,String>
    {
        Context context;
        View view;
        Thread thread1,thread2,thread3;
//        final Thread thread;
        UploadFirebase(Context context,View view)
        {
            this.view=view;
            this.context=context;
            dialogcreate=new ProgressDialog(RegistrationActivity.this);
            dialog=new ProgressDialog(RegistrationActivity.this);
//            thread=new Thread();
        }

        @Override
        protected String doInBackground(String... strings) {
            int j = mailst.indexOf('@');
            substr1 = mailst.substring(0, j);
            substr1 = substr1.replace(".", "&");
            substr2 = mailst.substring(j + 1, mailst.length());
            String[] substr2arr = substr2.split("\\.");
            substr2 = "";
            stringBuilder.append("user_"+substr1);
            for (j = 0; j < substr2arr.length; j++) {
                stringBuilder.append(substr2arr[j]);
            }
            userkey=stringBuilder.toString().trim();



//            userkey=encryptionDecryption.encrypt(stringBuilder.append("user_"+mailst).toString());
//            userkey=encryptionDecryption.encrypt(mailst);
            userimages=sreference.child(userkey);
            users=dreference.child(userkey);


//                    Toast.makeText(RegistrationActivity.this, ""+userkey, Toast.LENGTH_SHORT).show();
            fauth.createUserWithEmailAndPassword(mailst,passst).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        fuser = fauth.getCurrentUser();
                        thread3 = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(2500);
                                    if (fuser != null) {
                                        fauth.signOut();
                                    }
                                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.right_out, R.anim.left_in);
                                    RegistrationActivity.this.finish();

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread1 = new Thread() {
                            public void run() {
                                fuser.sendEmailVerification();
                            }
                        };
                        if (profimagestatus == 1) {
                            fuploadtask = userimages.putFile(profimageuri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                            thread2 = new Thread() {
                                                public void run() {
//                                            HashMap<String, Object> regmap = new HashMap<String, Object>();
//                                            regmap.put("profilepicurl",taskSnapshot.getDownloadUrl().toString());
                                                    users.child("profilepicurl").setValue(encryptionDecryption.encrypt(taskSnapshot.getDownloadUrl().toString()));
                                                    callusers();
                                                }
                                            };
                                            thread1.start();
                                            thread2.start();
                                            name.requestFocus();
                                            dialog.dismiss();
                                            regbtn.setEnabled(false);
                                            Snackbar.make(view, "Registration Successful. Verify Your Account!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                            thread3.start();
                                        }


                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            fuser.delete();
                                            dialog.dismiss();
                                            if (e.getMessage().toString().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                                Toast.makeText(RegistrationActivity.this, "Please Check Your Internet Connection!!!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                            dialogcreate.dismiss();
                                            dialog.setIndeterminate(true);
                                            dialog.setTitle("Registering Data....");
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.setCancelable(false);
                                            dialog.setMessage("Submitting..Please Wait....");
                                            dialog.show();
                                        }
                                    });

                        }
                        else
                        {
                            final ProgressDialog dialog = ProgressDialog.show(RegistrationActivity.this, "Registering Data....",
                                    "Submitting..Please Wait....", true);

                            thread2=new Thread()
                            {
                                public void run()
                                {
                                    dialogcreate.dismiss();
                                    users.child("profilepicurl").setValue(encryptionDecryption.encrypt("null"));
                                    dialog.dismiss();
                                }
                            };
                            thread1.start();
                            thread2.start();
                            callusers();
                            name.requestFocus();
                            regbtn.setEnabled(false);
                            Snackbar.make(view, "Registration Successful. Verify Your Account!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            thread3.start();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialogcreate.dismiss();
                    if(e.getMessage().toString().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred."))
                    {
                        Toast.makeText(RegistrationActivity.this, "Please Check Your Internet Connection!!!", Toast.LENGTH_LONG).show();
                    }
                    if(e.getMessage().toString().equals("The email address is already in use by another account."))
                    {
                        user.setError("Existing User!!!");
                        user.requestFocus();
                        Snackbar.make(view,"User Already Exists!!!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    else
                    {
                        Log.d("Error:",e.getMessage());
                    }
                    }
                });

            return null;
        }

        public void callusers()
        {
            users.child("accountcreated").setValue(encryptionDecryption.encrypt(dateregistered));
            users.child("activestatus").setValue(encryptionDecryption.encrypt("1"));
            users.child("city").setValue(encryptionDecryption.encrypt(cityst));
            users.child("district").setValue(encryptionDecryption.encrypt(districtst));
            users.child("dob").setValue(encryptionDecryption.encrypt(dobst.trim()));
            users.child("gender").setValue(encryptionDecryption.encrypt(genderst.trim()));
            users.child("hierarchy").setValue(encryptionDecryption.encrypt("users"));
            users.child("mobile").setValue(encryptionDecryption.encrypt(mobnost.trim()));
            users.child("name").setValue(encryptionDecryption.encrypt(namest));
            users.child("password").setValue(encryptionDecryption.encrypt(passst));
            users.child("state").setValue(encryptionDecryption.encrypt(statest));
            users.child("userid").setValue(encryptionDecryption.encrypt(mailst));
            users.child("userkey").setValue(encryptionDecryption.encrypt(userkey));
            users.child("tnc").setValue(encryptionDecryption.encrypt("NotAgreed"));
        }

        @Override
        protected void onPreExecute() {
            dialogcreate.setIndeterminate(true);
            dialogcreate.setTitle("Creating User...");
            dialogcreate.setCanceledOnTouchOutside(false);
            dialogcreate.setCancelable(false);
            dialogcreate.setMessage("Please Wait!");
            dialogcreate.show();
        }

        @Override
        protected void onPostExecute(String s) {
//            dialog.dismiss();



//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }

//        @Override
//        public void run() {
//            try
//            {
//                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
//                startActivity(intent);
//            }
//            catch (Exception e){}
//        }
    }

}

