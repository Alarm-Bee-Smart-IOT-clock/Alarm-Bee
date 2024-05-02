package com.example.arlambee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class remainder extends AppCompatActivity {

    private TextView selectedDateTextView;
    private EditText reminderEditText;
    private DatabaseReference remindersRef;
    private FirebaseAuth mAuth;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder);



        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        reminderEditText = findViewById(R.id.reminderEditText);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userID = getIntent().getStringExtra("username");

        if (getIntent().hasExtra("username")) {
            userID = getIntent().getStringExtra("username");
            if (userID != null) {
                remindersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Reminders");
            } else {

                Log.e("RemainderActivity", "Username or Current User is null");
                Toast.makeText(this, "Username or Current User is null", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "username not provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, (month + 1), dayOfMonth);
                selectedDateTextView.setText(selectedDate);

            }
        });
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    openBottomSheet();
                    return true;
                } else {
                    if (item.getItemId() == R.id.list) {
                        Intent intent = new Intent(remainder.this, All_remainders.class);
                        intent.putExtra("username", userID);
                        startActivity(intent);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });

    }
    public void addReminder(View view) {
        String selectedDate = selectedDateTextView.getText().toString();
        String reminderText = reminderEditText.getText().toString();

        if (!selectedDate.isEmpty() && !reminderText.isEmpty()) {
            // Get the current count of reminders for the selected date
            DatabaseReference selectedDateRef = remindersRef;
            selectedDateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long reminderCount = snapshot.getChildrenCount() + 1; // Increment the count

                    // Create a new reminder with a unique key based on the count
                    DatabaseReference newReminderRef = selectedDateRef.child("Reminder" + reminderCount);
                    newReminderRef.child("date").setValue(selectedDate);
                    newReminderRef.child("reminderText").setValue(reminderText);

                    Toast.makeText(remainder.this, "Reminder added to Firebase", Toast.LENGTH_SHORT).show();
                    reminderEditText.getText().clear();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(remainder.this, "Failed to add reminder", Toast.LENGTH_SHORT).show();
                }

            });


        } else {
            Toast.makeText(this, "Please enter both date and reminder text", Toast.LENGTH_SHORT).show();
        }
    }
    private void openBottomSheet() {
        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        // Create and show the bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        // Optionally, you can set click listeners for buttons inside the bottom sheet layout
        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
        Button btnOk = bottomSheetView.findViewById(R.id.btnOk);
        DatePicker datePicker = bottomSheetView.findViewById(R.id.datePicker);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                selectedDateTextView.setText(selectedDate);

                bottomSheetDialog.dismiss();
            }
        });
    }

}