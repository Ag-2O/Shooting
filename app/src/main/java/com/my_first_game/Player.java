package com.my_first_game;

/*
    プレイヤーオブジェクト
 */

import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

public class Player extends Object{
    // コンストラクタ
    public Player(){}
    public Player(float dw, float dh){
        super(dw,dh);
    }

    // 初期設定
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs){
        image = new BitmapDrawable(bitImage);
        centerX = x;
        centerY = y;
        speedX = sx;
        speedY = sy;
        imageWidth = imgw;
        imageHeight = imgh;
        dead = false;
        bulletStatus = bs;
        hitRange = new Rect((int) centerX - 40, (int) centerY - 40,
                            (int) centerX + 40, (int) centerY + 40);
        objectType = 0;
    }
    @Override
    public void objectInit(Bitmap[] bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, float dx, float dy) { }
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, int epc, int hp) {}

    // 描画
    @Override
    public void objectDraw(Canvas canvas){
        // 生きてるなら描画
        if(!dead){
            image.setBounds((int)(centerX - imageWidth / 2),
                            (int)(centerY - imageHeight / 2),
                            (int)(centerX + imageWidth / 2),
                            (int)(centerY + imageHeight / 2));
            image.draw(canvas);
        }
    }

    // 移動
    @Override
    public void objectMove(){}
    @Override
    public void objectMove(int diffX, int diffY){
        float centerXX = centerX;
        float centerYY = centerY;
        centerX = centerX - diffX;
        centerY = centerY - diffY;

        // 画面外なら移動しない
        if(isOutDisplayX(imageWidth / 2)) centerX = centerXX;
        if(isOutDisplayY(imageHeight / 2)) centerY = centerYY;

        hitRange = new Rect((int) centerX - 30, (int) centerY - 30,
                            (int) centerX + 30, (int) centerY + 30);

    }

    // 範囲を広げてタップした時に移動できるように
    @Override
    public Rect objectGetTapRect(){
        return new Rect(
                image.getBounds().left - 50,
                image.getBounds().top - 50,
                image.getBounds().right + 50,
                image.getBounds().bottom + 50
        );
    }
}
