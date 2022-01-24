package com.my_first_game;

/*
    カスタムサーフェイスビュー(ゲーム)
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
    private Bitmap enemyBit;            // 2:敵
    private Bitmap itemBit;             // 4:アイテム
    private Bitmap enemyBulletBit;      // 5:敵の弾
    private Bitmap trackingBulletBit;   // 5:敵の弾
    private Bitmap bossBit;             // 6:ボス

    // 爆発
    private Bitmap[] explosionBits = new Bitmap[4];
    private boolean isExplosion = false;

    // 弾が連続して重ならないようにするフラグ
    private boolean bulletFlag;
    private int bulletTime;
    private int bossBulletTime = 20;

    // ボスのリミッター
    private int bossLimiter = 0;

    // アイテムのカウント
    public int itemCount = 1;

    // タップしているかどうか
    private boolean isTap;

    // 敵
    private static final int enemyNum = 5;
    private int[] enemyPopCount = new int[enemyNum];
    private EnemyPop[] enemyPop = new EnemyPop[enemyNum];

    // 敵を上部で停滞させるための動きリスト
    private ArrayList<Integer> enemyPopR = new ArrayList();
    private ArrayList<Integer> enemyPopC = new ArrayList();
    private ArrayList<Integer> enemyPopS = new ArrayList();

    private int enemyPopNum;            // 湧いた敵の数
    private int gameCount;              // ゲームの時間
    public int gameScore = 0;           // ゲームスコア
    public boolean isGameOver = false;  // ゲームオーバーかどうか
    public boolean isGameClear = false; // ゲームクリアかどうか

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
        //displayWidth = 1080;
        //displayHeight = 1916;
        //Log.d("MainLoop", "DisplaySize: "+displayWidth+" x "+displayHeight+" ");
        holder.setFixedSize((int)displayWidth, (int)displayHeight);

        // 画像読み込み
        Resources resources = context.getResources();
        playerBit = BitmapFactory.decodeResource(resources, R.drawable.player2);
        bulletBit = BitmapFactory.decodeResource(resources, R.drawable.bullet2);
        enemyBit = BitmapFactory.decodeResource(resources, R.drawable.enemy);
        itemBit = BitmapFactory.decodeResource(resources, R.drawable.item);
        enemyBulletBit = BitmapFactory.decodeResource(resources, R.drawable.enemybullet);
        trackingBulletBit = BitmapFactory.decodeResource(resources, R.drawable.bullet);
        bossBit = BitmapFactory.decodeResource(resources, R.drawable.boss);

        //TODO: 爆発のアニメーションを作れ
        for(int i = 0; i < 4; i++){
            if(i == 0) explosionBits[i] = BitmapFactory.decodeResource(resources, R.drawable.explosion);
            if(i == 1) explosionBits[i] = BitmapFactory.decodeResource(resources, R.drawable.explosion);
            if(i == 2) explosionBits[i] = BitmapFactory.decodeResource(resources, R.drawable.explosion);
            if(i == 3) explosionBits[i] = BitmapFactory.decodeResource(resources, R.drawable.explosion);
        }

        //TODO:敵の画像を増やしてもいいかもしれない
        //TODO:爆発や弾の種類も増やしてもいいかも

        // プレイヤーオブジェクトの生成
        object.add(new Player(displayWidth,displayHeight));
        object.get(0).objectInit(playerBit,displayWidth / 2,displayHeight / 2,0,0,
                playerBit.getWidth(),playerBit.getHeight(),0);

        // 弾の制御
        bulletFlag = true;
        bulletTime = 8;

        // タップ状態
        isTap = false;

        // 敵オブジェクトの動き
        //TODO:ダサい
        enemyPopR.add(180); enemyPopR.add(90); enemyPopR.add(270); enemyPopR.add(90); enemyPopR.add(270); enemyPopR.add(90); enemyPopR.add(180);
        enemyPopC.add(15); enemyPopC.add(50); enemyPopC.add(85); enemyPopC.add(120); enemyPopC.add(140); enemyPopC.add(170); enemyPopC.add(200);
        enemyPopS.add(40); enemyPopS.add(30); enemyPopS.add(30); enemyPopS.add(30); enemyPopS.add(30); enemyPopS.add(30); enemyPopS.add(30);
        enemyPopNum = 0;

        int c = 10;     // 湧く間隔
        int m = 0;     // まとまりが湧く間隔
        for(int i = 0; i < enemyNum; i++){
            if(i % 10 == 0) m = m + 50;
            enemyPopCount[i] = i*c + m;
            enemyPop[i] = new EnemyPop(enemyPopCount[i], enemyPopS, enemyPopR, enemyPopC);
        }


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
        while(!isGameOver){
            //Log.d("MainLoop","in while");
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.BLACK);                      // 背景黒塗り

            // プレイヤーの処理
            object.get(0).bulletStatus = ma.getFireMode();      // 射撃モードの更新
            if(itemCount % 100 == 0) object.get(0).attack ++;   // アイテムによる攻撃力強化
            if(isTap) fireBullet();                             // タップ中なら射撃

            // オブジェクトの処理
            int count = 0;
            for(int i = 0; i < object.size(); i++){

                // 雑魚敵の攻撃処理
                if(i != 0 && object.get(i).isFireBullet && random.nextInt(10) < 1){
                    //object.get(i).isFireBullet = false;
                    enemyFireBullet(i);                         // 敵の射撃
                }

                // ボスの攻撃処理
                if(gameCount > 200){
                    if(i != 0 && object.get(i).objectType == 6 && bossBulletTime < 1) {
                        //TODO: levelで攻撃を制御
                        ma.updateHealth(object.get(i).health);

                        // 体力が減ると攻撃速度が上がる
                        if(object.get(i).health < 100) {
                            bossFireBullet(i,4);
                        } else if(object.get(i).health < 300) {
                            bossFireBullet(i,3);
                        } else if(object.get(i).health < 500){
                            bossFireBullet(i,2);
                        } else if(object.get(i).health < 700){
                            bossFireBullet(i,1);
                        } else{
                            bossFireBullet(i,0);
                        }

                        bossBulletTime = 30;
                        //TODO: Healthによって特殊技が合ってもいいかも
                        //TODO: POSE機能がほしいかも
                    }
                }

                object.get(i).objectDraw(canvas);               // 描画
                object.get(i).objectMove();                     // 移動
                judgeHit(object,i);                             // 当たり判定
                drawExplosion(i);                               // 爆発の描画
                drawItem(i);                                    // アイテムの描画
                if(object.get(i).objectType == 2) count++;      // 敵オブジェクトを数える
                if(object.get(i).isDead()) object.remove(i);    // 範囲外なら弾を消去
                ma.updateScore(gameScore);                      // スコア更新
                ma.updateTime(gameCount);                       // ゲーム時間更新
            }

            if(gameCount > 200) bossBulletTime --;
            if(gameCount == 200) popBoss();                    // 1600カウント後にボスを湧かせる
            popEnemies();                                       // 敵を湧かせる
            controlFire();                                      // 弾を連続で撃てないように
            ++ gameCount;                                       // ゲーム時間を進める
            holder.unlockCanvasAndPost(canvas);

            if(isGameOver || isGameClear) break;                // ゲームオーバーならループを抜ける

            //TODO: FPS調整する
            try {
                Thread.sleep(50);
            } catch (Exception e){}
        }
        //TODO: ゲームクリアとゲームオーバーを分ける
        if(isGameClear){
            ma.toResult(gameScore,1);   // ゲームクリア
        }else {
            ma.toResult(gameScore,0);     // ゲームオーバー
        }
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
        if(enemyPopNum != enemyNum){
            if(enemyPop[enemyPopNum].enemyPopCount == gameCount){
                //int diff = enemyNum - enemyPopNum;
                object.add(new Enemy(displayWidth,displayHeight));
                object.get(object.size() - 1)
                        .objectInit(enemyBit, random.nextInt(50), -60, 0,0,
                                enemyBit.getWidth(), enemyBit.getHeight(), 0, 0,
                                enemyPop[enemyPopNum].enemyMoveS, enemyPop[enemyPopNum].enemyMoveR,
                                enemyPop[enemyPopNum].enemyMoveC, 10);
                ++ enemyPopNum;
            }
        }
    }

    // ボスのポップ 1650秒くらい
    public void popBoss(){
        object.add(new Boss(displayWidth,displayHeight));
        object.get(object.size() - 1)
                .objectInit(bossBit, displayWidth / 2, -60,40,40,
                        bossBit.getWidth(), bossBit.getHeight(), 1000);
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
        // 通常
        if(object.get(0).bulletStatus == 0){
            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(bulletBit, object.get(0).centerX,
                                                     object.get(0).centerY - playerBit.getHeight(),
                                                     0, 75, bulletBit.getWidth(),
                                                     bulletBit.getHeight(), 0);
        }

        // 2連射撃
        if(object.get(0).bulletStatus == 1){
            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(bulletBit, object.get(0).centerX - 40,
                                                     object.get(0).centerY - playerBit.getHeight(),
                                                     0, 75, bulletBit.getWidth(),
                                                     bulletBit.getHeight(), 0);

            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(bulletBit, object.get(0).centerX + 40,
                                                     object.get(0).centerY - playerBit.getHeight(),
                                                     0, 75, bulletBit.getWidth(),
                                                     bulletBit.getHeight(), 0);
        }

        // 3連射撃
        if(object.get(0).bulletStatus == 2){
            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(bulletBit, object.get(0).centerX - 80,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 75, bulletBit.getWidth(),
                    bulletBit.getHeight(), 0);

            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(bulletBit, object.get(0).centerX,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 75, bulletBit.getWidth(),
                    bulletBit.getHeight(), 0);

            object.add(new PlayerBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(bulletBit, object.get(0).centerX + 80,
                    object.get(0).centerY - playerBit.getHeight(),
                    0, 75, bulletBit.getWidth(),
                    bulletBit.getHeight(), 0);
        }

        /*
        // ばらまき
        if(object.get(0).bulletStatus == 2){
            for(int i = 0; i < 10; i++){
                object.add(new PlayerBullet(displayWidth, displayHeight));
                object.get(object.size() - 1).objectInit(bulletBit, object.get(0).centerX,
                                                         object.get(0).centerY,
                                                         30, 30, bulletBit.getWidth(),
                                                         bulletBit.getHeight(), i*(360/10));
            }
        }

         */

    }

    // 敵が弾を撃つ
    public void enemyFireBullet(int i){
        // 通常
        if(random.nextInt(10) > 3){
            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(enemyBulletBit, object.get(i).centerX,
                    object.get(i).centerY + enemyBit.getHeight() / 2,
                    0, -20, enemyBulletBit.getWidth(),
                    enemyBulletBit.getHeight(), 0);
        }
        // 追尾する弾
        else{
            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX,
                    object.get(i).centerY + trackingBulletBit.getHeight() / 2,
                    20, 20, trackingBulletBit.getWidth(),
                    trackingBulletBit.getHeight(), 0, object.get(0).centerX,
                    object.get(0).centerY);
        }
    }

    // ボスが弾を撃つ
    public void bossFireBullet(int i, int level){
        //TODO: for文とかでまとめたほうが美しい

        // 通常
        object.add(new EnemyBullet(displayWidth, displayHeight));
        object.get(object.size() - 1).objectInit(enemyBulletBit, object.get(i).centerX - 150,
                object.get(i).centerY + enemyBit.getHeight() / 2,
                0, -20, enemyBulletBit.getWidth(),
                enemyBulletBit.getHeight(), 0);

        object.add(new EnemyBullet(displayWidth, displayHeight));
        object.get(object.size() - 1).objectInit(enemyBulletBit, object.get(i).centerX,
                object.get(i).centerY + enemyBit.getHeight() / 2,
                0, -20, enemyBulletBit.getWidth(),
                enemyBulletBit.getHeight(), 0);

        if(level > 2) {
            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(enemyBulletBit, object.get(i).centerX + 150,
                    object.get(i).centerY + enemyBit.getHeight() / 2,
                    0, -20, enemyBulletBit.getWidth(),
                    enemyBulletBit.getHeight(), 0);
        }

        // 確率でばらまきか追尾
        if(level > 0){
            for(int j = 0; j < 10; j++){
                object.add(new EnemyBullet(displayWidth, displayHeight));
                object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX,
                        object.get(i).centerY, 30, 30, trackingBulletBit.getWidth(),
                        trackingBulletBit.getHeight(), j*(360/10));
            }
        }

        // 追尾する弾
        if(level > 1){
            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX - 150,
                    object.get(i).centerY + trackingBulletBit.getHeight() / 2,
                    10, 10, trackingBulletBit.getWidth(),
                    trackingBulletBit.getHeight(), 0, object.get(0).centerX,
                    object.get(0).centerY);

            object.add(new EnemyBullet(displayWidth, displayHeight));
            object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX,
                    object.get(i).centerY + trackingBulletBit.getHeight() / 2,
                    10, 10, trackingBulletBit.getWidth(),
                    trackingBulletBit.getHeight(), 0, object.get(0).centerX,
                    object.get(0).centerY);

            if(level > 3) {
                object.add(new EnemyBullet(displayWidth, displayHeight));
                object.get(object.size() - 1).objectInit(trackingBulletBit, object.get(i).centerX + 150,
                        object.get(i).centerY + trackingBulletBit.getHeight() / 2,
                        10, 10, trackingBulletBit.getWidth(),
                        trackingBulletBit.getHeight(), 0, object.get(0).centerX,
                        object.get(0).centerY);
            }
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
                    //Log.d("judgeHit","enemy health: "+object.get(j).health);
                    // 弾の削除
                    object.get(i).dead = true;
                    // 一定回数被弾したら撃破
                    if(object.get(j).health < 0) {
                        gameScore += 100;
                        object.get(j).isExplosion = true;
                        object.get(j).dead = true;
                    }else{
                        gameScore += 1;
                        object.get(j).health -= object.get(0).attack;
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
                        isGameClear = true; // ゲームクリアフラグ
                    }else{
                        gameScore += 10;
                        object.get(j).health -= object.get(0).attack;
                        bossBulletTime --;  // 攻撃するほど敵の弾が増えるように
                    }


                }
            }

            // プレイヤーとアイテムの衝突
            if(i != j && object.get(i).objectType == 0 && object.get(j).objectType == 4){
                // 衝突したらアイテムを回収する
                if(isHitRange(object.get(i).hitRange,object.get(j).hitRange)){
                    //Log.d("judgeHit","item get!!");
                    gameScore += 50;
                    object.get(j).dead = true;
                    itemCount += 1;
                    //TODO:アイテムによる強化を実装しろ
                    //object.get(i).bulletStatus = (object.get(i).bulletStatus + 1) % 3;
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

