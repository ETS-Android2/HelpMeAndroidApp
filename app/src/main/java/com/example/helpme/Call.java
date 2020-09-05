package com.example.helpme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Call extends AppCompatActivity {
    private final int REQUEST_SPEECH_RECOGNIZER_CALL = 3000;
    TextToSpeech t1;
    String phoneNumber;
    String callAnswer = "";
    ImageButton phone;
    Button help;
    RelativeLayout callHelp;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        phone = findViewById(R.id.imageButtonSpeakPhone);
        help = findViewById(R.id.buttonHelp);
        callHelp = findViewById(R.id.callHelp);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t1.speak("Give contact name :", TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say contact name");
                startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER_CALL);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Help");
                String string = getString(R.string.help_text);
                builder.setMessage(string);
                final AlertDialog closedialog = builder.create();
                closedialog.show();
                t1.speak(string, TextToSpeech.QUEUE_FLUSH, null);
                final Timer timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    public void run() {
                        closedialog.dismiss();
                        timer2.cancel(); //this will cancel the timer of the system
                    }
                }, 15000); // the timer will count 5 seconds....

            }
        });
        callHelp.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(@SuppressLint("ClickableViewAccessibility") View view, MotionEvent motionEvent) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Call.this);
                builder.setTitle("Help");
                String string = getString(R.string.help_text);
                builder.setMessage(string);
                final AlertDialog closedialog = builder.create();
                closedialog.show();
                t1.speak(string, TextToSpeech.QUEUE_FLUSH, null);
                final Timer timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    public void run() {
                        closedialog.dismiss();
                        timer2.cancel(); //this will cancel the timer of the system
                    }
                }, 15000);   // the timer will count 5 seconds....

                return false;

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();
        if (requestCode == REQUEST_SPEECH_RECOGNIZER_CALL) {

            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra
                        (RecognizerIntent.EXTRA_RESULTS);
                callAnswer = results.get(0);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);

                } else {
                    Uri uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
                    String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER};
                    String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + callAnswer + "%'";
                    Cursor people = getContentResolver().query(uri, projection, selection, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
                    int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    people.moveToFirst();
                    try {
                        do {
                            phoneNumber = people.getString(indexNumber);
                            if (phoneNumber.contains("+91")) {
                                phoneNumber = phoneNumber.replace("+91", "");
                            }

                        } while (people.moveToNext() && people.getPosition() != 1);
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 0);
                            Intent callIntent1 = new Intent(Intent.ACTION_CALL);
                            callIntent1.setData(Uri.parse("tel:" + phoneNumber));
                            startActivity(callIntent1);
                            t1.speak("Making call:", TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            Intent callIntent2 = new Intent(Intent.ACTION_CALL);
                            callIntent2.setData(Uri.parse("tel:" + phoneNumber));
                            startActivity(callIntent2);
                            t1.speak("Making call:", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    } catch (Exception ex) {
                        t1.speak("Sorry no such contact:", TextToSpeech.QUEUE_FLUSH, null);
                        Toast.makeText(this, "Sorry no such contact", Toast.LENGTH_SHORT);
                    }
                }


            }
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




