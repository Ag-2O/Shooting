package com.my_first_game;

/*
    オブジェクト
 */


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;

public abstract class Object {
    public Utils utils = new Utils();
    public float displayWidth, displayHeight;   // オブジェクトの画像
    public Drawable image;                      // オブジェクトの画像
    public float centerX, centerY;              // オブジェクトの中心
    public float destinationX, destinationY;    // 目的地
    public float speedX, speedY;                // オブジェクトの速度
    public int imageWidth, imageHeight;         // オブジェクトの大きさ
    public boolean dead;                        // オブジェクトを消すための変数
    public int bulletStatus;                    // 弾の状態
    public int bulletAngle;                     // 弾の画像の角度
    public Rect hitRange;                       // オブジェクトの当たり判定
    public int objectType;                      // オブジェクトの種類
    public boolean isSpecial = false;           // 必殺を撃つかどうか
    public boolean isSlashing = false;          // 斬撃を撃つかどうか

    // 爆発
    public Drawable[] explosions = new Drawable[4];
    public boolean isExplosion = false;
    public int explosionIdx = 0;

    public int enemyPopCount;                   // 敵の出現時間
    public int health = 0;                      // 敵の体力
    public int attack = 1;                      // 攻撃力
    public boolean isFireBullet = false;        // 弾を撃つかどうか

    // 敵の移動
    public int enemyMoveCount;

    // コンストラクタ
    public Object(){}
    public Object(float dw, float dh){
        displayWidth = dw;
        displayHeight = dh;
    }

    // 初期設定
    public abstract void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh);
    public abstract void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs);
    public abstract void objectInit(Bitmap[] bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs);
    public abstract void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, float dx, float dy);
    public abstract void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, int epc, int hp);

    // 画像表示
    public abstract void objectDraw(Canvas canvas);

    // 移動
    public abstract void objectMove();
    public abstract void objectMove(int x, int y);

    // タップ範囲にある場合のみ移動するように
    public abstract Rect objectGetTapRect();

    // 外に出たかどうか
    public boolean isOutDisplayX(int ww){
        return (centerX - ww < 0 || centerX + ww > displayWidth);
    }

    public boolean isOutDisplayY(int hh){
        return (centerY - hh < 0 || centerY + hh > displayHeight);
    }

    // 死んだオブジェクトかどうか
    public boolean isDead(){
        return dead;
    }

    // リサイズ
    public Bitmap resizeImage(Bitmap bitImage, double size){
        double resizeScale = size;

        return Bitmap.createScaledBitmap(bitImage,
                (int) (bitImage.getWidth() * resizeScale),
                (int) (bitImage.getHeight() * resizeScale), true);
    }
}
