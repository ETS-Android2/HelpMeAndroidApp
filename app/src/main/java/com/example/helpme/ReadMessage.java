package com.example.helpme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class ReadMessage extends AppCompatActivity {
    TextView read;
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);
        read = findViewById(R.id.tvReadMessage);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        if (ContextCompat.checkSelfPermission(getApplicationContext(), (Manifest.permission.READ_SMS)) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "hello permission", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(ReadMessage.this, new String[]{Manifest.permission.READ_SMS}, 200);

        } else {
            Uri my_uri = Uri.parse("content://sms/inbox");
            Cursor readFstSms = read.getContext().getContentResolver().query(my_uri, null, null, null, null);
            if (readFstSms.moveToFirst()) {
                String msg_body = readFstSms.getString(readFstSms.getColumnIndexOrThrow("body")).toString();
                String sender_no = readFstSms.getString(readFstSms.getColumnIndexOrThrow("address")).toString();
                String date = readFstSms.getString(readFstSms.getColumnIndexOrThrow("date")).toString();
                String message="Message Body:\n"+msg_body+"\nSender No:\n"+sender_no;
                read.setText(message);
                t1.speak(message,TextToSpeech.QUEUE_FLUSH,null);

            }

            readFstSms.close();

        }

    }
}