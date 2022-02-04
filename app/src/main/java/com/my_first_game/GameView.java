package com.my_first_game;

/*
    カスタムサーフェイスビュー(ゲーム)
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Handler;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.annotation.ColorRes;

import com.google.android.material.slider.RangeSlider;

public class GameView extends SurfaceView implements SurfaceHolder.Callback,Runnable{
    private SurfaceHolder holder;
    private Thread thread;

    // どのアクティビティを使用しているか
    private MainActivity ma;
    private Utils us;

    // 画面サイズ
    private float displayWidth = 1080;
    private float displayHeight = 2148;

    // 画像
    private Bitmap playerBit;           // 0:プレイヤー
    private Bitmap bulletBit;           // 1:プレイヤーの弾
    private Bitmap pinkBulletBit;       // 1:プレイヤーの桃弾
    private Bitmap yellowBulletBit;     // 1:プレイヤーの黄弾
    private Bitmap enemyBit;            // 2:敵
    private Bitmap greenEnemyBit;       // 2:緑敵
    private Bitmap yellowEnemyBit;      // 2:黄敵
    private Bitmap pinkEnemyBit;        // 2:桃敵
    private Bitmap itemBit;             // 4:アイテム
    private Bitmap enemyBulletBit;      // 5:敵の弾
    private Bitmap trackingBulletBit;   // 5:敵の弾
    private Bitmap bossBit;             // 6:ボス
    private Bitmap specialBulletBit;    // 7:ULT

    // 効果音
    private SoundPool soundPool;
    private int playerFireSound;
    private int enemyFireSound;
    private int explosionSound;
    private int powerUpSound;
    private int getItemSound;
    private int enterSound;

    // 爆発
    private Bitmap[] explosionBits = new Bitmap[4];
    private boolean isExplosion = false;

    // 弾が連続して重ならないようにするフラグ
    private boolean bulletFlag;
    private int bulletTime;
    private int bossBulletTime = 5;

    // アイテムのカウント
    public int itemCount = 1;

    // タップしているかどうか
    private boolean isTap;

    private int gameCount;              // ゲームの時間
    public int gameScore = 0;           // ゲームスコア
    public int popTime = 200;           // 敵が湧く間隔
    public int popCount = 7;            // 敵が湧く量
    public boolean isGameOver = false;  // ゲームオーバーかどうか
    public boolean isGameClear = false; // ゲームクリアかどうか
    public int bossTime = 1600;         // ボスが出現する時間
    public boolean isLiveBoss = false;  // ボスが生存しているかどうか
    private int playerATK = 1;          // 攻撃力
    private int level;                  // ゲームレベル
    private int bossHP;                 // ボスの体力
    private int bossMAXHP;              // ボスの最大体力

    // オブジェクトを格納するリスト
    private ArrayList<Object> object = new ArrayList();

    // 乱数
    Random random = new Random();

    // コンストラクタ
    public GameView(Context context, SurfaceView sv){
        super(context);
        ma = (MainActivity) context;
        us = new Utils();
        init(context);
    }

    // コンストラクタ (xmlでビューを設置している場合はこっち)
    public GameView(Context context,AttributeSet attr){
        super(context, attr);
        ma = (MainActivity) context;
        us = new Utils();
        init(context);
    }

    // 初期化
    public void init(Context context){
        // surface viewを使うために
        holder = getHolder();
        holder.addCallback(this);
        thread = new Thread(this);
        displayWidth = ma.gvWidth;
        displayHeight = ma.gvHeight;
        holder.setFixedSize((int)displayWidth, (int)displayHeight);

        // 画像読み込み
        Resources resources = context.getResources();
        playerBit = BitmapFactory.decodeResource(resources, R.drawable.player);
        bulletBit = BitmapFactory.decodeResource(resources, R.drawable.bullets_green);
        pinkBulletBit = BitmapFactory.decodeResource(resources, R.drawable.bullets_pink);
        yellowBulletBit = BitmapFactory.decodeResource(resources, R.drawable.bullets_yellow);
        specialBulletBit = BitmapFactory.decodeResource(resources, R.drawable.bullets_green);

        enemyBit = BitmapFactory.decodeResource(resources, R.drawable.enemies_default);
        greenEnemyBit = BitmapFactory.decodeResource(resources, R.drawable.enemies_green);
        pinkEnemyBit = BitmapFactory.decodeResource(resources, R.drawable.enemies_pink);
        yellowEnemyBit = BitmapFactory.decodeResource(resources, R.drawable.enemies_yellow);

        itemBit = BitmapFactory.decodeResource(resources, R.drawable.items_energy);
        enemyBulletBit = BitmapFactory.decodeResource(resources, R.drawable.bullets_enemy);
        trackingBulletBit = BitmapFactory.decodeResource(resources, R.drawable.bullets_default);
        bossBit = BitmapFactory.decodeResource(resources, R.drawable.boss);

        //TODO: 爆発のアニメーションを作れ
        for(int i = 0; i < 4; i++){
            if(i == 0) explosionBits[i] = BitmapFactory.decodeResource(resources, R.drawable.explosion);
            if(i == 1) explosionBits[i] = BitmapFactory.decodeResource(resources, R.drawable.explosion);
            if(i == 2) explosionBits[i] = BitmapFactory.decodeResource(resources, R.drawable.explosion);
            if(i == 3) explosionBits[i] = BitmapFactory.decodeResource(resources, R.drawable.explosion);
        }

        // 音声読み込み
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(4)
                .build();

        playerFireSound = soundPool.load(context, R.raw.shotplayer, 1);
        enemyFireSound = soundPool.load(context, R.raw.shotenemy, 1);
        explosionSound = soundPool.load(context, R.raw.explosion, 1);
        powerUpSound = soundPool.load(context, R.raw.powerup, 1);
        getItemSound = soundPool.load(context, R.raw.getitem, 1);
        enterSound = soundPool.load(context, R.raw.enter, 1);

        // プレイヤーオブジェクトの生成
        object.add(new Player(displayWidth,displayHeight));
        object.get(0).objectInit(playerBit,displayWidth / 2,displayHeight / 2,0,0,
                playerBit.getWidth(),playerBit.getHeight(),0);

        // 弾の制御
        bulletFlag = true;
        bulletTime = 8;

        // タップ状態
        isTap = false;

        // ゲーム時間の初期化
        gameCount = 0;
    }

    // SurfaceViewが作られる時に実行される
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d("MainLoop", "surfaceCreated");
        thread.start();     // スレッドスタート
    }

    // SurfaceViewが更新される時に実行される
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d("MainLoop", "surfaceChanged");
        arg0.setFixedSize(arg2,arg3);
    }

    // SurfaceViewが破壊される時に実行される
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.d("MainLoop", "surfaceDestroyed");
        boolean retry = true;
        while(retry){
            try{
                thread.join();
                retry = false;
            } catch (InterruptedException e){}
        }
    }

    // メインループ
    public void run(){
        Canvas canvas;
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // 描画
        while(!isGameOver && !isGameClear){
            //Log.d("MainLoop","in while");
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.BLACK);                      // 背景黒塗り

            // プレイヤーの処理
            object.get(0).bulletStatus = ma.getFireMode();      // 射撃モードの更新
            object.get(0).isSpecial = ma.getIsSpecial();
            if(object.get(0).isSpecial) fireSpecialBullet();    // 必殺
            if(isTap) fireBullet();                             // タップ中なら射撃

            // オブジェクトの処理
            for(int i = 0; i < object.size(); i++){
                // 雑魚敵の攻撃処理
                if(i != 0 && object.get(i).isFireBullet){
                    object.get(i).isFireBullet = false;
                    enemyFireBullet(i);                         // 敵の射撃
                }

                // ボスの攻撃処理
                if(gameCount > bossTime){
                    if(i != 0 && object.get(i).objectType == 6 && bossBulletTime < 1) {
                        //TODO: Healthによって特殊技が合ってもいいかも
                        ma.updateHealth(object.get(i).health, bossMAXHP);  // 体力の描画
                        bossSpecialFire(i,object.get(i).health);  // 体力に応じた攻撃
                        bossBulletTime = 40;
                    }
                }

                object.get(i).objectDraw(canvas);               // 描画
                object.get(i).objectMove();                     // 移動
                judgeHit(object,i);                             // 当たり判定
                drawExplosion(i);                               // 爆発の描画
                drawItem(i);                                    // アイテムの描画
                if(object.get(i).isDead()) object.remove(i);    // 範囲外なら弾を消去

            }

            // メインスレッドでの処理
            ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ma.updateScore(gameScore);                  // スコア更新
                    ma.updateTime(gameCount);                   // ゲーム時間更新
                    ma.updateItemCounts(itemCount);             // アイテムカウントの更新
                    ma.updateAttack(playerATK);                 // 攻撃力の更新
                }
            });

            //Log.d("MainLoop","popCount: "+ popCount);
            Log.d("MainLoop","gameLevel: "+ level);
            if(isLiveBoss) bossBulletTime --;                           // ボスの攻撃間隔
            if(!isLiveBoss) if (bossTime == 0) popBoss();               // 1600カウント毎にボスを湧かせる
            if(!isLiveBoss){
                if(gameCount % popTime == 0) popCount = 5 + level / 2;  // popTime毎に湧き数をリセット
                if(popCount > 0 && gameCount % 5 == 0) popEnemies();    // 敵を湧かせる
                bossTime --;
            }

            controlFire();                                      // 弾を連続で撃てないように
            ++ gameCount;                                       // ゲーム時間を進める
            level = (gameCount / 500) + 1;                      // ゲームのレベルアップ
            holder.unlockCanvasAndPost(canvas);
            if(isGameOver || isGameClear) break;                // ゲームオーバーならループを抜ける

            // ポーズ
            if(ma.getIsPose()){
                while(true){
                    if(!ma.getIsPose()){
                        break;
                    }
                }
            }

            try {
                Thread.sleep(1000 / 60);
            } catch (Exception e){}
        }

        ma.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isGameClear){
                    ma.toResult(gameScore,1);       // ゲームクリア
                }else {
                    ma.toResult(gameScore,0);       // ゲームオーバー
                }
            }
        });
    }

    // タップした時の処理
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        int eventX = (int) event.getX();
        int eventY = (int) event.getY();
        //Log.d("MainLoop","Touch Position: x["+eventX+"], y["+eventY+" ");

        // 各処理
        switch(action){
            case MotionEvent.ACTION_DOWN:
                // 自機付近をタップした時に弾を生成
                //Log.d("MainLoop","in bullet generate");
                isTap = true;           // タップ
                bulletFlag = false;
                break;

            case MotionEvent.ACTION_UP:
                isTap = false;
                break;

            case MotionEvent.ACTION_MOVE:
                // 自機の移動
                //Log.d("MainLoop","in player move");
                if(us.isRectOverlap(eventX,eventY, object.get(0).objectGetTapRect())){
                    object.get(0).objectMove(eventX,eventY);
                }
                break;
        }
        return true;
    }

    // 敵のポップ
    public void popEnemies(){
        // カウントが進むにつれ難易度が増す
        int rand = random.nextInt((level % 4) + 1);

        if(rand == 1){
            // green
            object.add(new Enemy(displayWidth, displayHeight));
            object.get(object.size() - 1)
                    .objectInit(greenEnemyBit, displayWidth / 2, -60, 20, 20,
                            greenEnemyBit.getWidth(), greenEnemyBit.getHeight(), 1, 0,
                            15*level);
        }else if(rand == 2){
            // pink
            object.add(new Enemy(displayWidth, displayHeight));
            object.get(object.size() - 1)
                    .objectInit(pinkEnemyBit, displayWidth / 2, -60, 20, 20,
                            pinkEnemyBit.getWidth(), pinkEnemyBit.getHeight(), 2, 0,
                            15*level);
        }else if(rand == 3){
            // yellow
            object.add(new Enemy(displayWidth, displayHeight));
            object.get(object.size() - 1)
                    .objectInit(yellowEnemyBit, displayWidth / 2, -60, 20, 20,
                            yellowEnemyBit.getWidth(), yellowEnemyBit.getHeight(), 3, 0,
                            25*level);
        }else{
            // default
            object.add(new Enemy(displayWidth, displayHeight));
            object.get(object.size() - 1)
                    .objectInit(enemyBit, displayWidth / 2, -60, 20, 20,
                            enemyBit.getWidth(), enemyBit.getHeight(), 0, 0,
                            15*level);
        }

        popCount --;
    }

    // ボスのポップ 1600秒くらい
    public void popBoss(){
        bossMAXHP = 1000 * level;
        isLiveBoss = true;
        object.add(new Boss(displayWidth,displayHeight));
        object.get(object.size() - 1)
                .objectInit(bossBit, displayWidth / 2, -60,40,40,
                        bossBit.getWidth(), bossBit.getHeight(), bossMAXHP);

        // メインスレッド処理
        ma.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ma.displayHealth(bossMAXHP);
            }
        });
    }

    // 射撃間隔の制御
    public void controlFire(){
        if(!bulletFlag){
            -- bulletTime;
            if(bulletTime < 0){
                bulletTime = 8;
                bulletFlag = true;
            }
        }
    }

    // 敵に弾が当たったら爆発オブジェクト生成
    public void drawExplosion(int i){
        if(object.get(i).isExplosion) {
            object.add(new Explosion(displayWidth, displayHeight));
            object.get(object.size() - 1).
                    objectInit(explosionBits, object.get(i).centerX,
                            object.get(i).centerY, 0, 0,
                            explosionBits[0].getWidth(),
                            explosionBits[0].getHeight(), 0);
            soundPool.play(explosionSound, 1.0f, 1.0f, 0,0,1);
        }
    }

    // 爆発アニメーション後、アイテムオブジェクトを生成
    public void drawItem(int i){
        if(i != 0 && object.get(i).objectType == 3){
            if(object.get(i).dead){
                object.add(new Item(displayWidth, displayHeight));
                object.get(object.size() - 1).objectInit(itemBit, object.get(i).centerX,
                                                         object.get(i).centerY, 0, 0,
                                                         itemBit.getWidth(),
                                                         itemBit.getHeight(), 0);
            }
        }
    }

    // 弾を撃つ
    public void fireBullet(){
        // 緑
        if(object.get(0).bulletStatus == 0){
            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(bulletBit, object.get(0).centerX - 40,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 75, bulletBit.getWidth(),
                    bulletBit.getHeight(), playerATK);
            object.get(object.size() - 1).bulletStatus = 0;

            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(bulletBit, object.get(0).centerX + 40,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 75, bulletBit.getWidth(),
                    bulletBit.getHeight(), playerATK);
            object.get(object.size() - 1).bulletStatus = 0;
        }

        // 赤
        if(object.get(0).bulletStatus == 1){
            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(pinkBulletBit, object.get(0).centerX - 40,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 75, pinkBulletBit.getWidth(),
                    pinkBulletBit.getHeight(), playerATK);
            object.get(object.size() - 1).bulletStatus = 1;

            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(pinkBulletBit, object.get(0).centerX + 40,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 75, pinkBulletBit.getWidth(),
                    pinkBulletBit.getHeight(), playerATK);
            object.get(object.size() - 1).bulletStatus = 1;
        }

        // 黄色
        if(object.get(0).bulletStatus == 2){
            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(yellowBulletBit, object.get(0).centerX - 40,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 75, yellowBulletBit.getWidth(),
                    yellowBulletBit.getHeight(), playerATK);
            object.get(object.size() - 1).bulletStatus = 2;

            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(yellowBulletBit, object.get(0).centerX + 40,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 75, yellowBulletBit.getWidth(),
                    yellowBulletBit.getHeight(), playerATK);
            object.get(object.size() - 1).bulletStatus = 2;
        }
        soundPool.play(playerFireSound, 1.0f, 1.0f, 0,0,1);
    }

    // 必殺
    public void fireSpecialBullet(){
        // 普通のとは別に撃つ
        if(object.get(0).isSpecial){
            object.add(new PlayerBullet(displayWidth, displayHeight,3.0));
            object.get(object.size() - 1).objectInit(specialBulletBit, object.get(0).centerX,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 15, specialBulletBit.getWidth(),
                    specialBulletBit.getHeight(), playerATK * 200);
            object.get(object.size() - 1).bulletStatus = 4;
            object.get(0).isSpecial = false;
        }
        soundPool.play(playerFireSound, 1.0f, 1.0f, 0,0,1);
    }

    // 敵が弾を撃つ
    public void enemyFireBullet(int i){
        // 通常
        if(object.get(i).bulletStatus == 0){
            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(enemyBulletBit, object.get(i).centerX,
                    object.get(i).centerY + enemyBit.getHeight() / 2,
                    0, -20, enemyBulletBit.getWidth(),
                    enemyBulletBit.getHeight(), 0);
            soundPool.play(enemyFireSound, 1.0f, 1.0f, 0,0,1);
        }
        // 追尾する弾
        else if(object.get(i).bulletStatus == 1){
            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX,
                    object.get(i).centerY + trackingBulletBit.getHeight() / 2,
                    20, 20, trackingBulletBit.getWidth(),
                    trackingBulletBit.getHeight(), 0, object.get(0).centerX,
                    object.get(0).centerY);
            soundPool.play(enemyFireSound, 1.0f, 1.0f, 0,0,1);
        }
        // ばらまき
        else if(object.get(i).bulletStatus == 2){
            for(int j = 0; j < 6; j++){
                object.add(new EnemyBullet(displayWidth, displayHeight));
                object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX,
                        object.get(i).centerY, 20, 20, trackingBulletBit.getWidth(),
                        trackingBulletBit.getHeight(), j*(360/6));

            }
            soundPool.play(enemyFireSound, 1.0f, 1.0f, 0,0,1);
        }
        // 高速追尾2連
        else{
            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX,
                    object.get(i).centerY + trackingBulletBit.getHeight() / 2,
                    30, 30, trackingBulletBit.getWidth(),
                    trackingBulletBit.getHeight(), 0, object.get(0).centerX,
                    object.get(0).centerY);
            soundPool.play(enemyFireSound, 1.0f, 1.0f, 0,0,1);
        }
    }

    // ボスが弾を撃つ
    public void bossFireBullet(int i, int level){
        // 通常
        object.add(new EnemyBullet(displayWidth, displayHeight));
        object.get(object.size() - 1).objectInit(enemyBulletBit, object.get(i).centerX - 150,
                object.get(i).centerY + enemyBit.getHeight() / 2,
                0, -30, enemyBulletBit.getWidth(),
                enemyBulletBit.getHeight(), 0);

        object.add(new EnemyBullet(displayWidth, displayHeight));
        object.get(object.size() - 1).objectInit(enemyBulletBit, object.get(i).centerX + 150,
                object.get(i).centerY + enemyBit.getHeight() / 2,
                0, -30, enemyBulletBit.getWidth(),
                enemyBulletBit.getHeight(), 0);

        if(level > 2) {
            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(enemyBulletBit, object.get(i).centerX,
                    object.get(i).centerY + enemyBit.getHeight() / 2,
                    0, -30, enemyBulletBit.getWidth(),
                    enemyBulletBit.getHeight(), 0);
        }

        soundPool.play(enemyFireSound, 1.0f, 1.0f, 0,0,1);

        // 確率でばらまきか追尾
        if(level > 0){
            for(int j = 0; j < 20; j++){
                object.add(new EnemyBullet(displayWidth, displayHeight));
                object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX,
                        object.get(i).centerY, 20, 20, trackingBulletBit.getWidth(),
                        trackingBulletBit.getHeight(), j*(360/20));
            }
        }

        soundPool.play(enemyFireSound, 1.0f, 1.0f, 0,0,1);

        // 追尾する弾
        if(level > 1){
            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX - 150,
                    object.get(i).centerY + trackingBulletBit.getHeight() / 2,
                    20, 20, trackingBulletBit.getWidth(),
                    trackingBulletBit.getHeight(), 0, object.get(0).centerX,
                    object.get(0).centerY);

            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX,
                    object.get(i).centerY + trackingBulletBit.getHeight() / 2,
                    20, 20, trackingBulletBit.getWidth(),
                    trackingBulletBit.getHeight(), 0, object.get(0).centerX,
                    object.get(0).centerY);

            if(level > 3) {
                object.add(new EnemyBullet(displayWidth, displayHeight));
                object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX + 150,
                        object.get(i).centerY + trackingBulletBit.getHeight() / 2,
                        20, 20, trackingBulletBit.getWidth(),
                        trackingBulletBit.getHeight(), 0, object.get(0).centerX,
                        object.get(0).centerY);
            }
        }
        soundPool.play(enemyFireSound, 1.0f, 1.0f, 0,0,1);
    }

    public void bossSpecialFire(int i, int health){
        //TODO: 体力に応じた特殊攻撃の実装

        // 体力が減ると攻撃速度が上がる
        if(health < 1000) {
            bossFireBullet(i,4);
        } else if(health < bossMAXHP*0.3) {
            bossFireBullet(i,3);
        } else if(health < bossMAXHP*0.5){
            bossFireBullet(i,2);
        } else if(health < bossMAXHP*0.7){
            bossFireBullet(i,1);
        } else{
            bossFireBullet(i,0);
        }

    }

    //　被弾の処理
    public void judgeHit(ArrayList<Object> object, int i){
        //Log.d("judgeHit","executed");
        for(int j = 0; j < object.size() - 1;j++){
            // 敵の被弾
            if(i != j && object.get(i).objectType == 1 && object.get(j).objectType == 2){
                // 被弾したらオブジェクトを削除
                if(isHitRange(object.get(i).hitRange, object.get(j).hitRange)){
                    // 弾の削除
                    if(object.get(i).bulletStatus != 4) object.get(i).dead = true;

                    // healthが無くなったら撃破
                    if(object.get(j).health < 0) {
                        gameScore += 100;
                        object.get(j).isExplosion = true;
                        object.get(j).dead = true;
                    }else{
                        gameScore += 1;
                        // 属性別の処理
                        if(object.get(i).bulletStatus + 1 == object.get(j).bulletStatus){
                            object.get(j).health -= object.get(i).attack * 2;
                        }else{
                            object.get(j).health -= object.get(i).attack;
                        }
                    }
                }
            }

            // ボスの被弾
            if(i != j && object.get(i).objectType == 1 && object.get(j).objectType == 6) {
                // 被弾したらオブジェクトを削除
                if (isHitRange(object.get(i).hitRange, object.get(j).hitRange)) {
                    // 弾の削除
                    object.get(i).dead = true;

                    // 一定回数被弾したら撃破
                    if(object.get(j).health < 0) {
                        gameScore += 4000;
                        object.get(j).isExplosion = true;
                        object.get(j).dead = true;
                        isLiveBoss = false;
                        bossTime = 1600;
                    }else{
                        gameScore += 10;
                        object.get(j).health -= object.get(i).attack;
                        bossBulletTime --;  // 攻撃するほど敵の弾が増えるように
                    }
                }
            }

            // プレイヤーとアイテムの衝突
            if(i != j && object.get(i).objectType == 0 && object.get(j).objectType == 4){
                // 衝突したらアイテムを回収する
                if(isHitRange(object.get(i).hitRange,object.get(j).hitRange)){
                    gameScore += 50;
                    object.get(j).dead = true;
                    itemCount += 1;
                    soundPool.play(getItemSound, 1.0f, 1.0f, 0,0,1);

                    // アイテムによる強化
                    if(itemCount != 0 && itemCount % 20 == 0){
                        playerATK += 1;
                        soundPool.play(powerUpSound, 1.0f, 1.0f, 0,0,1);
                    }
                }
            }

            // プレイヤーの被弾
            if(i != j && object.get(i).objectType == 0 && object.get(j).objectType == 5){
                if(isHitRange(object.get(i).hitRange,object.get(j).hitRange)){
                    //Log.d("judgeHit","game over!!");
                    object.get(j).dead = true;
                    object.get(j).isExplosion = true;
                    isGameOver = true;
                }
            }
        }
    }

    // 当たり判定
    public boolean isHitRange(Rect rect1, Rect rect2){
        return rect1.left < rect2.right && rect2.left < rect1.right &&
                rect1.top < rect2.bottom && rect2.top < rect1.bottom;
    }
}

