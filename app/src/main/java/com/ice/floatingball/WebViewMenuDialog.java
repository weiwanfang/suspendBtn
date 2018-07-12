package com.ice.floatingball;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by huangjun on 2015/3/21.
 */
public class WebViewMenuDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ViewGroup rootView;
    private String mHomeUrl;
    private ProgressBar mPageLoadingProgressBar;
    private WebView mWebView;
    private DrawView drawview;
    private WindowManager.LayoutParams floatBallParams;

    public WebViewMenuDialog(Context context, String url) {
        super(context, R.style.dialog_default_style);
        this.context = context;
        this.mHomeUrl = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = (ViewGroup) View.inflate(context, R.layout.webview_nim_menu_dialog, null);


        final Animation animation = AnimationUtils.loadAnimation(context,R.anim.dialog_enter);
        animation.setFillAfter(true);
        rootView.startAnimation(animation);



        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawview = new DrawView(context);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        if (floatBallParams == null) {
            floatBallParams = new WindowManager.LayoutParams();
            floatBallParams.width = drawview.getWidth();
            floatBallParams.height = drawview.getHeight();
            floatBallParams.gravity = Gravity.TOP | Gravity.LEFT;
            floatBallParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            floatBallParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            floatBallParams.format = PixelFormat.RGBA_8888;
        }

        rootView.addView(drawview, floatBallParams);


        mPageLoadingProgressBar = (ProgressBar) rootView.findViewById(R.id.webProgressBar);
        mWebView = (WebView) rootView.findViewById(R.id.webview);
        // 取消滚动条
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                //重新测量
                mWebView.measure(w, h);
                mPageLoadingProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                // mTestHandler.sendEmptyMessage(MSG_OPEN_TEST_URL);


//                String title = view.getTitle();
//                if (!TextUtils.isEmpty(title)) {
//                    titleTxt.setText(title);
//                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }


        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress >= 80) {
                    // 隐藏进度条
                    mPageLoadingProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    mPageLoadingProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    mPageLoadingProgressBar.setProgress(progress);//设置进度值
                }
//                if (mWebView.canGoBack()) {
//                    closeTxt.setVisibility(View.VISIBLE);
//                } else {
//                    closeTxt.setVisibility(View.GONE);
//                }
            }

            @Override
            public boolean onJsConfirm(WebView arg0, String arg1, String arg2, JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            View myVideoView;
            View myNormalView;
            CustomViewCallback callback;

            /**
             * 全屏播放配置
             */
            @Override
            public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
//                FrameLayout normalView = (FrameLayout) findViewById(R.id.web_filechooser);
//                ViewGroup viewGroup = (ViewGroup) normalView.getParent();
//                viewGroup.removeView(normalView);
//                viewGroup.addView(view);
//                myVideoView = view;
//                myNormalView = normalView;
//                callback = customViewCallback;
            }

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onJsAlert(WebView arg0, String arg1, String arg2, JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */
                return super.onJsAlert(null, arg1, arg2, arg3);
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title)) {
//                    titleTxt.setText(title);
                }
            }
        });


        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setBuiltInZoomControls(true);// 隐藏缩放按钮
        webSetting.setSupportZoom(false); // 不支持缩放
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
//        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
//        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
//        TbsLog.d("time-cost", "cost time: " + (System.currentTimeMillis() - time));
//        CookieSyncManager.createInstance(this);
//        CookieSyncManager.getInstance().sync();

        uploadUrl(mHomeUrl);

        setContentView(rootView);
    }

    private void uploadUrl(String url) {
        mWebView.loadUrl(url);

    }

    @Override
    public void onClick(View v) {

//        if (clickListener != null) {
//            clickListener.onButtonClick(btnName);
//        }
    }
}
