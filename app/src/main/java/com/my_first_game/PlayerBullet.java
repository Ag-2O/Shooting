package com.my_first_game;

/*
    自分が発射した弾オブジェクト
 */


import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.util.ArrayList;

public class PlayerBullet extends Object {
    public double size = 0.5;       // 弾の倍率
    // コンストラクタ
    public PlayerBullet(){}
    public PlayerBullet(float dw, float dh){
        super(dw,dh);
    }
    public PlayerBullet(float dw, float dh, double s){
        super(dw,dh);
        size = s;
    }

    // 初期化
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int atk) {
        image = new BitmapDrawable(resizeImage(bitImage, size));
        centerX = x;
        centerY = y;
        speedX = sx;
        speedY = sy;
        imageWidth = imgw / 2;
        imageHeight = imgh / 2 ;
        dead = false;
        bulletAngle = 0;
        hitRange = new Rect((int) centerX - 20, (int) centerY - 20,
                            (int) centerX + 20, (int) centerY + 20);
        objectType = 1;
        attack = atk;
    }
    @Override
    public void objectInit(Bitmap[] bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, float dx, float dy) { }
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, int epc, ArrayList emc, ArrayList emx, ArrayList emy, int hp) {}

    // 描画
    @Override
    public void objectDraw(Canvas canvas) {
        // 生きているなら
        if(!dead){
            // 保存してから回転
            try {
                //TODO: NullPointerException、謎なのでtry文で回避させているが直したい
                canvas.save();
                image.setBounds((int) (centerX - imageWidth / 2),
                        (int) (centerY - imageHeight / 2),
                        (int) (centerX + imageWidth / 2),
                        (int) (centerY + imageHeight / 2));
                canvas.rotate(bulletAngle, centerX, centerY);
                image.draw(canvas);
                utils.drawHitRange(hitRange,canvas);
                canvas.restore();
            } catch (NullPointerException e){
                canvas.restore();
            }
        }
    }

    // 移動
    @Override
    public void objectMove() {
        centerX += (float) Math.cos(utils.toRadian(bulletAngle - 90)) * speedX;
        centerY += (float) Math.sin(utils.toRadian(bulletAngle - 90)) * speedY;

        // 当たり範囲の更新
        hitRange = new Rect((int) centerX - 20, (int) centerY - 20,
                (int) centerX + 20, (int) centerY + 20);

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
