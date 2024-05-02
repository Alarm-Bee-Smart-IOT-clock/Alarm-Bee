package com.example.arlambee;

import static com.example.arlambee.MainActivity.KEY_USERNAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Feedback extends AppCompatActivity {

    private EditText feedbackEditText;
    private EditText emailEditText;
    private Button sendButton;
    private ImageButton backButton;
    private DatabaseReference feedbackRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        feedbackRef = FirebaseDatabase.getInstance().getReference("Users").child("Feedback");
        String username = getIntent().getStringExtra("username");

        feedbackEditText = findViewById(R.id.edit_text_feedback);
        emailEditText = findViewById(R.id.edit_text_email);
        sendButton = findViewById(R.id.button_submit_feedback);
        backButton = findViewById(R.id.button_back);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Feedback.this, Home.class);
                intent.putExtra(KEY_USERNAME, username);
                startActivity(intent);


            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });

    }

    private void sendFeedback() {
        String feedback = feedbackEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (!feedback.isEmpty()) {
            // Push feedback to Firebase database
            String key = feedbackRef.push().getKey();
            feedbackRef.child(key).child("feedback").setValue(feedback);
            feedbackRef.child(key).child("email").setValue(email);

            // Clear input fields
            feedbackEditText.setText("");
            emailEditText.setText("");

            // Show success message
            Toast.makeText(Feedback.this, "Feedback sent successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Show error message if feedback is empty
            Toast.makeText(Feedback.this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
        }
    }
}