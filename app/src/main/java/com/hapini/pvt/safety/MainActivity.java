package com.hapini.pvt.safety;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button profile;
    private Button all_contacts;
    private Button recoded_images;
    private Button recoded_audio;
    private Button guider;
    private Button add_contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setIds();
        recoded_audio.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(),"good",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,RecordedAudio.class));

        });
        clickOnProfile();
        clickOnAllContact();
        clickOnAddContact();
        clickOnGuider();
        clickOnRecodedImage();
        clickOnRecodedAudio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.side_button,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {




        switch (item.getItemId()){
            case R.id.about:
                startActivity(new Intent(MainActivity.this,RecordedAudio.class));
                break;
            case R.id.logout:
                Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_SHORT).show();
                break;
            case R.id.privacy:
                Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_SHORT).show();


        }
        return super.onOptionsItemSelected(item);
    }

    private void clickOnRecodedAudio() {


    }

    private void clickOnRecodedImage() {

        recoded_images.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,Recorded_images.class));
        });
    }

    private void clickOnGuider() {

        guider.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,Guider.class));
        });
    }

    private void clickOnAddContact() {

        add_contact.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_SHORT).show();
        });
    }

    private void clickOnAllContact() {

        all_contacts.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,AllContacts.class));
        });
    }

    private void clickOnProfile() {

        profile.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,Profile.class));
        });
    }

    private void setIds() {

        profile = findViewById(R.id.profile);
        all_contacts = findViewById(R.id.all_contacts);
        recoded_audio = findViewById(R.id.recorded_audio);
        recoded_images = findViewById(R.id.recorded_picture);
        guider = findViewById(R.id.guider);
        add_contact = findViewById(R.id.add_contact);
    }
}