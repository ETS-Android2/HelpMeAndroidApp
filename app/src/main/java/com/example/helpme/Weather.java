package com.example.helpme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class Weather extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
    TextView town, desc, temperature;
    String city = "";
    TextToSpeech t1;



    @Override
    public void onLocationChanged(Location location) {
        try {

            town = findViewById(R.id.town);
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            double longitude = addresses.get(0).getLongitude();
            double latitude = addresses.get(0).getLatitude();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String KnownName = addresses.get(0).getFeatureName();
            String countrycode = addresses.get(0).getCountryCode();
            town.setText(city);
            String content;
            WeatherInner weatherInner = new WeatherInner();
            try {

                content = weatherInner.execute("https://openweathermap.org/data/2.5/weather?q=" + city + "," + countrycode + "&appid=439d4b804bc8187953eb36d2a8c26a02").get();
                Log.i("Content", content);
                JSONObject jsonObject = new JSONObject(content);
                String weatherdata = jsonObject.getString("weather");
                Log.i("Weather Data", weatherdata);
                JSONArray jsonArray = new JSONArray(weatherdata);
                String main = "", description = "", temp = "";
                String mainTemp = jsonObject.getString("main");
                String Visibility = jsonObject.getString("visibility");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject weatherPart = jsonArray.getJSONObject(i);
                    main = weatherPart.getString("main");
                    description = weatherPart.getString("description");
                }
                JSONObject jsonObjectmain = new JSONObject(mainTemp);
                temp = jsonObjectmain.getString("temp");
                Log.i("Temperature", temp);
                Log.i("Main", main);
                Log.i("Description", description);
                Log.i("Visibility", Visibility);
                desc.setText(description);
                temperature.setText(temp);
                t1.speak(city+"Temperature is:"+temp,TextToSpeech.QUEUE_FLUSH,null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class WeatherInner extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... address) {
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1) {
                    ch = (   char) data;
                    content = content + ch;
                    data = isr.read();

                }
                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        desc = findViewById(R.id.desc);
        temperature = findViewById(R.id.temp);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        requestPermission();
        isLocationEnabledOrNot();
        getLocation();



    }


    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 10, (LocationListener) this);

        } catch (SecurityException e) {
            e.printStackTrace();

        }
    }

    private void requestPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 400);


        }
    }

    private void isLocationEnabledOrNot() {
        LocationManager ln = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = ln.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            networkEnabled = ln.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gpsEnabled && !networkEnabled) {
            new AlertDialog.Builder(this).setTitle("Enable GPS Service:").setCancelable(false).setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }).setNegativeButton("Cancel", null)
                    .show();
        }

    }
}