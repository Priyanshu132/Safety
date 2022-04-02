package com.hapini.pvt.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    private EditText name;
    private EditText number;
    private EditText message;
    private EditText gender;
    private Button proceed;
    public static final String SHARED_DATA_DETAILS = "personal_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        name = findViewById(R.id.username);
        number = findViewById(R.id.mobile_no);
        message = findViewById(R.id.message);
        gender = findViewById(R.id.gender);
        proceed = findViewById(R.id.proceed);

        proceed.setOnClickListener(View->{


        if(name.getText().toString().isEmpty() || number.getText().toString().isEmpty() || message.getText().toString().isEmpty() ||
                gender.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Fields can't be Empty",Toast.LENGTH_SHORT).show();
        }
        else{

            SharedPreferences sd = getSharedPreferences(SHARED_DATA_DETAILS,MODE_PRIVATE);
            SharedPreferences sharedPreferences = getSharedPreferences("name_and_contacts",MODE_PRIVATE);
            SharedPreferences.Editor editor = sd.edit();
            SharedPreferences.Editor editor1 = sharedPreferences.edit();

            editor1.putString("message",message.getText().toString());


            editor.putString("name",name.getText().toString());
            editor.putString("gender",gender.getText().toString());
            editor.putString("message",message.getText().toString());
            editor.putString("number",number.getText().toString());

            editor.commit();
            editor.apply();
            editor1.commit();
            editor1.apply();

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        });

    }



}