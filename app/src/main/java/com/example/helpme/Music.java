package com.example.helpme;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class Music extends Activity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private ArrayList<Song> songList;
    MediaPlayer mediaPlayer;
    ImageButton bstop;
    TextView mtitle, artist, cDuration, tDuration;
    String current, duration;
    SeekBar seekBar;
    Handler seekHandler;
    int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        Random random = new Random();
        pos = random.nextInt(20 - 1) + 1;
        bstop = findViewById(R.id.imageButton);
        mtitle = (TextView) findViewById(R.id.textViewTitle);
        artist = (TextView) findViewById(R.id.textViewArtist);
        tDuration = (TextView) findViewById(R.id.textViewTduration);
        cDuration = (TextView) findViewById(R.id.textView7);
        seekBar = (SeekBar) findViewById(R.id.seekBar2);
        mediaPlayer = new MediaPlayer();
        songList = new ArrayList<Song>();
        getSongList();
        playSong();
        bstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                task.cancel(true);

            }
        });

    }

    Task task;

    private void playSong() {
        try {

            mediaPlayer.reset();
            Song playSong = songList.get(pos);
            mtitle.setText(playSong.getTitle());
            artist.setText(playSong.getArtist());
            long currSong = playSong.getID();
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong);

            try {
                mediaPlayer.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            task = new Task();
            mediaPlayer.prepare();
            task.execute();
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            duration = milliSecondsToTimer(mediaPlayer.getDuration());
            tDuration.setText(duration);
            cDuration.setText(duration);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void getSongList() {
        //retrieve song info
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        } else {

            ContentResolver musicResolver = getContentResolver();
            Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
            if (musicCursor != null && musicCursor.moveToPosition(3)) {
                //get columns
                int titleColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                //add songs to list
                do {
                    long thisId = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    songList.add(new Song(thisId, thisTitle, thisArtist));
                }
                while (musicCursor.moveToNext() && musicCursor.getPosition() != 23);
            }
        }
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public void seekUpdation() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
    }


    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        task.cancel(true);

        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        task.cancel(true);

        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    class Task extends AsyncTask<Integer, Integer, Void> {
        long i = 0;

        @Override
        protected Void doInBackground(Integer... params) {

            while (mediaPlayer.isPlaying()) {
                i = mediaPlayer.getCurrentPosition();
                seekUpdation();
                publishProgress(0);
                i++;
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tDuration.setText("" + milliSecondsToTimer(i));
        }
    }


}
