package com.example.subhamdhakal.eavesdrop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class fetchsong extends AppCompatActivity {
    Button mSendButton;
    String TAG = "Eavesdrop";

    ArrayList<Integer> newList=new ArrayList<>(5);
    ArrayList<Integer> bhai=new ArrayList<>(5);

    DatabaseReference mSongDatabase;
    List<StoreData> mStoreData;
    Integer []calculatedPitchArray;


    TextView msongNameTextView;
    TextView mArtistTextView;
    TextView mgenreTextView;
    TextView mfound;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetchsong);
        mSongDatabase= FirebaseDatabase.getInstance().getReference("songs");
        mStoreData=new ArrayList<>();
        msongNameTextView=findViewById(R.id.nameTextView);
        mArtistTextView=findViewById(R.id.artistTextView);
        mgenreTextView=findViewById(R.id.genreTextView);
        mfound=findViewById(R.id.foundTextView);


        final AudioDispatcher dispatcher =
                AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);


        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){

                final float pitchInHz = res.getPitch();

                newList.add((int) res.getPitch());
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

        mSendButton=findViewById(R.id.sendSongButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatcher.stop();
                calculate();
                retriveData();
            }
        });
    }

    private void calculate() {
        Set<Integer> set=new LinkedHashSet<Integer>(newList);

        calculatedPitchArray=new Integer[set.size()];
        set.toArray(calculatedPitchArray);
        Arrays.sort(calculatedPitchArray, Collections.reverseOrder());
        for(int i=0;i<calculatedPitchArray.length;i++){

            Log.d(TAG, "calculate:the data recorded for the song is  "+calculatedPitchArray[i]);
        }
    }

    public void retriveData(){
        mSongDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mStoreData.clear();
                Log.d(TAG, "onDataChange: acscasdd");
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    StoreData storeData=new StoreData();
                    storeData.setSongName(snapshot.getValue(StoreData.class).getSongName());
                    storeData.setSongArtist(snapshot.getValue(StoreData.class).getArtist());
                    storeData.setSongGenre(snapshot.getValue(StoreData.class).getSongGenre());
                    storeData.setSongPitch(snapshot.getValue(StoreData.class).getSongPitch());

                    Log.d(TAG, "onDataChange:name "+storeData.getSongName());
                    Log.d(TAG, "onDataChange:artist "+storeData.getArtist());
                    Log.d(TAG, "onDataChange:Genre "+storeData.getSongGenre());
                    Log.d(TAG, "onDataChange:pitch "+storeData.getSongPitch());

                    int matchingValues=comparision(storeData.getSongPitch());

                    if (matchingValues>0){
                        Log.d(TAG, "onDataChange:found the song "+storeData.getSongName());
                        msongNameTextView.setText(storeData.getSongName());
                        mArtistTextView.setText(storeData.getArtist());
                        mgenreTextView.setText(storeData.getSongGenre());
                        mfound.setText("Found");

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int comparision(String songPitch) {
        int matchingValues=0;

        Log.d(TAG, "comparision:inside comparision"+songPitch);
// First split the input String into an array,
// each element containing a String to be parse as an int
        String[] intsToParse = songPitch.split(",");

        int[] retrievedPitchArray = new int[intsToParse.length-2];

        for (int i = 0; i < retrievedPitchArray.length-4; i++)
        {
            retrievedPitchArray[i] = Integer.parseInt(intsToParse[i+1].trim());
            Log.d(TAG, "comparision: "+retrievedPitchArray[i]);
        }
        for(int i=0;i<retrievedPitchArray.length;i++){
            for(int j=0;j<calculatedPitchArray.length;j++){
                if(retrievedPitchArray[i]==calculatedPitchArray[j]){
                    matchingValues++;
                    Log.d(TAG, "comparision:matched value "+matchingValues);
                }
            }
        }


        return matchingValues;

    }


}
