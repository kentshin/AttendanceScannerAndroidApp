package com.example.ivolunteerschedscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    FirebaseAuth Fauth = FirebaseAuth.getInstance();
    FirebaseFirestore Fstorage = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef;

    ImageView click_photo, Profile_photo;
    EditText fn, ln, nick_name, email, address;
    private Button save;

    int Take_Image_Code = 10001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        Profile_photo = findViewById(R.id.profile_photo);
        click_photo = findViewById(R.id.click_camera);

        fn = findViewById(R.id.fn);
        ln = findViewById(R.id.ln);
        nick_name = findViewById(R.id.nick_name);
        email = findViewById(R.id.email);
        address = findViewById(R.id.volunter_address);

        save = findViewById(R.id.save);


        upload();
        save();




    }





    //functions below


    public void save() {

        final DocumentReference docRef = Fstorage.collection( "Volunteers"). document(Fauth.getCurrentUser().getUid());
        final DocumentReference docRef_level = Fstorage.collection( "Level"). document(Fauth.getCurrentUser().getUid());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fn.getText().toString().isEmpty() || ln.getText().toString().isEmpty( ) || address.getText().toString().isEmpty( )||nick_name.getText().toString().isEmpty( ) ||email.getText().toString().isEmpty( ) ) {

                    Toast.makeText(getApplicationContext(), "All fields are Required.", Toast.LENGTH_SHORT).show();
                }

                else {

                    String firstname = fn.getText().toString();
                    String lastname = ln.getText().toString();
                    String nickname = nick_name.getText().toString();
                    String email_add = email.getText().toString();
                    String address_= address.getText().toString();
                    int user_level = 01;
                    double current_exp = 00;
                    double exp_needed = 5;
                    double level_gauge = 00;
                    double total_hours = 00;



                    Map<String,Object> Users = new HashMap<>();

                    Users.put("firstname", firstname );
                    Users.put("lastname", lastname );
                    Users.put("nickname", nickname);
                    Users.put("email", email_add);
                    Users.put("address", address_ );
                    Users.put("contact", Fauth.getCurrentUser().getPhoneNumber().toString());


                    Map<String,Object> Level = new HashMap<>();
                    Level.put("current_exp", current_exp );
                    Level.put("exp_needed", exp_needed );
                    Level.put("level", user_level );
                    Level.put("level_gauge", level_gauge);
                    Level.put("total_hours", total_hours);





                    //for level details
                    docRef_level.set(Level).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });



                    //for profile
                    docRef.set(Users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),"iLevel App Volunteer Account Created", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), Home.class));


                            }else {
                                Toast.makeText(getApplicationContext(),"There's a problem creating a user account. Please try again in a moment.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });










                }
            }
        });

    }



    public void upload() {

        click_photo.setOnClickListener(new View.OnClickListener() {
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

                mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(Fauth.getCurrentUser().getUid() + ".jpeg");
                mStorageRef.putBytes(output_image.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

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
                FirebaseUser user = Fauth.getCurrentUser();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();

                user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Registration.this,"Profile Photo Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                });

            }


            //permission for camera asking
            private void askCamera(){

                if(ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Registration.this, new String[]{Manifest.permission.CAMERA}, Take_Image_Code);
                }else {

                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera_intent, Take_Image_Code);
                }

            }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == Take_Image_Code) {
                if(grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera_intent, Take_Image_Code);

                }else {

                    Toast.makeText(Registration.this, "Getting Camera Perssion", Toast.LENGTH_SHORT).show();
                }

            }

        }













}