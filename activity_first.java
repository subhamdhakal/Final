package com.example.subhamdhakal.eavesdrop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class activity_first extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }


    public void findSongClicked(View view){
        Intent intent=new Intent(this,fetchsong.class);
        startActivity(intent);


    }


    public void storeSongClicked(View view){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);


    }
}
