package com.my_first_game;

/*
    敵オブジェクト
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class Boss extends Object{
    private int moveNum = 0;        // 行動制御する変数
    private int horizontalNum = 0;  // 水平方向の移動のカウント
    private int code = 1;           // 符号反転
    private int flag = 1;
    private int moveWidth, moveHeight;

    // コンストラクタ
    public Boss(){}
    public Boss(float dw, float dh){
        super(dw,dh);
        moveWidth = (int)(dw / 85);
        moveHeight = (int)(dh / 200);
    }

    // 初期化
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int hp){
        image = new BitmapDrawable(bitImage);

        enemyPopCount = 0;
        enemyMoveCount = 0;

        centerX = x;
        centerY = y;
        speedX = sx;
        speedY = sy;
        imageWidth = imgw * 2;
        imageHeight = imgh * 2 ;
        dead = false;
        hitRange = new Rect((int) centerX - 150, (int) centerY - 150,
                (int) centerX + 150, (int) centerY + 150);
        objectType = 6;
        health = hp;
    }
    @Override
    public void objectInit(Bitmap[] bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, float dx, float dy) { }

    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, int epc, int hp){
        image = new BitmapDrawable(resizeImage(bitImage, 2.0));
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
        hitRange = new Rect((int) centerX - 130, (int) centerY - 130,
                (int) centerX + 130, (int) centerY + 130);
        objectType = 6;
        health = hp;
    }

    // 描画
    @Override
    public void objectDraw(Canvas canvas){
        // 生きているなら
        if(!dead){
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
        // 上からきてカニ歩きのイメージ
        if(moveNum < 7){
            centerY += speedY;
            horizontalNum = 0;
        }else if(horizontalNum < 13 * flag){
            centerX += speedX * code;
        }else{
            horizontalNum = 0;
            code *= -1;
            flag = 2;
        }
        moveNum++;
        horizontalNum++;

        hitRange = new Rect((int) centerX - 130, (int) centerY - 130,
                            (int) centerX + 130, (int) centerY + 130);

        // 範囲外なら死
        if(isOutDisplayX(- imageWidth / 2)) dead = true;
        if(isOutDisplayY(- imageHeight / 2)) dead = true;
    }
    @Override
    public void objectMove(int x, int y) {}

    @Override
    public Rect objectGetTapRect() {
        return null;
    }
}
