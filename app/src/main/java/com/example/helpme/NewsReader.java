package com.example.helpme;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class NewsReader extends AppCompatActivity {
    RecyclerView recyclerView;
    Context context;
    TextView readNews;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsreader);
        readNews = findViewById(R.id.tvNews);
        get_Data_From_Google_News();
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
    }


    public void get_Data_From_Google_News() {


        final StringBuilder disp = new StringBuilder();
        Handler mainhandler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://newsapi.org/v2/top-headlines?sources=google-news&apiKey=b19e48d4d11046b8b037b56de8bab3c9";
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    client.get(url, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.i("jsondata", response.toString());
                            try {
                                JSONArray articles = response.getJSONArray("articles");
                                for (int i = 0; i < articles.length(); i++) {
                                    JSONObject article = (JSONObject) articles.get(i);
                                    Log.i("info", article.getString("title"));
                                    disp.append("\n").append(i + 1).append(":").append(article.getString("title"));

                                }
                                readNews.setText(disp);
                                t1.speak(String.valueOf(disp), TextToSpeech.QUEUE_FLUSH, null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Log.i("Error", Integer.toString(statusCode));
                            Toast.makeText(NewsReader.this, "Failure:" + Integer.toString(statusCode), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Exception:", e.toString());
                }
            }

        };
        mainhandler.post(runnable);


    }


    @Override
    protected void onStop() {
        super.onStop();

        if (t1 != null) {
            t1.shutdown();
        }
    }


}