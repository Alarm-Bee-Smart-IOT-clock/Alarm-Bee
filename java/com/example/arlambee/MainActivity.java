package com.example.arlambee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersReference = database.getReference("Users");
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "arlambee_pref";
    private static final String KEY_PROCESS_COMPLETED = "process_completed";
    static final String KEY_USERNAME = "username";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username != null) {
            Intent intent = new Intent(MainActivity.this, Home.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish(); // Finish the current activity to prevent back navigation
        } else {
            findViewById(R.id.getStartedButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showUsernameDialog();
                }
            });
        }

    }
    private void showUsernameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_username, null);
        dialogBuilder.setView(dialogView);

       final EditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);

        dialogBuilder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                 String username = editTextUsername.getText().toString().trim();
                if (username.isEmpty()) {
                    Toast.makeText(MainActivity.this, "User name is not provided.", Toast.LENGTH_SHORT).show();
                    } else {
                    isUsernameProvided(username, new UsernameCheckListener() {
                        @Override
                        public void onUsernameCheckComplete(boolean usernameExists) {
                            if (usernameExists) {
                                Toast.makeText(MainActivity.this, "User name already provided.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Username does not exist in the database, proceed
                                dialog.dismiss();
                                saveUsername(username);
                                UserManager.getInstance().setUsername(username);
                                uploadUsernameToDatabase(username);
                                showQRCodeDialog(username);
                            }
                        }
                    });
//                    saveUsername(username);
//                    UserManager.getInstance().setUsername(username);
//                    usersReference.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DataSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                DataSnapshot snapshot = task.getResult();
//                                if (snapshot.exists()) {
//                                    // Username already exists in the database
//                                    Toast.makeText(MainActivity.this, "User name already provided.", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    // Username does not exist in the database, proceed
//                                    dialog.dismiss();
//                                    uploadUsernameToDatabase(username);
//                                    showQRCodeDialog(username);
//                                }
//                            } else {
//                                // Error occurred while accessing Firebase
//                                Toast.makeText(MainActivity.this, "Failed to check username.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
                    }


            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADD8E6"))); // Light blue color
        alertDialog.show();
    }
    private void saveUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    private void showQRCodeDialog(String username) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Scan your QR code");

        dialogBuilder.setPositiveButton("Scan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                checkCameraPermission();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ADD8E6")));
        alertDialog.show();
    }
    private void uploadUsernameToDatabase(String username) {
        DatabaseReference userReference = usersReference.child(username);

        // Example: Creating child references for alarms and reminders
        DatabaseReference alarmsReference = userReference.child("Alarms");
        DatabaseReference remindersReference = userReference.child("Reminders");

        alarmsReference.setValue(""); // Setting an empty value for "Arlam" node
        remindersReference.setValue("");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_PROCESS_COMPLETED, true);
        editor.apply();


    }
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startQRCodeScan();
        }
    }
    private void startQRCodeScan() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setPrompt("Scan QR code");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String contents = intentResult.getContents();
            if (contents != null) {
                String[] lines = contents.split("\n");
                if (lines.length >= 3) { // Ensure at least 3 lines are present
                    String line1 = lines[0];
                    String line2 = lines[1];
                    String line3 = lines[2];

                    Toast.makeText(this, "QR Code scanned: " + contents, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, QRdata.class);
                    intent.putExtra(KEY_USERNAME, username);
                    intent.putExtra("line1", line1);
                    intent.putExtra("line2", line2);
                    intent.putExtra("line3", line3);

                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Failed to scan QR code", Toast.LENGTH_SHORT).show();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRCodeScan();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void isUsernameProvided(String username, UsernameCheckListener listener) {
        DatabaseReference userReference = usersReference.child(username);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean usernameExists = dataSnapshot.exists();
                listener.onUsernameCheckComplete(usernameExists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onUsernameCheckComplete(false); // Assume username doesn't exist in case of error
            }
        });
    }


}