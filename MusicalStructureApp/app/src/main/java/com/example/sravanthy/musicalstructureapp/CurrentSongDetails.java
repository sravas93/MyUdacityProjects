package com.example.sravanthy.musicalstructureapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by sandeep on 12/29/2016.
 */
public class CurrentSongDetails extends AppCompatActivity {

    Button btnPlayPauseSong;
    Button btnLaunchBuySong;
    Button btnLaunchHomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_song_details);

        btnPlayPauseSong = (Button) findViewById(R.id.playPauseSong);
        btnLaunchBuySong = (Button) findViewById(R.id.launchBuySong);
        btnLaunchHomeScreen = (Button) findViewById(R.id.launchHomeScreen);

        btnPlayPauseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CurrentSongDetails.this, "Play/Pause Song.", Toast.LENGTH_LONG).show();
            }
        });

        btnLaunchBuySong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentSongDetails.this, BuySong.class);
                startActivity(intent);
            }
        });

        btnLaunchHomeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentSongDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
