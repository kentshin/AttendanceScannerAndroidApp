package com.example.ivolunteerschedscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Myprofile extends AppCompatActivity {


    private ImageView Profile_photo, edit_detail,editphoto;
    private TextView Fullname, Address, Contact, Email, Nick_name;

    private TextView date_started, level, exp_needed, totalhours;
    private ProgressBar levelbar;

    int Take_Image_Code = 10001;


    FirebaseFirestore fStorage = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    DocumentReference volunteer_id = fStorage.collection("Volunteers").document(fAuth.getCurrentUser().getUid());
    DocumentReference level_id = fStorage.collection("Level").document(fAuth.getCurrentUser().getUid());

    DecimalFormat formater = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        //variable assigning
        editphoto = findViewById(R.id.camera_photo);
        Fullname = findViewById(R.id.name);
        Address = findViewById(R.id.address);
        Contact = findViewById(R.id.contact);
        Email = findViewById(R.id.email);
        Nick_name = findViewById(R.id.nickname);

        date_started = findViewById(R.id.started_date);
        level = findViewById(R.id.level_variable);
        levelbar = findViewById(R.id.level_bar_variable);
        exp_needed = findViewById(R.id.exp_variable);
        totalhours = findViewById(R.id.total_hours_variable);

        Profile_photo = findViewById(R.id.profile_photo);
        edit_detail = findViewById(R.id.edit);





        download_profile_photo();
        display_profile();
        display_level();
        upload();


        edit_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_profile_dialog ();
            }
        });





    }






    //displaying profile details
    void display_profile() {

        volunteer_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Fullname.setText(documentSnapshot.get("firstname").toString() + " " + documentSnapshot.get("lastname").toString());
                Contact.setText(fAuth.getCurrentUser().getPhoneNumber());
                Nick_name.setText(documentSnapshot.get("nickname").toString());
                Address.setText(documentSnapshot.get("address").toString());
                Email.setText(documentSnapshot.get("email").toString());
            }
        });


    }


void display_level() {

    //converting the metadata timestamp long to date
    Long date_reg = fAuth.getCurrentUser().getMetadata().getCreationTimestamp();
    String dateString = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date_reg));
    date_started.setText(dateString);

    level_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {

            double level_bar_exp = (double) documentSnapshot.get("level_gauge");
            double total_hours = (double) documentSnapshot.get("total_hours");
            double expneeded = (double) documentSnapshot.get("exp_needed");


            level.setText(documentSnapshot.get("level").toString());
            exp_needed.setText(formater.format(expneeded) + " exp");
            totalhours.setText(formater.format(total_hours) + " hrs");
            levelbar.setProgress((int) level_bar_exp);


        }
    });


}














    void download_profile_photo() {

        String uid = fAuth.getCurrentUser().getUid();
        //Toast.makeText(getContext(), uid, Toast.LENGTH_LONG).show();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(uid + ".jpeg");
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null) {

                    Glide.with(Profile_photo.getContext()).load(uri).into(Profile_photo);

                }

            }
        });


    }




    //uploading new photo
    public void upload() {

        editphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCamera();

            }
        });

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Take_Image_Code) {

            if(resultCode == RESULT_OK) {

                Bitmap bitmap  = (Bitmap) data.getExtras().get("data");
                Profile_photo.setImageBitmap(bitmap);
                handleUpload(bitmap);


            }
        }

    }


    //handles the photo to the uid of the user
    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream output_image = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output_image);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(fAuth.getCurrentUser().getUid() + ".jpeg");

        mStorageRef.putBytes(output_image.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(Myprofile.this,"Profile Photo Uploaded Successfully", Toast.LENGTH_SHORT).show();

                getImageUrl(mStorageRef);
            }
        });


    }



    //getting the storage reference for the firebase storage
    private void getImageUrl(StorageReference mStorageRef) {
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                setUserProfile(uri);

            }
        });

    }



    //for updating and fetching profile image fetching
    private void setUserProfile(Uri uri) {
        FirebaseUser user = fAuth.getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }


    //permission for camera asking
    private void askCamera(){

        if(ContextCompat.checkSelfPermission(Myprofile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Myprofile.this, new String[]{Manifest.permission.CAMERA}, Take_Image_Code);
        }else {

            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, Take_Image_Code);
        }

    }

    //camera intent
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Take_Image_Code) {
            if(grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, Take_Image_Code);

            }else {

                Toast.makeText(Myprofile.this, "Getting Camera Permission", Toast.LENGTH_SHORT).show();
            }

        }

    }





    //for updating
    public void update_profile_dialog () {
        final Dialog update_dialog = new Dialog(this);
        update_dialog.setContentView(R.layout.edit_profile);


        EditText new_nickname = (EditText) update_dialog.findViewById(R.id.edit_nickname);
        EditText new_email = (EditText) update_dialog.findViewById(R.id.edit_email);
        EditText new_address = (EditText) update_dialog.findViewById(R.id.edit_address);

        Button update = (Button)update_dialog.findViewById(R.id.update);

        final DocumentReference docRef = fStorage.collection( "Volunteers"). document(fAuth.getCurrentUser().getUid());

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    new_nickname.setText(documentSnapshot.get("nickname").toString());
                    new_email.setText(documentSnapshot.get("email").toString());
                    new_address.setText(documentSnapshot.get("address").toString());

                }
            });
        update_dialog.show();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new_address.getText().toString().isEmpty()|| new_email.getText().toString().isEmpty() || new_nickname.getText().toString().isEmpty())
                {
                    Toast.makeText(Myprofile.this, "Please fill up all fields", Toast.LENGTH_LONG);

                } else {
                    fStorage.collection("Volunteers").document(fAuth.getCurrentUser().getUid())
                            .update("address", new_address.getText().toString(), "nickname", new_nickname.getText().toString(), "email", new_email.getText().toString());
                            update_dialog.dismiss();

                            //startActivity(new Intent(getApplicationContext(), Myprofile.class));
                            //Toast.makeText(Myprofile.this, "Profile successfully updated!", Toast.LENGTH_LONG);

                }
            }
        });

    }

















}