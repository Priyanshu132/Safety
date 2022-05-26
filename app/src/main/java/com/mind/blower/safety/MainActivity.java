package com.mind.blower.safety;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button profile;
    private Button test;
    private Button activate;
    private Button deactivate;
    private Button all_contacts;
    private Button recoded_audio;
    private Button add_contact;
    private static final int CONTACT_PERMISSION_CODE = 1;
    private static final int CONTACT_PICK_CODE = 2;
    private DatabaseReference databaseReference;
    AlertDialog.Builder builder;
    public static final String SHARED_DATA = "name_and_contacts1";
    public static final String SHARED_DATA_MESSAGE = "name_and_contacts";
    public static final String SHARED_DATA_VOICE = "voice";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private StorageReference storageReference;
    private static int MICROPHONE_PERMISSION_CODE = 200;
    private MediaRecorder mediaRecorder;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");
        setIds();

        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        profile.setOnClickListener(View->{

            Intent intent = new Intent(getApplicationContext(),Profile.class);
            startActivity(intent);

        });
        deactivate = findViewById(R.id.deactivate);
        deactivate.setOnClickListener(View->{
            deactivate.setVisibility(android.view.View.GONE);
            activate.setVisibility(android.view.View.VISIBLE);

            Intent service = new Intent(MainActivity.this, ScreenOnOffBackgroundService.class);
            service.setAction(ScreenOnOffBackgroundService.ACTION_STOP_FOREGROUND_SERVICE);
            startService(service);

        });
        test.setOnClickListener(View->{
           // clickOnProfile();
            Everything everything = new Everything(getApplicationContext(),MainActivity.this);
            everything.start();
        });
        if(isforegroundServiceRunning()){
            activate.setVisibility(android.view.View.GONE);
            deactivate.setVisibility(android.view.View.VISIBLE);
        }
        activate.setOnClickListener(View->{

            if(!isforegroundServiceRunning()) {
                activate.setVisibility(android.view.View.GONE);
                deactivate.setVisibility(android.view.View.VISIBLE);

                Intent service = new Intent(MainActivity.this, ScreenOnOffBackgroundService.class);
               service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                service.setAction(ScreenOnOffBackgroundService.ACTION_START_FOREGROUND_SERVICE);
                startForegroundService(service);
            }
        });


        clickOnAllContact();
        clickOnAddContact();

        clickOnRecodedAudio();

//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//        intentFilter.addAction(Intent.ACTION_USER_PRESENT);;
//        mReceiver = new ScreenReceiver();
//        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.side_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


