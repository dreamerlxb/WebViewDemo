package com.lxb.bjhlb;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loading Resources From App APK Assets
 * Created by sxb on 2017/9/8.
 */

public class WebViewClientImpl2 extends WebViewClient {
    private Activity activity = null;

    public WebViewClientImpl2(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(url.indexOf("jenkov.com") > -1 ) return false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
    }


    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

        if(url.startsWith("http://tutorials.jenkov.com/images/logo.png")){
            return loadFromAssets(url, "images/logo.png", "image/png", "");
        }

        return null;
    }

    // 从 apk 中加载资源
    private WebResourceResponse loadFromAssets( String url,
                                                String assetPath, String mimeType, String encoding){

        AssetManager assetManager = this.activity.getAssets();
        InputStream input = null;
        try {
//            Log.d(Constants.LOG_TAG, "Loading from assets: " + assetPath);


            input = assetManager.open("/images/logo.png");

            WebResourceResponse response =
                    new WebResourceResponse(mimeType, encoding, input);

            return response;
        } catch (IOException e) {
            Log.e("WEB-APP", "Error loading " + assetPath + " from assets: " +
                    e.getMessage(), e);
        }
        return null;
    }
}
