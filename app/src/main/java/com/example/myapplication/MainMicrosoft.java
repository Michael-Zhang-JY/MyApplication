package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MainMicrosoft extends AppCompatActivity implements View.OnClickListener {

    protected Button buttonReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main_microsoft);
        buttonReturn = findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(this);
        startTimer();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonReturn) {
            Intent intent = new Intent(MainMicrosoft.this, MainActivity.class);
            intent.putExtra("Post", "PostRequest");
            startActivity(intent);
            finish();
            stopTimer();
        }
    }

    private Timer timer = null;
    private TimerTask timerTask = null;
    private int countdown = 60;

    private void startTimer(){
        if (timer == null){
            timer = new Timer();
        }
        if (timerTask == null){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (countdown > 1){
                        countdown --;
                    }else {
                        Intent intent = new Intent(MainMicrosoft.this, MainActivity.class);
                        intent.putExtra("Post", "PostRequest");
                        startActivity(intent);
                        finish();
                        stopTimer();
                    }
                }
            };
        }
        timer.schedule(timerTask, 1000, 1000);
    }

    private void stopTimer(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        if (timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
    }
}
