package com.example.arlambee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class setAlarm extends AppCompatActivity implements DeleteBottomSheetFragment.OnDeleteClickListener{

    private String username;
    private RecyclerView recyclerView;


    MainAdapter mainAdapter;

    FloatingActionButton fab;
    private DatabaseReference alarmsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);


        username = getIntent().getStringExtra("username");

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child("Alarms");
        loadAlarms();


//        FirebaseRecyclerOptions<MainModel2> options = new FirebaseRecyclerOptions.Builder<MainModel2>().
//                setQuery(FirebaseDatabase.getInstance().getReference().child("Users").child(username).child("Reminders")
//                        ,MainModel2.class).
//                build();
//
//           mainAdapter = new MainAdapter(options);
//           recyclerView.setAdapter(mainAdapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(setAlarm.this, AddAlarm.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
    private void loadAlarms() {
        FirebaseRecyclerOptions<MainModel2> options =
                new FirebaseRecyclerOptions.Builder<MainModel2>()
                        .setQuery(alarmsRef, MainModel2.class)
                        .build();

        mainAdapter = new MainAdapter(options);
        recyclerView.setAdapter(mainAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }


    @Override
    public void onDeleteClick(String itemId) {
        alarmsRef.child(itemId).removeValue();
    }

    @Override
    public void onDeleteClick() {

    }

    public void showDeleteBottomSheet(String itemId){
        DeleteBottomSheetFragment bottomSheet = DeleteBottomSheetFragment.newInstance(itemId);
        bottomSheet.setOnDeleteClickListener(this);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }
}