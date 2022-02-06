package com.my_first_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
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
    public GameView gv;                 // surfaces

    public boolean isSetting = false;           // 設定画面
    public boolean isSpecialAvailable = false;  // 必殺が撃てるかどうか
    public boolean isSpecial = false;           // スペシャルボタンを押したかどうか
    public boolean isSlashingAvailable = false; // 斬撃が撃てるかどうか
    public boolean isSlashing = false;          // 斬撃ボタンを押したかどうか


    private SoundPool soundPool;        // 音声設定
    private int enterSound;             // 決定ボタン/メニューボタンの効果音
    private int modeChangeSound;        // モード変更ボタンの効果音

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setDisplaySize();               // 画面サイズ設定
        setSounds();                    // 音声設定

        setContentView(R.layout.activity_main);
    }


    /*
    ##########################################################################
                                  画面 & 音声
    ##########################################################################
     */

    public void setDisplaySize(){
        // 画面サイズ取得
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        Window window = getWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager manager = window.getWindowManager();
        Display display = manager.getDefaultDisplay();
        gvWidth = display.getWidth();   // 画面横
        gvHeight = display.getHeight(); // 画面高さ
    }

    public void setSounds(){
        // 音声設定
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();

        // 音声読み込み
        enterSound = soundPool.load(this, R.raw.enter, 1);
        modeChangeSound = soundPool.load(this, R.raw.modechange, 1);
    }


    /*
    ##########################################################################
                                  ボタン処理
    ##########################################################################
     */

    // 射撃モード変更
    public void changeFireMode(View view){
        ImageButton imageButton = (ImageButton) findViewById(R.id.imagebutton);
        soundPool.play(modeChangeSound, 1.0f, 1.0f, 1, 0, 1);
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

    // 必殺ボタン
    public void touchSpecialButton(View view){
        soundPool.play(modeChangeSound, 1.0f, 1.0f, 1, 0, 1);
        if(isSpecialAvailable){
            isSpecial = true;
            ImageButton imageButton = (ImageButton) findViewById(R.id.specialbutton);
            imageButton.setImageResource(R.drawable.buttons_specialno);
            isSpecialAvailable = false;
        }
    }

    // 斬撃ボタン
    public void touchSlashingButton(View view){
        if(isSlashingAvailable){
            isSlashing = true;
            ImageButton imageButton = (ImageButton) findViewById(R.id.slashingbutton);
            imageButton.setImageResource(R.drawable.buttons_slashingno);
            isSlashingAvailable = false;
        }

    }

    // 設定ボタン
    public void touchSetting(View view){
        soundPool.play(enterSound, 1.0f, 1.0f, 1, 0, 1);
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


    /*
    ##########################################################################
                                  テキスト表示
    ##########################################################################
     */

    // スコアの表示
    public void updateScore(int score){
        TextView scoreText = (TextView) findViewById(R.id.scoretext);
        scoreText.setText(" SCORE: "+score);
    }

    // ボスの体力の表示
    public void displayHealth(int MAXHP){
        ProgressBar bar = (ProgressBar) findViewById(R.id.hpBar);
        bar.setVisibility(View.VISIBLE);
        bar.setMax(MAXHP);
    }

    // ボスの体力の更新
    public void updateHealth(int HP, int MAXHP){
        ProgressBar bar = (ProgressBar)findViewById(R.id.hpBar);
        bar.setMax(MAXHP);
        bar.setProgress(MAXHP - HP);
    }

    // ボスの体力の非表示
    public void hideHealth(){
        ProgressBar bar = (ProgressBar) findViewById(R.id.hpBar);
        bar.setMax(1000);
        bar.setVisibility(View.GONE);
    }

    // タイムの表示
    public void updateTime(int time){
        TextView timeText = (TextView) findViewById(R.id.timetext);
        timeText.setText(" TIME: "+time);

        // 時間で各技が使えるように
        if(time % 500 == 0){
            ImageButton imageButton = (ImageButton) findViewById(R.id.specialbutton);
            imageButton.setImageResource(R.drawable.buttons_special);
            isSpecialAvailable = true;
        }
        if(time % 100 == 0){
            ImageButton imageButton = (ImageButton) findViewById(R.id.slashingbutton);
            imageButton.setImageResource(R.drawable.buttons_slashing);
            isSlashingAvailable = true;
        }
    }

    // 獲得したアイテムの表示
    public void updateItemCounts(int itemCount){
        TextView itemText = (TextView) findViewById(R.id.itemtext);
        itemText.setText(" Item: "+itemCount);
    }

    // 攻撃力の表示
    public void updateAttack(int attack){
        TextView attackText = (TextView) findViewById(R.id.atktext);
        attackText.setText(" ATK: "+attack);
    }

    // ゲームレベルの表示
    public void updateLevel(int level){
        TextView attackText = (TextView) findViewById(R.id.leveltext);
        attackText.setText(" Level: "+level);
    }

    //TODO: チュートリアル表示
    public void displayTutorial(){

    }

    /*
    ##########################################################################
                                    get関数
    ##########################################################################
     */

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
        boolean tmpIsSpecial = isSpecial;
        if(isSpecial) isSpecial = false;
        return tmpIsSpecial;
    }

    // 斬撃かどうかを返す
    public boolean getIsSlashing(){
        boolean tmpIsSlashing = isSlashing;
        if(isSlashing) isSlashing = false;
        return tmpIsSlashing;
    }


    /*
    ##########################################################################
                                    ゲーム終了時
    ##########################################################################
     */

    // プレイヤーが被弾したらリザルトへ
    public void toResult(int score, int clearType){
        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
        intent.putExtra("SCORE",score);
        intent.putExtra("CLEAR_TYPE",clearType);
        startActivity(intent);
    }
}