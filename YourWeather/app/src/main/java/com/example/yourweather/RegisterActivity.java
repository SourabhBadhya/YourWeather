package com.example.yourweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHandler db;
    Button register,loginpage;
    EditText ed1,ed2,ed3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DatabaseHandler(this);
        register = (Button)findViewById(R.id.register);
        loginpage = (Button)findViewById(R.id.login);
        ed1 = (EditText)findViewById(R.id.email);
        ed2 = (EditText)findViewById(R.id.password);
        ed3 = (EditText)findViewById(R.id.confirmpassword);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ed1.getText().toString();
                String password = ed2.getText().toString();
                String confirmpassword = ed3.getText().toString();
                if (confirmpassword.equals(password)) {
                    if (db.checkDuplicateEmail(email)) {
                        db.insert(email,password);
                        Toast.makeText(getApplicationContext(),"You have successfully registered",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email ID already exists",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Passwords do not match",Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });
    }
    public void openLoginActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
