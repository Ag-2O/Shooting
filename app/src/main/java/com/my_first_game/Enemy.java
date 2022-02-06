package com.my_first_game;

/*
    敵オブジェクト
 */

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class Enemy extends Object{
    private int moveNum = 0;        // 行動制御する変数
    private int horizontalNum = 0;  // 水平方向の移動のカウント
    private int code = 1;           // 符号反転
    private int flag = 1;
    private int interval = 20;      // 射撃間隔
    private int moveWidth, moveHeight;
    private int popType;            // 湧き場所
    Random random = new Random();   // 乱数

    // コンストラクタ
    public Enemy(){}
    public Enemy(float dw, float dh){
        super(dw,dh);
        moveWidth = (int)(dw / 45);
        moveHeight = (int)(dh / 200);
    }

    // 初期化
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs){
        image = new BitmapDrawable(bitImage);

        enemyPopCount = 0;
        enemyMoveCount = 0;

        centerX = x;
        centerY = y;
        speedX = sx;
        speedY = sy;
        imageWidth = imgw / 2;
        imageHeight = imgh / 2 ;
        dead = false;
        bulletStatus = bs;
        hitRange = new Rect((int) centerX - 30, (int) centerY - 30,
                (int) centerX + 30, (int) centerY + 30);
        objectType = 2;
    }
    @Override
    public void objectInit(Bitmap[] bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, float dx, float dy) { }

    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, int pt, int hp){
        image = new BitmapDrawable(bitImage);
        enemyPopCount = 0;
        enemyMoveCount = 0;
        centerX = x;
        centerY = y;
        speedX = sx;
        speedY = sy;
        imageWidth = imgw;
        imageHeight = imgh;
        dead = false;
        bulletStatus = bs;
        hitRange = new Rect((int) centerX - 60, (int) centerY - 60,
                            (int) centerX + 60, (int) centerY + 60);
        objectType = 2;
        health = hp;
        popType = pt;

        if(random.nextBoolean()) code = -1;
    }

    // 描画
    @Override
    public void objectDraw(Canvas canvas){
        // 生きているなら
        if(!dead){
            //Log.d("Enemy objectDraw","centerX: ["+ centerX +"], centerY: ["+ centerY +"] ");
            image.setBounds((int)(centerX - imageWidth / 2),
                            (int)(centerY - imageHeight / 2),
                            (int)(centerX + imageWidth / 2),
                            (int)(centerY + imageHeight / 2));
            image.draw(canvas);
            utils.drawHitRange(hitRange, canvas);
        }
    }

    // 移動
    @Override
    public void objectMove() {
        if(popType == 1){
            straightMove();     // 真下に向かって直進
        }else if(popType == 2){
            stopMove();         // 上部で停滞
        }else{
            defaultMove();      // 上部で左右に動く
        }

        fire();                 // 射撃

        hitRange = new Rect((int) centerX - 60, (int) centerY - 60,
                           (int) centerX + 60, (int) centerY + 60);

        // 範囲外なら死
        if(isOutDisplayX(- imageWidth / 2)) dead = true;
        if(isOutDisplayY(- imageHeight / 2)) dead = true;

        interval --;
    }
    @Override
    public void objectMove(int x, int y) {}

    @Override
    public Rect objectGetTapRect() {
        return null;
    }

    // 基本の動き
    public void defaultMove(){
        // 上からきてレレレ
        if(moveNum < moveHeight) {
            centerY += speedY;
            horizontalNum = 0;
        }else if(moveNum > 180){
            // 強化
            centerY += speedY;
            interval --;
        }else if(horizontalNum < moveWidth * flag){
            centerX += speedX * code;
        }else{
            horizontalNum = 0;
            code *= -1;
            flag = 2;
        }

        moveNum++;
        horizontalNum++;
    }

    // まっすぐ
    public void straightMove(){
        centerY += speedY / 2;
    }

    // 上部で停滞
    public void stopMove(){
        if(moveNum < moveHeight*2) centerY += speedY;   // 停滞
        if(moveNum > 250) centerX += speedX;            // 一定時間で退場
        moveNum++;
    }

    // 射撃
    public void fire(){
        if(interval < 0){
            isFireBullet = true;
            interval = 30;
        }
    }

}
