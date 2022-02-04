package com.my_first_game;

/*
    爆発オブジェクト
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;

public class Explosion extends Object{
    // コンストラクタ
    public Explosion(){}
    public Explosion(float dw, float dh){
        super(dw,dh);
    }

    // 初期化
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs) {}
    @Override
    public void objectInit(Bitmap[] bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs){
        // 爆発アニメーション
        //TODO: アニメーションを作れ
        for(int i = 0; i < 4; i++){
            explosions[i] = new BitmapDrawable(resizeImage(bitImage[i],2.0));
        }
        centerX = x;
        centerY = y;
        speedX = sx;
        speedY = sy;
        imageWidth = imgw * 2;
        imageHeight = imgh * 2;
        dead = false;
        // 爆発の初期状態
        bulletStatus = bs;
        hitRange = new Rect((int) centerX - 30, (int) centerY - 30,
                            (int) centerX + 30, (int) centerY + 30);
        objectType = 3;
    }
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, float dx, float dy) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, int epc, int hp) {}

    // 描画
    @Override
    public void objectDraw(Canvas canvas){
        explosions[explosionIdx].setBounds((int) centerX - 40, (int) centerY - 40,
                                           (int) centerX + 40, (int) centerY + 40);
        explosions[explosionIdx].draw(canvas);
        ++explosionIdx;                     // 爆発のコマを進める
        if(explosionIdx>3) dead = true;     // 最後なら死
    }

    @Override
    public void objectMove() {}

    @Override
    public void objectMove(int x, int y) {}

    @Override
    public Rect objectGetTapRect() {
        return null;
    }
}
