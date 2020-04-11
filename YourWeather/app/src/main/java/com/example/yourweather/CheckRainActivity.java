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

public class CheckRainActivity extends AppCompatActivity {
    EditText ed3;
    Button b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_rain);
        Toast.makeText(getApplicationContext(),
                "Check Rain Activity",Toast.LENGTH_SHORT).show();
        ed3 = (EditText)findViewById(R.id.editText3);
        b2 = (Button)findViewById(R.id.button3);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String place = ed3.getText().toString();
                System.out.println(place);
                CustomRequest customRequest = new CustomRequest(CheckRainActivity.this);
                customRequest.execute(place);
            }
        });
    }
    class CustomRequest extends AsyncTask<String,String,String> {
        DatabaseHandler databaseHandler;
        public String data = "";
        String response1;
        ProgressDialog progressDialog;
        public CustomRequest(CheckRainActivity checkRainActivity) {
            progressDialog = new ProgressDialog(checkRainActivity);
            databaseHandler = new DatabaseHandler(CheckRainActivity.this);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                data = "";
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + params[0] + "&appid=2c098342ad96366ce3aa78152e661b25");
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
                String description = object.getString("main");
                String city = response.getString("name");
                Intent intent = new Intent(CheckRainActivity.this, InformationActivity.class);
                if (description.equalsIgnoreCase("Rain")) {
                    description = "Yes, it is raining here";
                } else {
                    description = "No, it is not raining here";
                }
                intent.putExtra("temp", temp);
                intent.putExtra("description", description);
                intent.putExtra("city", city);
                startActivity(intent);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
