package com.my_first_game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;

public class Item extends Object{
    // コンストラクタ
    public Item(){}
    public Item(float dw, float dh){
        super(dw,dh);
    }

    // 初期化
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs){
        image = new BitmapDrawable(resizeImage(bitImage, 2.0));
        centerX = x;
        centerY = y;
        speedX = sx;
        speedY = sy;
        imageWidth = imgw * 2;
        imageHeight = imgh * 2;
        dead = false;
        bulletStatus = bs;
        hitRange = new Rect((int) centerX - 30, (int) centerY - 30,
                            (int) centerX + 30, (int) centerY + 30);
        objectType = 4;
    }
    @Override
    public void objectInit(Bitmap[] bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, float dx, float dy) {}

    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, int epc, ArrayList emc, ArrayList emx, ArrayList emy, int hp) {}

    // 描画
    @Override
    public void objectDraw(Canvas canvas){
        // 生きてるなら描画
        if(!dead) {
            image.setBounds((int) (centerX - imageWidth / 2),
                    (int) (centerY - imageHeight / 2),
                    (int) (centerX + imageWidth / 2),
                    (int) (centerY + imageHeight / 2));
            image.draw(canvas);
            utils.drawHitRange(hitRange,canvas);
        }
    }

    // 移動
    @Override
    public void objectMove(){
        centerY += 10;
        hitRange = new Rect((int)centerX - 30, (int) centerY - 30,
                            (int)centerX + 30, (int)centerY + 30);
        if(isOutDisplayX(-imageWidth / 2)) dead = true;
        if(isOutDisplayY(-imageHeight / 2)) dead = true;
    }
    @Override
    public void objectMove(int x, int y) {}

    @Override
    public Rect objectGetTapRect() {
        return null;
    }
}
