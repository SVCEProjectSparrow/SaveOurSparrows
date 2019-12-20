package com.svce.sparrowpro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ViewProfile extends AppCompatActivity {

    CardView cardView;
    EditText name,mobno,city,district,state;
    RadioGroup gendergroup;
    RadioButton male,female,transgender,genderbtn;
    ImageView profimage;
    Button regbtn;
    TextView dobtxt,user;
    DatePickerDialog.OnDateSetListener dateSetListener;
    LinearLayout genderlayout;
    String namest,mobnost,cityst,districtst,statest,dobst,genderst,userkey;
    int selectedgender=0;
    Uri profimageuri;
    static final int PICK_IMAGE_REQUEST = 1;
    ProgressDialog dialog;
    Bundle bundle,bundle1;
    UserDetails userDetails;
    SharedPreferences sharedpreferences;
    Calendar cal;
    Date currentdate,dobdate;
    DatePickerDialog datedialog;
    FirebaseDatabase fdatabase;
    DatabaseReference dreference,users;
    FirebaseStorage fstorage;
    StorageReference sreference,userimages;
    StorageTask fuploadtask;
    EncryptionDecryption encryptionDecryption;
    FloatingActionButton edit,cancel;
    FloatingActionMenu floatingActionMenu;
    HashMap<String, Object> regmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        String s[]={ android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,s,1);
        }

        getSupportActionBar().setSubtitle("View Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final View parentView=findViewById(android.R.id.content);
        bundle=getIntent().getExtras();
        userDetails=(UserDetails)bundle.getSerializable("userdetails");
        bundle1=new Bundle();
        bundle1.putSerializable("userdetails",userDetails);


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
        dobst=userDetails.dob;
        namest=userDetails.name;
        mobnost=userDetails.mobile;
        cityst=userDetails.city;
        districtst=userDetails.district;
        statest=userDetails.state;
        genderst=userDetails.gender;
        userkey=userDetails.userkey;
        user=findViewById(R.id.user);
        user.setText(userDetails.username);
        profimageuri=null;
        floatingActionMenu=findViewById(R.id.floatingmenu);
        edit=findViewById(R.id.edit_update);
        cancel=findViewById(R.id.edit_cancel);
        genderlayout=findViewById(R.id.genderlayout);
        name=(EditText)findViewById(R.id.name);
        mobno=(EditText)findViewById(R.id.mobno);
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
        edit_cancel();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_update();
                floatingActionMenu.close(true);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_cancel();
                floatingActionMenu.close(true);
            }
        });

        cal = Calendar.getInstance();
        currentdate = cal.getTime();
        dobtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                datedialog = new DatePickerDialog(
                        ViewProfile.this,
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
                        dobst=userDetails.dob;
                        Snackbar.make(parentView,"Invalid Date of Birth !",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    dobtxt.setText(dobst);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        fdatabase= FirebaseDatabase.getInstance();
        dreference=fdatabase.getReference("sparrow_users");
        fstorage=FirebaseStorage.getInstance();
        sreference=fstorage.getReference("sparrow_users");
        encryptionDecryption=new EncryptionDecryption();

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                namest = name.getText().toString();
                mobnost = mobno.getText().toString();
                cityst = city.getText().toString();
                districtst = district.getText().toString();
                statest = state.getText().toString();
                selectedgender = gendergroup.getCheckedRadioButtonId();
                genderbtn = (RadioButton) findViewById(selectedgender);
                genderst = genderbtn.getText().toString();

                if ((!namest.matches("[a-zA-Z0-9 ]+$")) || (namest.trim().length() == 0)) {
                    name.setError("Only Alphabets");
                    name.requestFocus();
                } else if ((!mobnost.matches("[0-9]{10}$")) || (mobnost.trim().length() == 0)) {
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
                } else if(!getMobileDataState()) {
                    Snackbar.make(view,"Turn On Mobile Data!!!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                } else {
                    UpdateFirebase updateFirebase=new UpdateFirebase(ViewProfile.this,view);
                    updateFirebase.execute("");
                }
            }
        });

        profimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ViewProfile.this,MainActivity.class);
        intent.putExtras(bundle1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_out,R.anim.left_in);
        ViewProfile.this.finish();
    }


    public void edit_update()
    {
        profimageuri=null;
        dobst=userDetails.dob;
        Picasso.get().load(userDetails.profileurl)
                .placeholder(R.drawable.ic_account_circle_black_200dp)
                .resize(400, 400)
                .centerCrop().into(profimage);
        name.setText(namest);
        mobno.setText(mobnost);
        switch (genderst)
        {
            case "Male":
                female.setEnabled(true);
                transgender.setEnabled(true);
                male.setChecked(true);
                male.setEnabled(true);
                break;
            case "Female":
                male.setEnabled(true);
                transgender.setEnabled(true);
                female.setChecked(true);
                female.setEnabled(true);
                break;
            case "Transgender":
                male.setEnabled(true);
                female.setEnabled(true);
                transgender.setChecked(true);
                transgender.setEnabled(true);
                break;
        }
        dobtxt.setText(dobst);
        city.setText(cityst);
        district.setText(districtst);
        state.setText(statest);
        profimage.setEnabled(true);
        name.setEnabled(true);
        mobno.setEnabled(true);
        dobtxt.setEnabled(true);
        city.setEnabled(true);
        district.setEnabled(true);
        state.setEnabled(true);
        regbtn.setVisibility(View.VISIBLE);
        name.requestFocus();
        name.setSelection(userDetails.name.length());
    }

    public void edit_cancel()
    {
        profimageuri=null;
        dobst=userDetails.dob;
        Picasso.get().load(userDetails.profileurl)
                .placeholder(R.drawable.ic_account_circle_black_200dp)
                .resize(400, 400)
                .centerCrop().into(profimage);
        name.setText(namest);
        mobno.setText(mobnost);
        switch (genderst)
        {
            case "Male":
                female.setEnabled(false);
                transgender.setEnabled(false);
                male.setChecked(true);
                male.setEnabled(false);
                break;
            case "Female":
                male.setEnabled(false);
                transgender.setEnabled(false);
                female.setChecked(true);
                female.setEnabled(false);
                break;
            case "Transgender":
                male.setEnabled(false);
                female.setEnabled(false);
                transgender.setChecked(true);
                transgender.setEnabled(false);
                break;
        }
        dobtxt.setText(dobst);
        city.setText(cityst);
        district.setText(districtst);
        state.setText(statest);
        profimage.setEnabled(false);
        name.setEnabled(false);
        mobno.setEnabled(false);
        dobtxt.setEnabled(false);
        city.setEnabled(false);
        district.setEnabled(false);
        state.setEnabled(false);
        regbtn.setVisibility(View.GONE);
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
                Picasso.get().load(profimageuri)
                        .resize(400, 400)
                        .centerCrop().into(profimage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(ViewProfile.this, ""+error, Toast.LENGTH_SHORT).show();
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




    public class UpdateFirebase extends AsyncTask<String,String,String>
    {
        Context context;
        View view;
        UpdateFirebase(Context context,View view)
        {
            this.view=view;
            this.context=context;
            dialog=new ProgressDialog(ViewProfile.this);
        }

        @Override
        protected String doInBackground(String... strings) {

            userimages=sreference.child(userkey);
            users=dreference.child(userkey);
            regmap = new HashMap();

            if(profimageuri==null)
            {
                update();
            }
            else {
                fuploadtask = userimages.putFile(profimageuri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                regmap.put("profilepicurl", encryptionDecryption.encrypt(taskSnapshot.getDownloadUrl().toString()));
                                update();
                                userDetails.setProfileurl(taskSnapshot.getDownloadUrl().toString());
                                afterupdate();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                dialog.dismiss();
                                if (e.getMessage().toString().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                    Toast.makeText(context, "Please Check Your Internet Connection!!!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                createDialog();
                            }
                        });

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            if(profimageuri==null) {
                createDialog();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(profimageuri==null)
            {
                afterupdate();
            }
        }

        public void createDialog()
        {
            dialog.setIndeterminate(true);
            dialog.setTitle("Updating Data...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Please Wait!");
            dialog.show();
        }

        public void update()
        {
            regmap.put("city", encryptionDecryption.encrypt(cityst));
            regmap.put("district", encryptionDecryption.encrypt(districtst));
            regmap.put("dob", encryptionDecryption.encrypt(dobst.trim()));
            regmap.put("gender", encryptionDecryption.encrypt(genderst.trim()));
            regmap.put("mobile", encryptionDecryption.encrypt(mobnost.trim()));
            regmap.put("name", encryptionDecryption.encrypt(namest));
            regmap.put("state", encryptionDecryption.encrypt(statest));
            users.updateChildren(regmap);
        }

        public void afterupdate()
        {
            userDetails.setCity(cityst);
            userDetails.setDistrict(districtst);
            userDetails.setDob(dobst);
            userDetails.setGender(genderst);
            userDetails.setName(namest);
            userDetails.setMobile(mobnost);
            userDetails.setState(statest);
            sharedpreferences = getSharedPreferences("currentUser", 0);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(userDetails);
            editor.putString("currentUsers", "1");
            editor.putString("userjson", json);
            editor.commit();
            bundle1.putSerializable("userdetails", userDetails);
            namest = userDetails.name;
            mobnost = userDetails.mobile;
            cityst = userDetails.city;
            districtst = userDetails.district;
            statest = userDetails.state;
            genderst = userDetails.gender;
            dialog.dismiss();
            Snackbar.make(view, "Update Successful!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            edit_cancel();
        }
    }


}
