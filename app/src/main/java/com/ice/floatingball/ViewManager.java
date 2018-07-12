package com.ice.floatingball;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asd on 1/1/2017.
 */

public class ViewManager {
    FloatingView floatBall;
    WindowManager windowManager;
    public static ViewManager manager;
    Context context;
    private WindowManager.LayoutParams floatBallParams;

    private WindowManager.LayoutParams trigonViewParams;

    TrigonView trigonView;

    private ViewManager(Context context) {
        this.context = context;
    }

    public static ViewManager getInstance(Context context) {
        if (manager == null) {
            manager = new ViewManager(context);
        }
        return manager;
    }

    public void showFloatBall() {
        floatBall = new FloatingView(context);
        trigonView = new TrigonView(context);

        floatBall.setBackgroundResource(R.mipmap.btn_link);
        trigonView.setBackgroundResource(R.mipmap.bg_link_close);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (floatBallParams == null) {
            floatBallParams = new WindowManager.LayoutParams();
            floatBallParams.width = floatBall.width;
            floatBallParams.height = floatBall.height;
            floatBallParams.gravity = Gravity.TOP | Gravity.LEFT;
            floatBallParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            floatBallParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            floatBallParams.format = PixelFormat.RGBA_8888;
        }

        if (trigonViewParams == null) {
            trigonViewParams = new WindowManager.LayoutParams();
            trigonViewParams.width = trigonView.width;
            trigonViewParams.height = trigonView.height;
            trigonViewParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            trigonViewParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            trigonViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            trigonViewParams.format = PixelFormat.RGBA_8888;
        }

        windowManager.addView(trigonView, trigonViewParams);
        windowManager.addView(floatBall, floatBallParams);

        trigonView.setVisibility(View.GONE);

        floatBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(MyAccessibilityService.BACK);
//                Toast.makeText(context, "点击了悬浮球 执行后退操作", Toast.LENGTH_SHORT).show();

                if (teamNotifyDialog == null) {
                    showTeamNotifyMenu();
                } else {
                    if (teamNotifyDialog.isShowing()) {
                        teamNotifyDialog.dismiss();
                    } else {
                        teamNotifyDialog.show();
                    }
                }


            }
        });



        floatBall.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;
            float tempX;
            float tempY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int RawX = (int) event.getRawX();
                int RawY = (int) event.getRawY();
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                int screenWidth = dm.widthPixels;
                int screenHeight = dm.heightPixels;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();

                        tempX = event.getRawX();
                        tempY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - startX;
                        float dy = event.getRawY() - startY;
                        //计算偏移量，刷新视图
                        floatBallParams.x += dx;
                        floatBallParams.y += dy;
                        windowManager.updateViewLayout(floatBall, floatBallParams);
                        startX = event.getRawX();
                        startY = event.getRawY();

                        Log.i("ACTION_MOVE", "dx: " + dx + "    dy:" + dy);
                        trigonView.setVisibility(View.VISIBLE);
                        if (RawX > (screenWidth - trigonView.getWidth()) && RawY > (screenHeight - trigonView.getHeight())) {
                            Log.i("悬浮球", "在内部");
                            //从外到内震动
                            trigonView.getBackground().setAlpha(100);


                            Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                            long[] patter = {0, 50};
                            vibrator.vibrate(patter, -1);

                        } else {

                            Log.i("悬浮球", "在外部");
                            //透明度百分之50
                            trigonView.getBackground().setAlpha(50);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        trigonView.setVisibility(View.GONE);
                        //判断松手时View的横坐标是靠近屏幕哪一侧，将View移动到依靠屏幕
                        if (RawX > (screenWidth - trigonView.getWidth()) && RawY > (screenHeight - trigonView.getHeight())) {
                            Log.i("悬浮球", "在内部");
                            //从外到内震动
                            floatBall.setVisibility(View.GONE);
                        } else {
                            Log.i("悬浮球", "在外部");
                        }


                        //判断松手时View的横坐标是靠近屏幕哪一侧，将View移动到依靠屏幕
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (endX < getScreenWidth() / 2) {
                            endX = 0;
                        } else {
                            endX = getScreenWidth() - floatBall.width;
                        }
                        floatBallParams.x = (int) endX;
                        windowManager.updateViewLayout(floatBall, floatBallParams);
                        //如果初始落点与松手落点的坐标差值超过6个像素，则拦截该点击事件
                        //否则继续传递，将事件交给OnClickListener函数处理
                        if (Math.abs(endX - tempX) > 6 && Math.abs(endY - tempY) > 6) {
                            return true;
                        }
                        break;
                }
                return false;
            }

        });
    }

    private WebViewMenuDialog teamNotifyDialog;

    private void showTeamNotifyMenu() {
        List<String> btnNames = new ArrayList<>();
        btnNames.add("1111111");
        btnNames.add("2222222");
        if (teamNotifyDialog == null) {
            teamNotifyDialog = new WebViewMenuDialog(context, "https://www.baidu.com/");
        }
        teamNotifyDialog.show();
        teamNotifyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                teamNotifyDialog = null;

            }
        });
    }

    public int getScreenWidth() {
        return windowManager.getDefaultDisplay().getWidth();
    }


}
