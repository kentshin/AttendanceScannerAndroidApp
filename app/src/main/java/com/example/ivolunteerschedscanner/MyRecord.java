package com.example.ivolunteerschedscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyRecord extends AppCompatActivity {

    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();
    private CollectionReference reference = fstorage.collection("Attendance");
    private RecyclerView record_holder;
    private FirebaseAdapter adapter;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_record);

        record_holder = findViewById(R.id.record_holder);

        display_history();
    }






    public void display_history() {

        Query query = reference.whereEqualTo("volunteer_id", fAuth.getCurrentUser().getUid()).orderBy("time_date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<record_lists> newoptions = new FirestoreRecyclerOptions.Builder<record_lists>()
                .setLifecycleOwner(this)
                .setQuery(query, record_lists.class)
                .build();

        adapter = new FirebaseAdapter(newoptions);
        record_holder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        record_holder.setAdapter(adapter);

    }








}