package com.example.helpme;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Voice_Commands extends AppCompatActivity {
    Button help;
    TextToSpeech t1;
    ImageButton speakCommand;
    int REQUEST_SPEECH_RECOGNIZER1 = 100;
    String mAnswer;
    Date currentDate;
    TextView dateTime;
    String date;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice__commands);
        help = findViewById(R.id.buttonHelp);
        dateTime=findViewById(R.id.textView7Date);
        show=findViewById(R.id.textViewShow);
        DateFormat df = new SimpleDateFormat("EEEE, dd MMMM yyyy, h:mm a");
        date = df.format(Calendar.getInstance().getTime());
        dateTime.setText(date);
        speakCommand = findViewById(R.id.imageButtonSpeak);
        t1 = new TextToSpeech(getApplicationContext(),  new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        show.setText("What can I do you for today:");


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
                }, 15000);   // the timer will count 5 seconds....

            }
        });
        speakCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                t1.speak("Say command:", TextToSpeech.QUEUE_FLUSH, null);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Commands");
                startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SPEECH_RECOGNIZER1) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra
                        (RecognizerIntent.EXTRA_RESULTS);
                mAnswer = results.get(0);
                if (mAnswer.equalsIgnoreCase("call")) {
                    t1.speak("Call", TextToSpeech.QUEUE_FLUSH, null);
                    Intent call = new Intent(Voice_Commands.this, Call.class);
                    startActivity(call);
                } else if (mAnswer.equalsIgnoreCase("message")) {
                    t1.speak("message", TextToSpeech.QUEUE_FLUSH, null);
                    Intent call = new Intent(Voice_Commands.this, SendMessage.class);
                    startActivity(call);
                } else if (mAnswer.equalsIgnoreCase("save")) {
                    t1.speak("Save", TextToSpeech.QUEUE_FLUSH, null);
                    Intent save = new Intent(Voice_Commands.this, SaveContact.class);
                    startActivity(save);
                }  else if (mAnswer.equalsIgnoreCase("stop")) {
                        t1.stop();
                }
                else if (mAnswer.equalsIgnoreCase("read")) {
                    t1.speak("Read message", TextToSpeech.QUEUE_FLUSH, null);
                    Intent alarm = new Intent(Voice_Commands.this, ReadMessage.class);
                    startActivity(alarm);
                }else if (mAnswer.equalsIgnoreCase("set alarm")) {
                    t1.speak("Set alarm", TextToSpeech.QUEUE_FLUSH, null);
                    Intent alarm = new Intent(Voice_Commands.this, Alarm.class);
                    startActivity(alarm);
                } else if (mAnswer.equalsIgnoreCase("music")) {
                    t1.speak("Playing music", TextToSpeech.QUEUE_FLUSH, null);
                    Intent music = new Intent(Voice_Commands.this, Music.class);
                    startActivity(music);
                } else if (mAnswer.equalsIgnoreCase("weather")) {
                    t1.speak("Telling weather", TextToSpeech.QUEUE_FLUSH, null);
                    Intent weather = new Intent(Voice_Commands.this, Weather.class);
                    startActivity(weather);
                } else if ((mAnswer.equalsIgnoreCase("date"))||(mAnswer.equalsIgnoreCase("time"))) {
                    t1.speak("Date and time is"+date, TextToSpeech.QUEUE_FLUSH, null);

                } else if (mAnswer.equalsIgnoreCase("location")) {
                    Intent loc=new Intent(Voice_Commands.this,CurrentLocation.class);
                    startActivity(loc);

                } else {
                    Intent viewSearch = new Intent(Intent.ACTION_WEB_SEARCH);
                    viewSearch.putExtra(SearchManager.QUERY, mAnswer);
                    startActivity(viewSearch);

                }


            }
        }
    }
}




































































































