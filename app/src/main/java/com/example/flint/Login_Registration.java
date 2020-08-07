package com.example.flint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login_Registration extends AppCompatActivity {
    private Button Login, Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__registration);

        Login = (Button) findViewById(R.id.Login1);
        Register = (Button) findViewById(R.id.Register1);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Registration.this, Login.class);
                startActivity(intent);
                finish();
                return;

            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Registration.this, Register.class);
                startActivity(intent);
                finish();
                return;

            }
        });

    }
}