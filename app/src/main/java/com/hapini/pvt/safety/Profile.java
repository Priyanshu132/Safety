package com.hapini.pvt.safety;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    private TextView name;
    private TextView gender;
    private TextView mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.name);
        name.setText("Priyanshu Gupta");
        gender = findViewById(R.id.gender);
        gender.setText("Male");
        mobile = findViewById(R.id.mobile);
        mobile.setText("7055825661 ");
    }
}