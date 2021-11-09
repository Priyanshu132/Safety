package com.hapini.pvt.safety;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class Login extends AppCompatActivity {

    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        setIds();
        clickOnLogin();
    }

    private void clickOnLogin() {

        login.setOnClickListener(view -> {
            startActivity(new Intent(Login.this,MainActivity.class));
        });

    }

    private void setIds() {

        login = findViewById(R.id.login);
    }
}