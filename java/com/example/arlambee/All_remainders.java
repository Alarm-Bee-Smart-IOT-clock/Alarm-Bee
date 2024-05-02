package com.example.arlambee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class All_remainders extends AppCompatActivity {

    private RecyclerView recyclerView;
    MainAdapter2 mainAdapter2;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_remainders);

        userID = getIntent().getStringExtra("username");

        recyclerView = findViewById(R.id.recycleView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>().
                        setQuery(FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Reminders")
                                ,MainModel.class).
                        build();

        mainAdapter2 = new MainAdapter2(options);
        recyclerView.setAdapter(mainAdapter2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter2.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter2.stopListening();
    }
}