package com.example.ivolunteerschedscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fstorage =  FirebaseFirestore.getInstance();
    PhoneAuthProvider.ForceResendingToken Token;
    Boolean verificationProgress = false;


    EditText phone,otp;
    Button verify;
    String verification;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        verify = findViewById(R.id.submit);

        //Autoread OTP
        requestsmspermission();
        new OTP_Receiver().setEditText(otp);

        verification();




    }



        public void verification () {

            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!verificationProgress) {

                        if (!phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10) {
                            String phonenumber = "+63" + phone.getText().toString();
                            requestOTP(phonenumber);
                            verify.setText("Sending...");

                        } else {
                            phone.setError("Phone Number is Not Valid");
                        }

                    } else {

                        String userOTP = otp.getText().toString();
                        PhoneAuthCredential credential;

                        if (!userOTP.isEmpty() && userOTP.length() == 6) {

                            credential = PhoneAuthProvider.getCredential(verification, userOTP);
                            verifyAuth(credential);
                            verify.setEnabled(false);
                            verify.setText("Verifying...");

                        } else {
                            otp.setError("Invalid OTP");
                        }

                    }

                }

            });


        }





        private void checkUser() {
            phone.setEnabled(false);
            //
            final DocumentReference docRef = fstorage.collection("Volunteers").document(fAuth.getCurrentUser().getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {


                            dialogbox();
                            Intent intent = new Intent (getApplicationContext(), Home.class);
                            startActivity(intent);
                            finish();

                    }else {

                        Intent intent = new Intent (getApplicationContext(), Registration.class);
                        startActivity(intent);
                        finish();
                    }

                }
            });




        }





            private void requestOTP(String phonenumber) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verification = s;
                        Token = forceResendingToken;
                        verify.setText("Verify");
                        verificationProgress = true;
                        otp.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,"One Time Password Sent!", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {


                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        Toast.makeText(MainActivity.this,"Cannot Create a Service Provider Account" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }



        private void verifyAuth(PhoneAuthCredential credential) {
            fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()) {
                        checkUser();
                        verify.setEnabled(false);


                    }else {

                        otp.setError("Invalid Verification Code.");

                    }
                }
            });
        }














    @Override
    protected void onStart () {
        super.onStart();
        if(fAuth.getCurrentUser() != null) {
            checkUser();
            verify.setEnabled(false);
            verify.setText("Signing in...");

        }


    }



    //Autoread OTP
    private void requestsmspermission() {
        String smspermission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this,smspermission);
        //check if read SMS permission is granted or not
        if(grant!= PackageManager.PERMISSION_GRANTED)
        {
            String[] permission_list = new String[1];
            permission_list[0]=smspermission;
            ActivityCompat.requestPermissions(this,permission_list,1);
        }
    }






    public void dialogbox () {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.verifying_dialog);
        dialog.show();

    }










}











