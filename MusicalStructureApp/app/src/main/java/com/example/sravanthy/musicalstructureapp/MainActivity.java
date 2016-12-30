package com.example.sravanthy.musicalstructureapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnLaunchRock;
    Button btnLaunchRap;
    Button btnLaunchDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLaunchRock = (Button) findViewById(R.id.launchTop100RockSongs);
        btnLaunchRap = (Button) findViewById(R.id.launchTop100RapSongs);
        btnLaunchDetails = (Button) findViewById(R.id.launchCurrentSongDetails);

        btnLaunchRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Top100RockSongs.class);
                startActivity(intent);
            }
        });

        btnLaunchRap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Top100RapSongs.class);
                startActivity(intent);
            }
        });

        btnLaunchDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CurrentSongDetails.class);
                startActivity(intent);
            }
        });
    }
}
