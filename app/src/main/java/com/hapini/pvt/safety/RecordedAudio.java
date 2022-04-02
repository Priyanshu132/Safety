package com.hapini.pvt.safety;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.HashSet;
import java.util.Set;

public class RecordedAudio extends AppCompatActivity {

    public static final String SHARED_DATA_VOICE = "voice";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorded_audio);

        getSupportActionBar().hide();

        SharedPreferences sd = getSharedPreferences(SHARED_DATA_VOICE,MODE_PRIVATE);
        Set<String> set = sd.getStringSet("voice",new HashSet<>());
        String audio[] = new String[set.size()];
        System.arraycopy(set.toArray(), 0, audio, 0, set.size());
        listView = findViewById(R.id.audio);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecordedAudio.this, android.R.layout.simple_spinner_dropdown_item,audio);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                gotoUrl(audio[i]);
            }
        });


    }

    private void gotoUrl(String audio) {

        Uri uri = Uri.parse(audio);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}