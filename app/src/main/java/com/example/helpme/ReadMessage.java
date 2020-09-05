package com.example.helpme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;


public class ReadMessage extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextView read;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);
        read = findViewById(R.id.tvReadMessage);
        t1 = new TextToSpeech(this, this);
        readMessage();
    }


    public void readMessage() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), (Manifest.permission.READ_SMS)) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "sms if:", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(ReadMessage.this, new String[]{Manifest.permission.READ_SMS}, 200);
            Uri my_uri = Uri.parse("content://sms/inbox");
            Cursor readFstSms = read.getContext().getContentResolver().query(my_uri, null, null, null, null);
            if (readFstSms.moveToFirst()) {
                String msg_body = readFstSms.getString(readFstSms.getColumnIndexOrThrow("body")).toString();
                String sender_no = readFstSms.getString(readFstSms.getColumnIndexOrThrow("address")).toString();
                String date = readFstSms.getString(readFstSms.getColumnIndexOrThrow("date")).toString();
                String message = "Message Body:\n" + msg_body + "\nSender No:\n" + sender_no;
                Toast.makeText(getApplicationContext(), "message:" + message, Toast.LENGTH_LONG).show();
                read.setText(message);
                t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                readFstSms.close();


            }
        } else {
            Toast.makeText(getApplicationContext(), "sms else:", Toast.LENGTH_LONG).show();
            Uri my_uri1 = Uri.parse("content://sms/inbox");
            Cursor readFstSms1 = read.getContext().getContentResolver().query(my_uri1, null, null, null, null);
            if (readFstSms1.moveToFirst()) {
                String msg_body = readFstSms1.getString(readFstSms1.getColumnIndexOrThrow("body")).toString();
                String sender_no = readFstSms1.getString(readFstSms1.getColumnIndexOrThrow("address")).toString();
                String date = readFstSms1.getString(readFstSms1.getColumnIndexOrThrow("date")).toString();
                String message = "Message Body:\n" + msg_body + "\nSender No:\n" + sender_no;
                t1.speak("hello", TextToSpeech.QUEUE_FLUSH, null);
                t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                read.setText(message);
                t1.speak("hello", TextToSpeech.QUEUE_FLUSH, null);
                t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                readFstSms1.close();

            }


        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to shutdown tts!
        if (t1 != null) {
            Log.d("Error", "speech on destroy");
            t1.stop();
            t1.shutdown();
        }
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            //Setting speech Language
            t1.setLanguage(Locale.getDefault());
            t1.setPitch(1);
        } else {
            Log.e("error:", "error");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (t1 != null) {
            t1.shutdown();
        }
    }
}

