package com.example.yourweather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LatitudeActivity extends AppCompatActivity {
    EditText ed1,ed2;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latitude);
        ed1 = (EditText)findViewById(R.id.latitude);
        ed2 = (EditText)findViewById(R.id.longitude);
        b1 = (Button)findViewById(R.id.latlongweather);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude = ed1.getText().toString();
                String longitude = ed2.getText().toString();
                System.out.println(latitude + " " + longitude);
                CustomRequest customRequest = new CustomRequest(LatitudeActivity.this);
                customRequest.execute(latitude,longitude);
            }
        });
    }
    class CustomRequest extends AsyncTask<String,String,String> {
        DatabaseHandler databaseHandler;
        public String data = "";
        String response1;
        ProgressDialog progressDialog;
        public CustomRequest(LatitudeActivity latitudeActivity) {
            progressDialog = new ProgressDialog(latitudeActivity);
            databaseHandler = new DatabaseHandler(LatitudeActivity.this);
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
                Intent intent = new Intent(LatitudeActivity.this, InformationActivity.class);
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

