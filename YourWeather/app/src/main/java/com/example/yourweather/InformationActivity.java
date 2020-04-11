package com.example.yourweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {
    TextView textView,textView1,textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        textView = (TextView)findViewById(R.id.textView4);
        textView1 = (TextView)findViewById(R.id.textView5);
        textView2 = (TextView)findViewById(R.id.textView6);
        Intent intent = getIntent();
        String temp = intent.getStringExtra("temp");
        Float tempdouble = Float.parseFloat(temp);
        Float tempCelsius = tempdouble - 273.15F;
        String celsius = String.valueOf(tempCelsius);
        String description = intent.getStringExtra("description");
        String city = intent.getStringExtra("city");
        System.out.println(temp + " " + description + " " + city);
        textView.setText("Temperature : " + celsius + "°C");
        textView1.setText("Status : " + description);
        textView2.setText("City : " +  city);
    }
}
