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
import android.telephony.SmsManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SendMessage extends AppCompatActivity {
    private static final int REQUEST_SPEECH_RECOGNIZER_BODY = 300;
    ImageButton sendMessage;
    TextToSpeech t1;
    String contactName = "";
    Button help;
    private static final int REQUEST_SPEECH_RECOGNIZER_MESSAGE = 200;
    String number;
    RelativeLayout sendHelp;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        help = findViewById(R.id.buttonHelp);
        sendHelp = findViewById(R.id.rlsend);
        sendMessage = findViewById(R.id.imageButtonSpeakMessage);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        sendHelp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SendMessage.this);
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
        sendMessage.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                t1.speak("Give contact name", TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say contact name");
                startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER_MESSAGE);

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

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SPEECH_RECOGNIZER_MESSAGE) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra
                        (RecognizerIntent.EXTRA_RESULTS);
                assert results != null;
                contactName = results.get(0);
                Uri uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};
                String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + contactName + "%'";
                Cursor people = getContentResolver().query(uri, projection, selection, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
                assert people != null;
                int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                people.moveToFirst();
                try {
                    do {
                        number = people.getString(indexNumber);
                        if (number.contains("+91")) {
                            number = number.replace("+91", "");
                        }

                    } while (people.moveToNext() && people.getPosition() != 1);
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say message");
                    startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER_BODY);
                    t1.speak("say message", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), "people:" + people, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "phone:" + number, Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    t1.speak("Sorry no such contact:", TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(this, "Sorry no such contact", Toast.LENGTH_SHORT).show();
                }


            }


        }
        if (requestCode == REQUEST_SPEECH_RECOGNIZER_BODY) {

            List<String> results2 = data.getStringArrayListExtra
                    (RecognizerIntent.EXTRA_RESULTS);
            String message = results2.get(0);
            sendSmsMsgFnc(number, message);
            t1.speak("Message sent successfully", TextToSpeech.QUEUE_FLUSH, null);

        }
    }


    public void sendSmsMsgFnc(String mblNumVar, String smsMsgVar) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(mblNumVar, null, smsMsgVar, null, null);
                t1.speak("Message sent:", TextToSpeech.QUEUE_FLUSH, null);
            } catch (Exception ErrVar) {
                Toast.makeText(getApplicationContext(), ErrVar.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
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