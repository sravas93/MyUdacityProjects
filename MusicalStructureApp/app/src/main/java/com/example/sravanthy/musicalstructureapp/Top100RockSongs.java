package com.example.sravanthy.musicalstructureapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by sandeep on 12/29/2016.
 */
public class Top100RockSongs extends AppCompatActivity {

    Button btnPlaySong1;
    Button btnPlaySong2;
    Button btnPlaySong3;
    Button btnLaunchHomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top100_rock_songs);

        btnPlaySong1 = (Button) findViewById(R.id.playRockSong1);
        btnPlaySong2 = (Button) findViewById(R.id.playRockSong2);
        btnPlaySong3 = (Button) findViewById(R.id.playRockSong3);
        btnLaunchHomeScreen = (Button) findViewById(R.id.launchHomeScreen);

        btnPlaySong1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Top100RockSongs.this, CurrentSongDetails.class);
                startActivity(intent);
            }
        });

        btnPlaySong2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Top100RockSongs.this, CurrentSongDetails.class);
                startActivity(intent);
            }
        });

        btnPlaySong3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Top100RockSongs.this, CurrentSongDetails.class);
                startActivity(intent);
            }
        });

        btnLaunchHomeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Top100RockSongs.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

