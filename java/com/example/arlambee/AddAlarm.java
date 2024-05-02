package com.example.arlambee;

import static com.example.arlambee.MainActivity.KEY_USERNAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddAlarm extends AppCompatActivity {

    private TimePicker timePicker;
    private CheckBox checkBoxDaily;
    private CheckBox checkBoxMondayToFriday;
    private CheckBox checkBoxOnce;
    private DatabaseReference alarmsRef;
    private String username;
    private FirebaseAuth mAuth;
    private int selectedHours;
    private int selectedMinutes;
    private int alarmCounter;
    private String alarmTime;
    private String alarmDescription;
    private boolean isActive;
    RecyclerView recyclerView;

    private BottomNavigationView bottomNavigationView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        timePicker = findViewById(R.id.timePicker);
        checkBoxDaily = findViewById(R.id.checkBoxDaily);
        checkBoxMondayToFriday = findViewById(R.id.checkBoxMondayToFriday);
        checkBoxOnce = findViewById(R.id.checkBoxOnce);

//        recyclerView = findViewById(R.id.rv);

        mAuth = FirebaseAuth.getInstance();

        username = getIntent().getStringExtra("username");

        if (username == null) {
            Log.e("AddAlarmActivity", "User ID is null");
            finish();
            return;
        }

        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");

            if (username != null) {
                alarmsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child("Alarms");
            } else {
                // Handle the case where userID or currentUser is null
                Log.e("AddAlarmActivity", "User ID is null");
                Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // Handle the case where "userID" extra is not passed in the Intent
            Toast.makeText(this, "User ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }
        Button btnSetAlarm = findViewById(R.id.btnSetAlarm);
        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetAlarmButtonClick();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_home) {
                    Intent intent =new Intent(AddAlarm.this, Home.class);
                    intent.putExtra(KEY_USERNAME, username);
                    startActivity(intent);
                    return true;
                }
                // Add more conditions for other menu items if needed
                else {
                    return false;
                }
            }
        });

    }
    public void onSetAlarmButtonClick() {
        int selectedHours, selectedMinutes;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            selectedHours = timePicker.getHour();
            selectedMinutes = timePicker.getMinute();
        } else {
            selectedHours = timePicker.getCurrentHour();
            selectedMinutes = timePicker.getCurrentMinute();
        }

        String alarmTime = String.format("%02d:%02d", selectedHours, selectedMinutes);  // Replace with actual title logic
        String alarmDescription = getCheckBoxDescription();  // Replace with actual description logic
        boolean isActive = true;  // Replace with actual logic


        alarmCounter = getAlarmCounter(username);
        String alarmId = generateAlarmId(alarmCounter);

        saveAlarmToFirebase(alarmId, alarmTime, alarmDescription, isActive);

        String toastMessage = "Alarm set for " + String.format("%02d:%02d", selectedHours, selectedMinutes)
                + "\nRepeat: " + getRepeatOptions();
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(this, setAlarm.class);
//        intent.putExtra("username", username);
//        intent.putExtra("alarmID", alarmId);
//        startActivity(intent);
//        finish();
    }

    private void saveAlarmToFirebase(String alarmId, String alarmTime, String alarmDescription, boolean isActive) {
        if (username != null) {
            DatabaseReference alarmReference = alarmsRef.child(alarmId);

            alarmReference.child("time").setValue(alarmTime);
            alarmReference.child("description").setValue(alarmDescription);
            alarmReference.child("isActive").setValue(isActive);

            // Update the alarm counter in preferences
//            if (alarmCounter == 0) {
//                saveAlarmCounter(1); // Reset the alarm counter to 1 for new users
//            }
//
//            alarmCounter++;
//            saveAlarmCounter(alarmCounter);

            alarmCounter = getAlarmCounter(username);
            if (alarmCounter == 0) {
                // Reset the alarm counter to 1 for new users
                alarmCounter = 1;
                // Save the updated alarm counter to preferences
            } else {
                // Increment the alarm counter for subsequent alarms
                alarmCounter++;
                // Save the updated alarm counter to preferences
            }
            saveAlarmCounter(username, alarmCounter);
            alarmsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<MainModel2> alarms = new ArrayList<>();
                    for (DataSnapshot alarmSnapshot : snapshot.getChildren()) {
                        MainModel2 alarm = alarmSnapshot.getValue(MainModel2.class);
                        alarms.add(alarm);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show();
            finish();
        }


    }


    private String getRepeatOptions() {
        StringBuilder repeatOptions = new StringBuilder();

        if (checkBoxDaily.isChecked()) {
            repeatOptions.append("Daily");
        } else if (checkBoxMondayToFriday.isChecked()) {
            repeatOptions.append("Monday to Friday");
        } else if (checkBoxOnce.isChecked()) {
            repeatOptions.append("Once");
        }

        return repeatOptions.toString();
    }
    private String generateAlarmId(int alarmCounter) {
        return "alarm" + (alarmCounter+1);
    }
    private int getAlarmCounter(String username) {
        SharedPreferences preferences = getSharedPreferences("AlarmPreferences"+ username, MODE_PRIVATE);
        return preferences.getInt("alarmCounter", 0);
    }

    private void saveAlarmCounter(String username, int counter) {
        SharedPreferences preferences = getSharedPreferences("AlarmPreferences"+username, MODE_PRIVATE);
        preferences.edit().putInt("alarmCounter", counter).apply();
    }
    private String getCheckBoxDescription() {
        StringBuilder descriptionBuilder = new StringBuilder();

        if (checkBoxDaily.isChecked()) {
            descriptionBuilder.append("Repeat daily");
        } else if (checkBoxMondayToFriday.isChecked()) {
            descriptionBuilder.append("Repeat Monday to Friday");
        } else if (checkBoxOnce.isChecked()) {
            descriptionBuilder.append("Repeat once");
        }

        return descriptionBuilder.toString();
    }

}