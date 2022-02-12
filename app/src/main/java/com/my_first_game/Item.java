package com.my_first_game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

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
        image = new BitmapDrawable(bitImage);
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
        objectType = 4;
    }
    @Override
    public void objectInit(Bitmap[] bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, float dx, float dy) {}

    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, int epc, int hp) {}

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
        hitRange = new Rect((int)centerX - 60, (int) centerY - 60,
                            (int)centerX + 60, (int)centerY + 60);
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
