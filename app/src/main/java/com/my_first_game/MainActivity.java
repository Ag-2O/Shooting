package com.my_first_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.graphics.PixelFormat;
import android.provider.ContactsContract;
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
import android.widget.TableLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

/*
    メイン画面
 */

public class MainActivity extends AppCompatActivity {
    public int gvWidth, gvHeight;       // 画面幅と高さ
    public int fireMode = 0;            // 射撃モード(0-3)
    public boolean isSetting = false;   // 設定画面
    public GameView gv;                 // surfaces
    public boolean isSpecialAvailable = false;   // 必殺が撃てるかどうか
    public boolean isSpecial = false;   // スペシャルボタンを押したかどうか

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
            imageButton.setImageResource(R.drawable.buttons_pink);
        }else if (fireMode == 1){
            fireMode = 2;
            imageButton.setImageResource(R.drawable.buttons_yellow);
        }else{
            fireMode = 0;
            imageButton.setImageResource(R.drawable.buttons_green);
        }
    }

    // スコアの表示
    public void updateScore(int score){
        TextView scoreText = (TextView) findViewById(R.id.scoretext);
        scoreText.setText("SCORE: "+score);
    }

    // ボスの体力の表示
    public void displayHealth(int health){
        ProgressBar bar = (ProgressBar) findViewById(R.id.hpBar);
        bar.setVisibility(View.VISIBLE);
        bar.setMax(10000);
        bar.setProgress(10000 - health);
    }

    // ボスの体力の更新
    public void updateHealth(int health){
        ProgressBar bar = (ProgressBar)findViewById(R.id.hpBar);
        bar.setMax(10000);
        bar.setProgress(10000 - health);
    }

    // タイムの表示
    public void updateTime(int time){
        TextView timeText = (TextView) findViewById(R.id.timetext);
        timeText.setText("TIME: "+time);
        if(time % 500 == 0){
            ImageButton imageButton = (ImageButton) findViewById(R.id.specialbutton);
            imageButton.setImageResource(R.drawable.buttons_special);
            isSpecialAvailable = true;
        }
    }

    // 獲得したアイテムの表示
    public void updateItemCounts(int itemCount){
        TextView itemText = (TextView) findViewById(R.id.itemtext);
        itemText.setText("Item: "+itemCount);
    }

    // 攻撃力の更新
    public void updateAttack(int attack){
        TextView attackText = (TextView) findViewById(R.id.atktext);
        attackText.setText("ATK: "+attack);
    }

    // プレイヤーが被弾したらリザルトへ
    public void toResult(int score, int clearType){
        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
        intent.putExtra("SCORE",score);
        intent.putExtra("CLEAR_TYPE",clearType);
        startActivity(intent);
    }

    // 設定ボタンのクリック
    public void touchSetting(View view){
        TableLayout tableLayout = (TableLayout) findViewById(R.id.explanation);
        ImageButton imageButton = (ImageButton) findViewById(R.id.settingimagebutton);
        TextView textView = (TextView) findViewById(R.id.pose);
        if(!isSetting){
            // pose
            isSetting = true;
            textView.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.VISIBLE);

        }else{
            // start
            isSetting = false;
            textView.setVisibility(View.GONE);
            tableLayout.setVisibility(View.GONE);
        }
    }

    // 必殺ボタンのクリック
    public void touchSpecialButton(View view){
        if(isSpecialAvailable){
            isSpecial = true;
            ImageButton imageButton = (ImageButton) findViewById(R.id.specialbutton);
            imageButton.setImageResource(R.drawable.buttons_specialno);
            isSpecialAvailable = false;
        }
    }

    // 射撃モードを返す
    public int getFireMode(){
        return fireMode;
    }

    // poseかどうかを返す
    public boolean getIsPose(){
        return isSetting;
    }

    // 必殺かどうかを返す
    public boolean getIsSpecial(){
        boolean tmp = isSpecial;
        if(isSpecial) isSpecial = false;
        return tmp;
    }
}