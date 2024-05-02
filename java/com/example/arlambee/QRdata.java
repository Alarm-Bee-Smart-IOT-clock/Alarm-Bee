package com.example.arlambee;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class QRdata extends AppCompatActivity {
    private TextView textViewUsername;
    private TextView textViewLine1;
    private TextView textViewLine2;
    private TextView textViewLine3;

    String username;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "arlambee_pref";
    private static final String KEY_PROCESS_COMPLETED = "process_completed";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdata);

        String username = UserManager.getInstance().getUsername();


        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_PROCESS_COMPLETED, true);
        editor.apply();



//        Intent intent1 = getIntent();
//        if (intent1 != null && intent1.hasExtra("username")) {
//            username = intent1.getStringExtra("username");
//        } else {
//            }

        textViewUsername = findViewById(R.id.textViewUsername);
        textViewLine1 = findViewById(R.id.textViewLine1);
        textViewLine2 = findViewById(R.id.textViewLine2);
        textViewLine3 = findViewById(R.id.textViewLine3);
        Button btn = (Button) findViewById(R.id.finishBtn);

        Intent intent = getIntent();
        if (intent != null) {
            String line1 = intent.getStringExtra("line1");
            String line2 = intent.getStringExtra("line2");
            String line3 = intent.getStringExtra("line3");


            if (line1 != null) {
                textViewLine1.setText(" Look for the WiFi network named \" " + line1 + " \".");
            }
            if (line2 != null) {
                textViewLine2.setText("3. Connect to the hotspot using the password  \" " + line2 + "\".");
            }
            if (line3 != null && !line3.isEmpty()) {
                // Assuming line3 contains the IP address
                String ipAddress = line3;
                String url = "http://" + ipAddress; // Form the URL with the provided IP address

                // Set up clickable link in textViewLine3
                String hyperlink = "<a href='" + url + "'>" + "Click Here ..." + "</a>";
                textViewLine3.setText(Html.fromHtml(hyperlink));
                textViewLine3.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                // Handle the case when line3 is null or empty
                textViewLine3.setText("4. Connect to the hotspot using the provided link");
            }
            }
        String finalUsername = username;
        btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QRdata.this, Home.class);
                    intent.putExtra("username", finalUsername);
                    startActivity(intent);

                }
            });

        }
    }
