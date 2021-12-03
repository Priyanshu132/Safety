package com.hapini.pvt.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {

    private Button SignUp;
    private TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        setIds();
        clickOnSignUp();
        clickOnLogin();
    }

    private void clickOnLogin() {

        login.setOnClickListener(view -> {
            startActivity(new Intent(SignUp.this, Login.class));
        });
    }

    private void setIds() {

        SignUp = findViewById(R.id.signup);
        login = findViewById(R.id.login);
    }
    private void clickOnSignUp() {

        SignUp.setOnClickListener(view -> {
            startActivity(new Intent(SignUp.this, Login.class));
        });
    }


}