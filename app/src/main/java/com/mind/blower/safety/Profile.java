package com.mind.blower.safety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    private ImageView picture;
    private TextView name;
    private TextView gender;
    private TextView mobile_no;
    private TextView message;
    private Button change;
    private DatabaseReference databaseReference;
    private Button Save;
    private EditText change_message;
    public static final String SHARED_DATA = "name_and_contacts";
    public static final String SHARED_DATA_DETAILS = "personal_details";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final String[] msg = new String[1];
        databaseReference.child("Message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                msg[0] = snapshot.child("message").getValue().toString();
                message.setText(msg[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        picture = findViewById(R.id.image);
        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        mobile_no = findViewById(R.id.mobile);
        message = findViewById(R.id.em_message);
        change = findViewById(R.id.change);
        change_message = findViewById(R.id.edit_message);
        Save = findViewById(R.id.save);

        change.setOnClickListener(View->{

            change_message.setVisibility(android.view.View.VISIBLE);
            change_message.setText("");
            change.setVisibility(android.view.View.GONE);
            Save.setVisibility(android.view.View.VISIBLE);
        });

        Save.setOnClickListener(View->{

            if(change_message.getText().toString().isEmpty()){
                Toast.makeText(this, "Enter Some Message", Toast.LENGTH_SHORT).show();
            }
            else {
                HashMap<String,String> map = new HashMap<>();
                map.put("message",change_message.getText().toString());
                message.setText(change_message.getText().toString());
                saveData(change_message.getText().toString());
                databaseReference.child("Message").setValue(map);
                Save.setVisibility(android.view.View.GONE);
                change_message.setVisibility(android.view.View.GONE);
                change.setVisibility(android.view.View.VISIBLE);


            }
        });
    }

    private void saveData(String mesaage){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_DATA,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("message",mesaage);
        editor.commit();
        editor.apply();
     //   Toast.makeText(getApplicationContext(),sharedPreferences.getString("message"," Gi"),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_DATA_DETAILS,MODE_PRIVATE);

        name.setText(sharedPreferences.getString("name","Name"));
        mobile_no.setText(sharedPreferences.getString("number","Mobile No."));
        gender.setText(sharedPreferences.getString("gender","Gender"));
        message.setText(sharedPreferences.getString("message","Hi, I am in trouble Need Your Help"));

    }
}