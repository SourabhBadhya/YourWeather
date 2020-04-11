package com.example.yourweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
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

public class SMSActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    EditText ed1,ed2;
    Button b1;
    String message = "";
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        ed1 = (EditText)findViewById(R.id.places);
        ed2 = (EditText)findViewById(R.id.phone);
        b1 = (Button)findViewById(R.id.sendsms);
        sentPI = PendingIntent.getBroadcast(SMSActivity.this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(SMSActivity.this, 0, new Intent(DELIVERED), 0);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placename = ed1.getText().toString();
                CustomRequest customRequest = new CustomRequest(SMSActivity.this);
                phone = ed2.getText().toString();
                customRequest.execute(placename);
                System.out.println(message);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
                        break;

                    //Something went wrong and there's no way to tell what, why or how.
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure!", Toast.LENGTH_SHORT).show();
                        break;

                    //Your device simply has no cell reception. You're probably in the middle of
                    //nowhere, somewhere inside, underground, or up in space.
                    //Certainly away from any cell phone tower.
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service!", Toast.LENGTH_SHORT).show();
                        break;

                    //Something went wrong in the SMS stack, while doing something with a protocol
                    //description unit (PDU) (most likely putting it together for transmission).
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU!", Toast.LENGTH_SHORT).show();
                        break;

                    //You switched your device into airplane mode, which tells your device exactly
                    //"turn all radios off" (cell, wifi, Bluetooth, NFC, ...).
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off!", Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered!", Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered!", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };

        //register the BroadCastReceivers to listen for a specific broadcast
        //if they "hear" that broadcast, it will activate their onReceive() method
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
    }
    class CustomRequest extends AsyncTask<String,String,String> {
        DatabaseHandler databaseHandler;
        public String data = "";
        String response1;
        ProgressDialog progressDialog;
        public CustomRequest(SMSActivity smsActivity) {
            progressDialog = new ProgressDialog(smsActivity);
            databaseHandler = new DatabaseHandler(SMSActivity.this);
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
                Float value = Float.parseFloat(temp);
                value -= 273.15F;
                temp = String.valueOf(value);
                String description = object.getString("description");
                String city = response.getString("name");
                message = "";
                message += "City : " + city + "\n" + "Temperature : " + temp + "\n" + "Status : " + description;
                System.out.println(message);
                if (ContextCompat.checkSelfPermission(SMSActivity.this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(SMSActivity.this, new String [] {Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                else
                {
                    SmsManager sms = SmsManager.getDefault();
                    System.out.println(message + "Hello " + phone);
                    sms.sendTextMessage(phone, null, message, sentPI, deliveredPI);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
