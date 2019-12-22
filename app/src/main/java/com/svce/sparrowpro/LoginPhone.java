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

public class LoginPhone extends AppCompatActivity {
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
        setContentView(R.layout.loginphone);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        String s[]={ android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};


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
    }

}

