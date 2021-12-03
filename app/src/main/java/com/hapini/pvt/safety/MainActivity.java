package com.hapini.pvt.safety;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button profile;
    private Button all_contacts;
    private Button recoded_images;
    private Button recoded_audio;
    private Button guider;
    private Button add_contact;
    private static final int CONTACT_PERMISSION_CODE =  1;
    private static final int CONTACT_PICK_CODE =  2;
    private DatabaseReference databaseReference;
    AlertDialog.Builder builder;
    public static final String SHARED_DATA = "name_and_contacts";


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
            //startActivity(new Intent(MainActivity.this,Guider.class));
            //saveData();
        });
    }

    private void clickOnAddContact() {

        add_contact.setOnClickListener(view -> {

                if(checkForPermission()){

                    pickContactIntent();

                }
                else{
                    requestPermission();

                }


        });
    }

    private void pickContactIntent() {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent,CONTACT_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            sendMessage();
        }
        else{
            Toast.makeText(getApplicationContext(),"Permission Denied!",Toast.LENGTH_SHORT).show();
        }

        if(requestCode == CONTACT_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickContactIntent();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == CONTACT_PICK_CODE){

                Uri uri = data.getData();

                Cursor cursor =   getContentResolver().query(uri,null,null,null,null);
                if(cursor.moveToFirst()){

                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Toast.makeText(getApplicationContext(),name+" "+number,Toast.LENGTH_LONG).show();

                    addContactToFirebase(name,number);
                }

            }
        }
    }

    private void addContactToFirebase(String name, String number) {


    databaseReference = FirebaseDatabase.getInstance().getReference();
        builder = new AlertDialog.Builder(this);

        HashMap<String,String> map = new HashMap<>();
        map.put("Name",name);
        map.put("number",number);
        String temp_num = number.charAt(0) == '+' ? number.substring(3):number;
    databaseReference.child("Your_Contacts").child(temp_num).setValue(map);
    saveData();

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.alert_box_layout, null);
        builder.setView(dialoglayout);
        builder.show();
    }

    private boolean checkForPermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == (PackageManager
                .PERMISSION_GRANTED);

        return result;
    }

    private void requestPermission(){

        String[] persmission = {Manifest.permission.READ_CONTACTS};

        ActivityCompat.requestPermissions(this,persmission,CONTACT_PERMISSION_CODE);
    }

    private void clickOnAllContact() {

        all_contacts.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,AllContacts.class));
        });
    }

    private void clickOnProfile() {

        profile.setOnClickListener(view -> {



            if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                sendMessage();
            }
            else{

                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},100);
            }
           // startActivity(new Intent(MainActivity.this,Profile.class));
        });
    }

    private void sendMessage() {
      //  String num = "+917055825661";
        //String msg = "Hi, I am Priyanshu Gupta.";

        saveData();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_DATA,MODE_PRIVATE);
        //HashSet<String> Names = (HashSet<String>) sharedPreferences.getStringSet("Name",new HashSet<>());
        HashSet<String> Numbers = (HashSet<String>) sharedPreferences.getStringSet("Number",new HashSet<>());
        //ArrayList<String> Name_list = new ArrayList<>(Names);
        ArrayList<String> Number_list = new ArrayList<>(Numbers);

        if( Numbers.size() > 0){

            for(int i = 0 ; i < Number_list.size() ;i++){

                String num = Number_list.get(i);
                String msg = "Hi , I am Priyanshu Gupta I need Your help!";

                if(!num.isEmpty()){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(num,null,msg,null,null);


                }
            }
            Toast.makeText(getApplicationContext(),"Alert Everyone Successfully",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(),"Contact List is Empty",Toast.LENGTH_SHORT).show();

        }



    }

    private void setIds() {

        profile = findViewById(R.id.profile);
        all_contacts = findViewById(R.id.all_contacts);
        recoded_audio = findViewById(R.id.recorded_audio);
        recoded_images = findViewById(R.id.recorded_picture);
        guider = findViewById(R.id.guider);
        add_contact = findViewById(R.id.add_contact);
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_DATA,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        HashSet<String> Name_Set;
        Name_Set = new HashSet<>();

        HashSet<String> Number_Set;
        Number_Set = new HashSet<>();

        FirebaseDatabase.getInstance().getReference("Your_Contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()){

                    Name_Set.add((String) ds.child("Name").getValue());
                    Number_Set.add((String) ds.child("number").getValue());

                }
                Toast.makeText(getApplicationContext(),Name_Set.size()+" "+Number_Set.size(),Toast.LENGTH_SHORT).show();

                editor.putStringSet("Name",Name_Set);
                editor.putStringSet("Number",Number_Set);
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}