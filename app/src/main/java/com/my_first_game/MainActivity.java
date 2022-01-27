package com.my_first_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;

/*
    メイン画面
 */

public class MainActivity extends AppCompatActivity {
    // 画面幅と高さ
    public int gvWidth, gvHeight;
    // 射撃モード(0-3)
    public int fireMode = 0;
    // surfaces
    public GameView gv;

    //TODO: 敵の体力ゲージ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        // 画面サイズ取得
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        Window window = getWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager manager = window.getWindowManager();
        Display display = manager.getDefaultDisplay();
        gvWidth = display.getWidth();
        gvHeight = display.getHeight();

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        // Viewサイズを取得
        GameView gv = (GameView) findViewById(R.id.gameview);
        gvWidth = gv.getWidth();
        gvHeight = gv.getHeight();
        Log.v("onWindowFocusChanged", "width=" + gvWidth + ", height=" + gvHeight);
    }

    // 射撃モード変更
    public void modifyFireMode(View view){
        ImageButton imageButton = (ImageButton) findViewById(R.id.imagebutton);
        if(fireMode == 0){
            fireMode = 1;
            imageButton.setImageResource(R.drawable.doublebullets);
        }else if (fireMode == 1){
            fireMode = 2;
            imageButton.setImageResource(R.drawable.circlebullets);
        }else{
            fireMode = 0;
            imageButton.setImageResource(R.drawable.onebullet);
        }
    }

    // 射撃モードを返す
    public int getFireMode(){
        return fireMode;
    }

    // スコアの表示
    public void updateScore(int score){
        TextView scoreText = (TextView) findViewById(R.id.scoretext);
        scoreText.setText("SCORE: "+score);
    }

    // ボスの体力の表示
    public void updateHealth(int health){
        //TextView scoreText = (TextView) findViewById(R.id.healthtext);
        ProgressBar bar = (ProgressBar)findViewById(R.id.hpBar);
        bar.setMax(1000);
        bar.setProgress(health);
        //scoreText.setText("HEALTH: "+health);
    }

    // タイムの表示
    public void updateTime(int time){
        TextView timeText = (TextView) findViewById(R.id.timetext);
        timeText.setText("TIME: "+time);
    }

    // プレイヤーが被弾したらリザルトへ
    public void toResult(int score, int clearType){
        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
        intent.putExtra("SCORE",score);
        startActivity(intent);
    }

}