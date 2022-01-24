package com.my_first_game;

/*
    リザルト画面の処理
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // スコアを表示
        TextView scoreLabel = findViewById(R.id.scoreLabel);
        int score = getIntent().getIntExtra("SCORE", 0);
        scoreLabel.setText(score + "");

        // ハイスコアの表示
        TextView highScoreLabel = findViewById(R.id.highScoreLabel);
        SharedPreferences sharedPreferences = getSharedPreferences("GAME_DATA", MODE_PRIVATE);
        int highScore = sharedPreferences.getInt("HIGH_SCORE", 0);

        if (score > highScore) {
            highScoreLabel.setText("High Score : " + score);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.apply();

        } else {
            highScoreLabel.setText("High Score : " + highScore);
        }
    }

    // もう一度遊ぶ
    public void tryAgain(View view){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}