package com.example.helpme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class IncomingMessage extends AppCompatActivity {
    TextToSpeech t1;
    TextView read;
    RelativeLayout relativeLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);
        read = findViewById(R.id.tvReadMessage);
        relativeLayout = findViewById(R.id.rl);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                t1.speak("hello", TextToSpeech.QUEUE_FLUSH, null);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), (Manifest.permission.READ_SMS)) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "sms if:", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(IncomingMessage.this, new String[]{Manifest.permission.READ_SMS}, 200);


                } else {

                    Uri my_uri2 = Uri.parse("content://sms/inbox");
                    Cursor readFstSms5 = getApplicationContext().getContentResolver().query(my_uri2, null, null, null, null);
                    if (readFstSms5.moveToFirst()) {
                        String msg_body = readFstSms5.getString(readFstSms5.getColumnIndexOrThrow("body")).toString();
                        String sender_no = readFstSms5.getString(readFstSms5.getColumnIndexOrThrow("address")).toString();
                        String date = readFstSms5.getString(readFstSms5.getColumnIndexOrThrow("date")).toString();
                        String message = "Message Body:\n" + msg_body + "\nSender No:\n" + sender_no;
                        read.setText(message);
                        t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                        readFstSms5.close();

                    }

                }
                return false;
            }


        });


    }

    @Override
    protected void onDestroy() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onDestroy();
    }


}

















