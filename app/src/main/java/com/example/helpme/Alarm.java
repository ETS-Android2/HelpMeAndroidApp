package com.example.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Alarm extends AppCompatActivity {
    TextToSpeech t1;
    ImageButton alarm;
    Button help;
    private final int REQUEST_SPEECH_RECOGNIZER_HOUR = 4000;
    private final int REQUEST_SPEECH_RECOGNIZER_MINUTES = 5000;

    private final int REQUEST_SPEECH_RECOGNIZER_ALARM = 6000;
    String hour,minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        alarm = findViewById(R.id.imageButtonSpeakAlarm);
        help = findViewById(R.id.buttonHelp);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        alarm.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                t1.speak("Which hour", TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say hour");
                t1.speak("Which hour", TextToSpeech.QUEUE_FLUSH, null);
                startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER_ALARM);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_SPEECH_RECOGNIZER_ALARM){
            if(resultCode==RESULT_OK){
                List<String> results = data.getStringArrayListExtra
                        (RecognizerIntent.EXTRA_RESULTS);
                assert results != null;
                hour = results.get(0);
                t1.speak("Which minutes:", TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say minutes");
                startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER_MINUTES);

            }
        }
        if(requestCode==REQUEST_SPEECH_RECOGNIZER_MINUTES){
            List<String> results2 = data.getStringArrayListExtra
                    (RecognizerIntent.EXTRA_RESULTS);
            minute = results2.get(0);
            Toast.makeText(getApplicationContext(),"hour:"+hour+"minute:"+minute,Toast.LENGTH_LONG).show();
            Intent intent=new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            intent.putExtra(AlarmClock.EXTRA_HOUR,Integer.parseInt(hour));
            intent.putExtra(AlarmClock.EXTRA_MINUTES,Integer.parseInt(minute));

            startActivity(intent);
            t1.speak("Alarm set successfully",TextToSpeech.QUEUE_FLUSH,null);

        }

    }
}