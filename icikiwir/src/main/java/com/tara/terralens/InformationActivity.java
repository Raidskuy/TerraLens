package com.tara.terralens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        // set toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // handle the back button in the toolbar
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}
