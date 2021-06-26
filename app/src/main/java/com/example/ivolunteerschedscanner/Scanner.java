package com.example.ivolunteerschedscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Scanner extends AppCompatActivity {
    FirebaseAuth Fauth = FirebaseAuth.getInstance();
    FirebaseFirestore Fstorage = FirebaseFirestore.getInstance();
    private CodeScanner mCodeScanner;


    final DocumentReference docRef2 = Fstorage.collection( "Level"). document(Fauth.getCurrentUser().getUid());
    final DocumentReference docRef = Fstorage.collection( "Attendance"). document(Fauth.getCurrentUser().getUid());

    double hours_exp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()) {

                                    Toast.makeText(Scanner.this, "Opps! You already signed in for attendance", Toast.LENGTH_LONG).show();

                                }else {

                                                String result_scan = result.getText().toString();
                                                Toast.makeText(Scanner.this, result.getText(), Toast.LENGTH_LONG).show();
                                                final DocumentReference docRef3 = Fstorage.collection( "Time_Volunteer"). document(result.getText());

                                                //getting points for event
                                                docRef3.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        String points = documentSnapshot.get("_points").toString();
                                                        hours_exp = Double.valueOf(points).doubleValue();

                                                    }
                                                });



                                                //updating values of level
                                                docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        double current_exp;
                                                        double base;
                                                        double total_hours;
                                                        double level_gauge;

                                                        long current_level = (long) documentSnapshot.get("level");
                                                        base = (double) documentSnapshot.get("exp_needed");
                                                        current_exp = (double) documentSnapshot.get("current_exp") + hours_exp;
                                                        total_hours = (double) documentSnapshot.get("total_hours") + hours_exp;

                                                        level_gauge = (current_exp/base)*100;
                                                        double new_exp_needed = base*2 - 4;

                                                        //first condition
                                                        if (level_gauge == 100) {
                                                            long new_level = current_level + 1;
                                                            double new_level_gauge = 00;
                                                            double new_current_exp = 00;

                                                            docRef2.update("current_exp", new_current_exp, "total_hours", total_hours, "level_gauge",  new_level_gauge, "exp_needed", new_exp_needed, "level",new_level);
                                                            Toast.makeText(Scanner.this, "CONRGRATULATION! YOU LEVEL UP!", Toast.LENGTH_LONG).show();


                                                        //second condition
                                                        } else if (level_gauge > 100) {
                                                            long new_level = current_level + 1;
                                                            double excess_exp = current_exp - base;
                                                            double new_level_gauge = (excess_exp / base) * 100;

                                                            docRef2.update("current_exp", excess_exp, "total_hours", total_hours, "level_gauge", new_level_gauge, "exp_needed", new_exp_needed, "level", new_level);
                                                            Toast.makeText(Scanner.this, "CONRGRATULATION! YOU LEVEL UP!", Toast.LENGTH_LONG).show();


                                                        //default condition
                                                        }else {

                                                            docRef2.update("current_exp", current_exp, "total_hours", total_hours, "level_gauge", level_gauge);

                                                        }



                                                    }
                                                });
                                                //end for level up


                                                //saving to attendance
                                                Map<String,Object> Attendace = new HashMap<>();
                                                Attendace.put("event_id", result_scan);
                                                Attendace.put("volunteer_id", Fauth.getCurrentUser().getUid());
                                                Attendace.put("time_date", Timestamp.now());

                                                docRef.set(Attendace).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            finish();
                                                            startActivity(new Intent(getApplicationContext(), Home.class));

                                                        }

                                                    }
                                                });
                                                //end for saving

                                            }
                            }
                        });



                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }








}













