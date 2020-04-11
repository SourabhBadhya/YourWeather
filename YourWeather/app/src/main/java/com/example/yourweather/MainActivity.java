package com.example.yourweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText ed1,ed2;
    Button b1,b2;
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHandler(this);
        b1 = (Button) findViewById(R.id.login);
        b2 = (Button) findViewById(R.id.register);
        ed1 = (EditText) findViewById(R.id.email);
        ed2 = (EditText) findViewById(R.id.password);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ed1.getText().toString();
                String password = ed2.getText().toString();
                if (db.isValidUser(email, password)) {
                    Toast.makeText(getApplicationContext(),
                            "Welcome", Toast.LENGTH_SHORT).show();
                    openSectionActivity();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Register Here", Toast.LENGTH_SHORT).show();
                openRegisterActivity();
            }
        });
    }

    public void openRegisterActivity() {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }
    public void openSectionActivity() {
        Intent intent = new Intent(this,SectionActivity.class);
        startActivity(intent);
    }
}