//        switch (item.getItemId()) {
//            case R.id.about:
//                startActivity(new Intent(MainActivity.this, RecordedAudio.class));
//                break;
//            case R.id.logout:
//                Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.privacy:
//                Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
//
//
//        }
        return super.onOptionsItemSelected(item);
    }

    public void clickOnRecodedAudio()  {

        recoded_audio.setOnClickListener(view -> {


//            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//            sendIntent.setData(Uri.parse("sms:"+"7055825661"));
//            startActivity(sendIntent);

            Intent intent = new Intent(getApplicationContext(),RecordedAudio.class);
            startActivity(intent);
            //Toast.makeText(getApplicationContext(),"Under Condtruction",Toast.LENGTH_SHORT).show();



        });
    }

    public void uploadRecordiing() {

        progressDialog.setTitle("Uploading Your Recording...");
        progressDialog.setMessage("Please Wait! It will take a few minutes...");
        progressDialog.show();
       StorageReference filepath = storageReference.child("Emergency_Audio").child("New_Audio.mp3");

        Uri uri = Uri.fromFile(new File(getRecordingFilePath()));
        SharedPreferences sd = getSharedPreferences(SHARED_DATA_VOICE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sd.edit();
        Set<String> set = sd.getStringSet("voice",new HashSet<>());

       filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
               Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_SHORT).show();

                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        sendMessage(String.valueOf(uri));
                        set.add(String.valueOf(uri));
                        editor.putStringSet("voice",set);
                        editor.commit();
                        editor.apply();
                    }
                });

           }
       });

    }

    public void startRecording(){

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Toast.makeText(getApplicationContext(),"Done Recording",Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getRecordingFilePath(){

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File music_file = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(music_file,"emergency_record1"+".mp3");
        return file.getPath();
    }

    public boolean isMicrophonePresent(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }
        return false;
    }

    public void getMicrophonePermission(){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);
        }
    }

    public void stopRecording(){

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    public void clickOnRecodedImage() {


       // recoded_images.setOnClickListener(view -> {

           Toast.makeText(getApplicationContext(),"Under Condtruction",Toast.LENGTH_SHORT).show();

      //  });
    }

    public void clickOnGuider() {



     //   guider.setOnClickListener(view -> {


            String title = "NEED YOUR HELP";
            String msg = "I am in trouble please help me.... See My location From Your Inbox";
            FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all",title,msg,getApplicationContext(),
                    MainActivity.this);
            notificationsSender.SendNotifications();

           // Toast.makeText(getApplicationContext(),"Under Condtruction",Toast.LENGTH_SHORT).show();
      //  });
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {


            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();

                    if(location != null){

                        // do whatever you want
                        sendMessage(location.getLatitude(),location.getLongitude());
                    }
                    else{
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000).setFastestInterval(1000).setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                sendMessage(location1.getLatitude(),location.getLongitude());
                                 // values
                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
                    }
                }
            });
        }
        else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void sendMessage(double latitude, double longitude) {

        //saveData();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_DATA,MODE_PRIVATE);
        HashSet<String> Numbers = (HashSet<String>) sharedPreferences.getStringSet("Number",new HashSet<>());
        ArrayList<String> Number_list = new ArrayList<>(Numbers);

        if( Numbers.size() > 0){

            for(int i = 0 ; i < Number_list.size() ;i++){

                String num = Number_list.get(i);
                String msg = latitude +" "+ longitude;

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

    public void clickOnAddContact() {

        add_contact.setOnClickListener(view -> {

                if(checkForPermission()){

                    pickContactIntent();

                }
                else{
                    requestPermission();

                }


        });
    }

    public void pickContactIntent() {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent,CONTACT_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && grantResults.length > 0 && (grantResults[0]+grantResults[1] +
                    grantResults[2]+grantResults[3]+grantResults[4]) == PackageManager.PERMISSION_GRANTED){


        }
        else {
            getPermission();
        }



        }
    public boolean isforegroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager)  getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(ScreenOnOffBackgroundService.class.getName().equals(serviceInfo.service.getClassName())){
                Log.e("Error","Service is already Running");
                return true;
            }
        }
        return false;
    }

    public void getPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
        {


        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.SEND_SMS,
                    Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS,Manifest.permission.FOREGROUND_SERVICE}, 100);
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

    public void addContactToFirebase(String name, String number) {


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

    public boolean checkForPermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == (PackageManager
                .PERMISSION_GRANTED);

        return result;
    }

    public void requestPermission(){

        String[] persmission = {Manifest.permission.READ_CONTACTS};

        ActivityCompat.requestPermissions(this,persmission,CONTACT_PERMISSION_CODE);
    }

    public void clickOnAllContact() {

        all_contacts.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,AllContacts.class));
        });
    }

    public void clickOnProfile() {

     //   test.setOnClickListener(view -> {
           // Toast.makeText(this,"Service is Running...",Toast.LENGTH_SHORT).show();
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            // Send Message
            if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                sendMessage();
            }
            else{

                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},100);
            }


            // Send Location
            if(networkInfo != null) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                }
            }


            // Send Notification
            String title = "NEED YOUR HELP";
            String msg = "I am in trouble please help me.... See My location From Your Inbox";
            FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all",title,msg,getApplicationContext(),
                    MainActivity.this);
            notificationsSender.SendNotifications();


            // Send Recordings
            if(networkInfo != null) {
                if (isMicrophonePresent()) {
                    getMicrophonePermission();
                    startRecording();
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopRecording();

                        Toast.makeText(getApplicationContext(), "Recording Done Good to go", Toast.LENGTH_SHORT).show();
                        uploadRecordiing();
                    }
                }, 5000);
            }



       // });
    }

    public void sendMessage(String msg) {

        //  saveData();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_DATA,MODE_PRIVATE);
        HashSet<String> Numbers = (HashSet<String>) sharedPreferences.getStringSet("Number",new HashSet<>());
        ArrayList<String> Number_list = new ArrayList<>(Numbers);

        if( Numbers.size() > 0){

            for(int i = 0 ; i < Number_list.size() ;i++){

                String num = Number_list.get(i);
             //   String msg = "Hi , I am Priyanshu Gupta I need Your help!";

                if(!num.isEmpty()){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(num,null,msg,null,null);


                }
            }
         //   Toast.makeText(getApplicationContext(),"Alert Everyone Successfully",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(),"Contact List is Empty",Toast.LENGTH_SHORT).show();

        }
    }
    public void sendMessage() {

      //  saveData();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_DATA,MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences(SHARED_DATA_MESSAGE,MODE_PRIVATE);
        HashSet<String> Numbers = (HashSet<String>) sharedPreferences.getStringSet("Number",new HashSet<>());
        ArrayList<String> Number_list = new ArrayList<>(Numbers);

        if( Numbers.size() > 0){

            for(int i = 0 ; i < Number_list.size() ;i++){

                String num = Number_list.get(i);
               String msg = sharedPreferences1.getString("message","Hi, I am in trouble Need Your Help");

            if(!num.isEmpty()){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(num,null, msg,null,null);


                }
            }
            Toast.makeText(getApplicationContext(),"Alert Everyone Successfully",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getApplicationContext(),"Contact List is Empty",Toast.LENGTH_SHORT).show();

        }
    }

    public void setIds() {

        profile = findViewById(R.id.profile);
        all_contacts = findViewById(R.id.all_contacts);
        recoded_audio = findViewById(R.id.recorded_audio);
        test = findViewById(R.id.test);
        add_contact = findViewById(R.id.add_contact);
        activate = findViewById(R.id.activate);
    }

    public void saveData(){
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
               // Toast.makeText(getApplicationContext(),Name_Set.size()+" "+Number_Set.size(),Toast.LENGTH_SHORT).show();

                editor.putStringSet("Name",Name_Set);
                editor.putStringSet("Number",Number_Set);
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

       getPermission();
       saveData();
    }



//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
//            Intent i = new Intent();
//            i.setClass(getApplicationContext(), Profile.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getApplicationContext().startActivity(i);
//        }
//        return true;
//    }
}