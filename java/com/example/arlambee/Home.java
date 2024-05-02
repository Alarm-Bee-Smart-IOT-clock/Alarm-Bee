package com.example.arlambee;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    private static final String KEY_USERNAME = "username";
    Button AddAlarm,AddRemainder;
    String username;
    private SharedPreferences sharedPreferences;

    private RelativeLayout parentLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = getIntent().getStringExtra("username");

        sharedPreferences = getSharedPreferences("arlambee_pref", MODE_PRIVATE);


        parentLayout = findViewById(R.id.parent_layout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_help) {
                    openHelpLayout();
                    return true;
                } else if (item.getItemId() == R.id.action_feedback) {
                    openFeedbackLayout();
                    return true;
                }
                return false;
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imageView = findViewById(R.id.list_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the menu programmatically
                openOptionsMenu();
            }
        });


        TextView userNameTextView = findViewById(R.id.user_name);
        getSupportActionBar().setTitle("");

        sharedPreferences = getSharedPreferences("arlambee_pref", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        AddAlarm = findViewById(R.id.addAlarm);
        AddRemainder =findViewById(R.id.addRemainders);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
        } else {
            Toast.makeText(this, "Username not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
            }

        if (username != null) {
            userNameTextView.setText(username);
        }

        AddRemainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, remainder.class);
                intent.putExtra(KEY_USERNAME, username);
                startActivity(intent);
            }
        });
        AddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this,setAlarm.class);
                intent.putExtra(KEY_USERNAME, username);
                startActivity(intent);

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {

           sharedPreferences.edit().remove(KEY_USERNAME).apply();

            Intent intent = new Intent(Home.this, MainActivity.class);
            intent.putExtra(KEY_USERNAME, username);
            startActivity(intent);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void openFeedbackLayout() {
        Intent intent =new Intent(Home.this, Feedback.class);
        intent.putExtra(KEY_USERNAME, username);
        startActivity(intent);
        finish();
    }
    public void openHelpLayout() {
        Intent intent =new Intent(Home.this, Help.class);
        intent.putExtra(KEY_USERNAME, username);
        startActivity(intent);
        finish();
    }
}