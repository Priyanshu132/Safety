package com.mind.blower.safety;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Everything {

    private Context context;
    private Activity activity;
    private ProgressDialog progressDialog;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static final String SHARED_DATA = "name_and_contacts1";
    public static final String SHARED_DATA_MESSAGE = "name_and_contacts";
    public static final String SHARED_DATA_VOICE = "voice";
    private static int MICROPHONE_PERMISSION_CODE = 200;
    private StorageReference storageReference;
    LocationManager locationManager;
    private MediaRecorder mediaRecorder;
    private static final int GPS_TIME_INTERVAL = 1000 * 60 * 5; // get gps location every 1 min
    private static final int GPS_DISTANCE = 1000; // set the distance value in meter
    private static final int HANDLER_DELAY = 1000 * 60 * 5;
    private static final int START_HANDLER_DELAY = 0;

    public Everything(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }


    public void start(){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Send Message
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            sendMessage();
        }
        else{

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.SEND_SMS},100);
        }

        try {
           sendMessage(28.753612,77.4961515);
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
           // getCurrentLocation();
            sendRecoding();
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        // Send Location
//        if(networkInfo != null) {
//
//            try {
//                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
//            }
//            catch (Exception e)
//            {
//                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.d("erroring",e.getMessage());
//            }
//
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
//                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
//                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                Log.e("sd","Entering in networkinfo Function");
//                getCurrentLocation();
//            } else {
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION}, 100);
//            }
//        }


    }

    public void sendRecoding(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
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

                    Toast.makeText(context, "Recording Done Good to go", Toast.LENGTH_SHORT).show();
                    uploadRecordiing();
                }
            }, 5000);
        }
    }

    public void sendNotification(){
        // Send Notification
        Toast.makeText(context, "Send Notification", Toast.LENGTH_SHORT).show();
        String title = "NEED YOUR HELP";
        String msg = "I am in trouble please help me.... See My location From Your Inbox";
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all",title,msg,context,
                activity);
        notificationsSender.SendNotifications();

    }

    public String getRecordingFilePath(){

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File music_file = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(music_file,"emergency_record1"+".mp3");
        return file.getPath();
    }

    public void uploadRecordiing() {

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference filepath = storageReference.child("Emergency_Audio").child("New_Audio.mp3");

        Uri uri = Uri.fromFile(new File(getRecordingFilePath()));
        SharedPreferences sd = context.getSharedPreferences(SHARED_DATA_VOICE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sd.edit();
        Set<String> set = sd.getStringSet("voice",new HashSet<>());

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


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
    private void startRecording(){
        Log.e("sd","Entering in startRecording Function");
        Toast.makeText(context,"Starting Recording",Toast.LENGTH_SHORT).show();
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.prepare();
            mediaRecorder.start();

          //  Toast.makeText(context,"Done Recording",Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getMicrophonePermission(){

        if(ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);
        }
    }

    private void stopRecording(){

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private boolean isMicrophonePresent(){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }
        return false;
    }

    public void sendMessage(double latitude, double longitude) {
        Log.e("sd","Entering in sendMessage Function To send coordinates");
        //saveData();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_DATA,MODE_PRIVATE);
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
          //  Toast.makeText(context,"Alert Everyone Successfully",Toast.LENGTH_SHORT).show();

        }else{
           // Toast.makeText(context,"Contact List is Empty",Toast.LENGTH_SHORT).show();

        }

    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        Log.e("sd","Entering in getCurrentLocation Function");
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
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

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                }
            });
        }
        else{
         //   context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void sendMessage() {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_DATA,MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = context.getSharedPreferences(SHARED_DATA_MESSAGE,MODE_PRIVATE);
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
          //  Toast.makeText(context,"Alert Everyone Successfully",Toast.LENGTH_SHORT).show();

        }else{
           // Toast.makeText(context,"Contact List is Empty",Toast.LENGTH_SHORT).show();

        }
    }


    private void sendMessage(String msg) {

        //  saveData();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_DATA,MODE_PRIVATE);
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


        }else{
         //   Toast.makeText(context,"Contact List is Empty",Toast.LENGTH_SHORT).show();

        }
    }



}
