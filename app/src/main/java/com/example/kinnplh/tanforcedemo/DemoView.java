package com.example.kinnplh.tanforcedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import static android.graphics.Color.GRAY;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static java.lang.Float.NaN;
import static java.lang.Float.floatToIntBits;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by kinnplh on 2017/8/15.
 */

public class DemoView extends View {

    public int screenWidth; // size of the device
    public int screenHeight;
    private float densityDpi;
    Paint p;
    float tanForceMag;
    float tanForceDir;
    float norForceMag;
    float fps;
    public DemoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        densityDpi = dm.densityDpi;
        p = new Paint();
        p.setAntiAlias(true);
        tanForceMag = 0;
        tanForceDir = NaN;
        norForceMag = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setColor(RED);
        p.setTextSize(48);
        canvas.drawText(String.format("FPS: %f", fps), 100, 100, p);

        float centerX = screenWidth / 2.0f;
        float centerY = screenHeight / 2.0f;
        p.setColor(GRAY);
        for(float theta = 0; theta <= 359; theta += 10){
            float crtTheta = theta / 180 * (float)PI;
            int length = 400;
            float dx = length * (float)cos(crtTheta);
            float dy = length * (float)sin(crtTheta);
            canvas.drawLine(centerX, centerY, centerX + dx, centerY + dy, p);
        }
        p.setColor(WHITE);
        canvas.drawCircle(centerX, centerY, 25, p);
        if(tanForceMag > 0) {
            double dispL = tanForceMag * 8;
            double dx = dispL * sin(tanForceDir / 180 * PI);
            double dy = dispL * cos(tanForceDir / 180 * PI);
            canvas.drawLine(centerX, centerY,
                    centerX + (float)dx, centerY + (float)dy,  p);
        }

    }

    void drawForceinfo(float tan_mag, float tan_dir, float nor_mag){
        tanForceMag = tan_mag;
        tanForceDir = tan_dir;
        norForceMag = nor_mag;
        invalidate();
    }

    void drawFps(float fps){
        this.fps = fps;
        invalidate();
    }



}
