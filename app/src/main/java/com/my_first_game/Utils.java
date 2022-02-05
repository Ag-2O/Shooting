package com.my_first_game;

/*
    適当な関数まとめ
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;

import java.security.spec.ECField;
import java.util.ArrayList;

public class Utils {
    // 変数ズ
    static public final float screenWidth = 1080;
    static public final float screenHeight = 2148;
    private static final double PIE = 3.14159226;

    // 座標と画像が重なっているかどうか
    public boolean isRectOverlap(int x, int y, Rect img){
        return (img.left < x && img.top < y && img.right > x && img.bottom > y);
    }

    // ラジアンへ
    public double toRadian(double deg){
        return (deg * PIE / 180);
    }

    // 2点間の角度を求める
    public double getAngle(double x, double y, double x2, double y2){
        return (Math.atan2(y2 - y, x2 - x) * 180d / PIE);
    }

    // 当たりを判範囲表示
    public void drawHitRange(Rect rect, Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.RED);
    }

    // 効果音再生
    public void playSound(MediaPlayer mp){
        mp.seekTo(0);
        mp.start();
    }
}
