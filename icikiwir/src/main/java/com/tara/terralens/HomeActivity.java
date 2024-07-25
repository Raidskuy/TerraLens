package com.tara.terralens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        VideoView videoView = findViewById(R.id.background_video);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.terralens_blackhole);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        // tombol "Take Picture"
        findViewById(R.id.button_take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });

        // tombol "Gallery"
        findViewById(R.id.button_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tambahkan intent untuk mengarahkan ke halaman GalleryActivity
                Intent intent = new Intent(HomeActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        // tombol "Information"
        findViewById(R.id.button_information).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, InformationActivity.class);
                startActivity(intent);
            }
        });
    }
}
