package com.example.ivolunteerschedscanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAdapter extends FirestoreRecyclerAdapter<record_lists, FirebaseAdapter.transholder> {

    private FirebaseFirestore fstorage = FirebaseFirestore.getInstance();

    public FirebaseAdapter(@NonNull FirestoreRecyclerOptions<record_lists> options) {
        super(options);


    }

    @Override
    protected void onBindViewHolder(@NonNull final FirebaseAdapter.transholder holder, int position, @NonNull record_lists model) {


        DocumentReference provider_ref_id = fstorage.collection("Time_Volunteer").document(model.getEvent_id());

        provider_ref_id.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()) {

                    String total_hours = snapshot.getString("_points");
                    holder.hours.setText(total_hours + " total hrs");

                }

            }
        });

        holder.event_name.setText(model.getEvent_id());
        holder.date.setText(model.getTime_date().toDate().toString());


    }


    @NonNull
    @Override
    public FirebaseAdapter.transholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myrecord_list, parent, false);

        return new FirebaseAdapter.transholder(view);
    }






    public class transholder extends RecyclerView.ViewHolder{

        TextView event_name;
        TextView date;
        TextView hours;
        

        public transholder(@NonNull View itemView) {
            super(itemView);

            event_name = itemView.findViewById(R.id.event_name);
            date = itemView.findViewById(R.id.date);
            hours = itemView.findViewById(R.id.hours);

        }
    }
}
