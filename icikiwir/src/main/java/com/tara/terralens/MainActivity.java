package com.tara.terralens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        VideoView videoView = findViewById(R.id.background_video);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.terralens_backgroundblue);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        // Add OnClickListener for the "Start" button
        MaterialButton startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(view -> {
            // Intent to navigate to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}
