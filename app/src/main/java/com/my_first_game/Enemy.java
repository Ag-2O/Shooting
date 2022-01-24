package com.my_first_game;

/*
    敵オブジェクト
 */

import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class Enemy extends Object{
    // コンストラクタ
    public Enemy(){}
    public Enemy(float dw, float dh){
        super(dw,dh);
    }

    // 初期化
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh) {}
    @Override
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs){
        image = new BitmapDrawable(bitImage);

        enemyPopCount = 0;
        enemyMoveCount = 0;

        centerX = utils.setSizeX(displayWidth, x);
        centerY = utils.setSizeY(displayHeight, y);
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
    public void objectInit(Bitmap bitImage, float x, float y, float sx, float sy, int imgw, int imgh, int bs, int epc, ArrayList ems, ArrayList emr, ArrayList emc, int hp){
        //image = new BitmapDrawable(resizeImage(bitImage, 2.0));
        image = new BitmapDrawable(bitImage);

        enemyPopCount = 0;
        enemyMoveS = ems;
        enemyMoveR = emr;
        enemyMoveC = emc;
        enemyMoveCount = 0;

        centerX = utils.setSizeX(displayWidth, x);
        centerY = utils.setSizeY(displayHeight, y);
        speedX = sx;
        speedY = sy;
        imageWidth = imgw;
        imageHeight = imgh;
        dead = false;
        bulletStatus = bs;
        hitRange = new Rect((int) centerX - 60, (int) centerY - 60,
                            (int) centerX + 60, (int) centerY + 60);
        objectType = 2;
        //Log.d("Enemy objectInit","health: "+health);
        health = hp;
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
        if(moveCount != enemyMoveS.size()){
            double tx = Math.cos(utils.toRadian(enemyMoveR.get(moveCount) - 90)) *
                        enemyMoveS.get(moveCount);
            double ty = Math.sin(utils.toRadian(enemyMoveR.get(moveCount) - 90)) *
                        enemyMoveS.get(moveCount);
            centerX += tx;
            centerY += ty;
            hitRange = new Rect((int) centerX - 60,(int) centerY - 60,
                                (int) centerX + 60, (int) centerY + 60);
            if(enemyPopCount == enemyMoveC.get(moveCount)){
                ++ moveCount;
                isFireBullet = true;    // 射撃
            }
        }
        ++ enemyPopCount;

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
