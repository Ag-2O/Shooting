package com.my_first_game;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void startGame(View view){
        // メインゲームへ遷移
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}