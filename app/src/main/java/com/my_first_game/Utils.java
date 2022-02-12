package com.my_first_game;

/*
    適当な関数まとめ
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Utils {
    private static final double PIE = 3.14159226;

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
}
