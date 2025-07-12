package com.example.demo_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button about, recommendation, identification, benefits;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons after setContentView
        about = findViewById(R.id.about);
        recommendation = findViewById(R.id.recom);
        identification = findViewById(R.id.identification);
        benefits = findViewById(R.id.medicinal_plant);

        // Set up onClickListeners
        about.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, about.class);
            startActivity(intent);
        });

        benefits.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Benefits.class);
            startActivity(intent);
        });

        identification.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, identification.class);
            startActivity(intent);
        });

        recommendation.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Recommendation.class);
            startActivity(intent);
        });
    }
}
