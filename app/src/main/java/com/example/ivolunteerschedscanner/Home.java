package com.example.ivolunteerschedscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Home extends AppCompatActivity {

    FirebaseFirestore fStorage = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    ImageView profile_photo;
    CardView Attendance, Profile, RecordList;
    TextView level, exp_value, hours, nickname;
    ProgressBar level_bar;

    DocumentReference volunteer_id = fStorage.collection("Volunteers").document(fAuth.getCurrentUser().getUid());
    DocumentReference level_id = fStorage.collection("Level").document(fAuth.getCurrentUser().getUid());

    DecimalFormat formater = new DecimalFormat("0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    Attendance = findViewById(R.id.first);
    Profile = findViewById(R.id.third);
    RecordList = findViewById(R.id.second);

    profile_photo = findViewById(R.id.imageView);
    level = findViewById(R.id.level);
    hours = findViewById(R.id.hours);
    exp_value = findViewById(R.id.exp_value);
    nickname = findViewById(R.id.nickname);

    level_bar = findViewById(R.id.level_bar);


        download_pp();
        display_profile();
        display_level();
        camera_permission();


        Attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), Scanner.class));

            }
        });



        Profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(getApplicationContext(), Myprofile.class));
                }
        });


        RecordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MyRecord.class));
            }
        });






    }






    //displaying profile details
    void display_profile() {

        volunteer_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                nickname.setText(documentSnapshot.get("nickname").toString());
            }
        });

    }


    //additional display header
    void display_level() {
        level_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                double level_bar_exp = (double)documentSnapshot.get("level_gauge");
                double ttal_hours = (double) documentSnapshot.get("total_hours");
                double gauge = (double) documentSnapshot.get("level_gauge");


                level.setText(documentSnapshot.get("level").toString());
                hours.setText(ttal_hours + "");
                exp_value.setText(formater.format(gauge) + " %");
                level_bar.setProgress((int) level_bar_exp);

            }
        });

    }


    //user photo download
    void download_pp() {
        String uid = fAuth.getCurrentUser().getUid();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(uid + ".jpeg");
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {
                    Glide.with(profile_photo.getContext()).load(uri).into(profile_photo);
                }
            }
        });

    }




        void camera_permission() {

            int MY_PERMISSIONS_REQUEST_CAMERA=0;
                // Here, this is the current activity
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
                {

                }
                else
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA );
                }
            }


        }





}