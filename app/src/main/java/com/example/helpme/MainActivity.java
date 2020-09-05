package com.example.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 5000;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent newIntent = new Intent(MainActivity.this, Voice_Commands.class);
                startActivity(newIntent);
                t1.speak("Hi,How can I help? . Tap on screen for help", TextToSpeech.QUEUE_FLUSH, null);
                finish();
            }
        }, SPLASH_TIME_OUT);


    }
}