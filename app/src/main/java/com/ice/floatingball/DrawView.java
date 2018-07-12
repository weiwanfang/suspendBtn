package com.ice.floatingball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by weiwanfang on on 2018/7/11 17:04
 * mailbox: weiwanfang@foxmail.com
 * description:
 * update:
 * version:
 */
public class DrawView extends View {
    private Path ovalPath;
    private int mCenterCircleX;
    private int mCenterCircleY;
    private String TAG = "DrawView";
    Region mRegion;

    public DrawView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布的坐标原点移到圆心位置
        canvas.translate((getWidth() + getPaddingLeft() - getPaddingRight()) / 2,
                (getHeight() + getPaddingTop() - getPaddingBottom()) / 2);
        ovalPath = new Path();
        ovalPath.moveTo(0, 0);
        ovalPath.lineTo(100, 0);
        RectF oval = new RectF(-100, -100, 100, 100);
        ovalPath.addArc(oval, 0, 160);
        ovalPath.lineTo(0, 0);
        ovalPath.close();
        RectF r = new RectF();
        ovalPath.computeBounds(r, true);
        mRegion = new Region();
        mRegion.setPath(ovalPath, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(ovalPath, paint);
        mCenterCircleX = (getWidth() + getPaddingLeft() - getPaddingRight()) / 2;
        mCenterCircleY = (getHeight() + getPaddingTop() - getPaddingBottom()) / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x;
        float y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX() - mCenterCircleX;
                y = event.getY() - mCenterCircleY;
                boolean b = mRegion.contains((int) x, (int) y);
                Log.d(TAG, "onTouchEvent: b: " + b + " x: " + x + " y: " + y);
                break;

        }
        return true;
    }

}
