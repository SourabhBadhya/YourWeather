package com.example.yourweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SectionActivity extends AppCompatActivity {
    Button b1,b2,b3,b4,b5;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    TextView tempText,statusText,cityText;
    // GPSTracker class
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        b1 = (Button)findViewById(R.id.latlong);
        b2 = (Button)findViewById(R.id.place);
        b3 = (Button)findViewById(R.id.checkrain);
        b4 = (Button)findViewById(R.id.sms);
        b5 = (Button)findViewById(R.id.location);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLatitudeActivity();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeatherActivity();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCheckRainActivity();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSMSActivity();
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocationActivity();
            }
        });
    }

    public void openLatitudeActivity() {
        Intent intent = new Intent(this,LatitudeActivity.class);
        startActivity(intent);
    }

    public void openWeatherActivity() {
        Intent intent = new Intent(this,WeatherActivity.class);
        startActivity(intent);
    }

    public void openCheckRainActivity() {
        Intent intent = new Intent(this,CheckRainActivity.class);
        startActivity(intent);
    }
    public void openLocationActivity() {
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        gps = new GPSTracker(SectionActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String longitudeinString = String.valueOf(longitude);
            String latitudeinString = String.valueOf(latitude);
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            CustomRequest customRequest = new CustomRequest(SectionActivity.this);
            customRequest.execute(latitudeinString,longitudeinString);
        } else {
            gps.showSettings();
        }
    }
    public void openSMSActivity() {
        Intent intent = new Intent(this,SMSActivity.class);
        startActivity(intent);
    }
    class CustomRequest extends AsyncTask<String,String,String> {
        DatabaseHandler databaseHandler;
        public String data = "";
        String response1;
        ProgressDialog progressDialog;
        public CustomRequest(SectionActivity sectionActivity) {
            progressDialog = new ProgressDialog(sectionActivity);
            databaseHandler = new DatabaseHandler(SectionActivity.this);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                data = "";
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + params[0] + "&lon=" + params[1] + "&appid=2c098342ad96366ce3aa78152e661b25");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while(line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }
            } catch(MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e1) {
                e1.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Retrieving data, please wait.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            System.out.println(result);
            try {
                response1 = result;
                JSONObject response = new JSONObject(response1);
                JSONObject main = response.getJSONObject("main");
                JSONArray array = response.getJSONArray("weather");
                JSONObject object = array.getJSONObject(0);
                String temp = String.valueOf(main.getDouble("temp"));
                String description = object.getString("description");
                String city = response.getString("name");
                Intent intent = new Intent(SectionActivity.this, InformationActivity.class);
                intent.putExtra("temp", temp);
                intent.putExtra("description", description);
                intent.putExtra("city", city);
                startActivity(intent);
            } catch(JSONException e) {
                Toast.makeText(getApplicationContext(),
                        "Invalid Coordinates", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
