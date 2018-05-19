package com.example.subhamdhakal.eavesdrop;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SpectralPeakProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;



public class MainActivity extends AppCompatActivity {
    EditText meditTextName;
    EditText meditTextArtist;
    Spinner mspinnerGeneres;

    String TAG = "Eavesdrop";

    ArrayList<Integer> mylist=new ArrayList<>(5);
    DatabaseReference mSongDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        meditTextName=findViewById(R.id.editTextName);
        meditTextArtist=findViewById(R.id.editTextArtist);
        mspinnerGeneres=findViewById(R.id.spinnerGeneres);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.generes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspinnerGeneres.setAdapter(adapter);


        mSongDatabase= FirebaseDatabase.getInstance().getReference("songs");




        final AudioDispatcher dispatcher =
                AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);


        final Button mRecordButton=findViewById(R.id.recordButton);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PitchDetectionHandler pdh = new PitchDetectionHandler() {
                    @Override
                    public void handlePitch(PitchDetectionResult res, AudioEvent e){

                        final float pitchInHz = res.getPitch();

                        mylist.add((int) res.getPitch());
                        Log.d(TAG, "handlePitch: "+pitchInHz);


//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        });
                    }
                };

                AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
                dispatcher.addAudioProcessor(pitchProcessor);

                Thread audioThread = new Thread(dispatcher, "Audio Thread");
                audioThread.start();
            }
        });

        final Button mRecord=findViewById(R.id.storeButton);
        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatcher.stop();
                calculate();


            }
        });

    }

    private void calculate() {
        Log.d(TAG, "calcubsajldadasdlate: "+mylist.getClass());

        Set<Integer> set=new LinkedHashSet<Integer>(mylist);
        Integer []data=new Integer[set.size()];
        set.toArray(data);
        addSong(data);


    }

    public void addSong(Integer[] data){

        Log.d(TAG, "addSonsadjblasjdg: "+Arrays.toString(data));


        String name=meditTextName.getText().toString().trim();
        String artist=meditTextArtist.getText().toString().trim();

        String genre=mspinnerGeneres.getSelectedItem().toString();

        if(!TextUtils.isEmpty(name) ||!TextUtils.isEmpty(artist)){
            Log.d(TAG, "addSong: inside the if statement");

            String id=mSongDatabase.push().getKey();

            
            StoreData storeData=new StoreData(name,artist,genre,Arrays.toString(data));
            Log.d(TAG, "addSong: after creating the store data object");

            mSongDatabase.child(id).setValue(storeData);

            Toast.makeText(this,"Song Added",Toast.LENGTH_SHORT).show();


        }else{
            Toast.makeText(this,"Fill the text fields",Toast.LENGTH_SHORT).show();
        }
    }
}

