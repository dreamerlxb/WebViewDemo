package com.lxb.bjhlb;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by sxb on 2017/9/8.
 * Caching Web Resources in The Android Device
 * 在Android设备缓存资源
 */

public class WebViewClientImpl extends WebViewClient {
    private Activity activity = null;
    private UrlCache urlCache = null;

    public WebViewClientImpl(Activity activity) {
        this.activity = activity;
        this.urlCache = new UrlCache(activity);

        this.urlCache.register("http://tutorials.jenkov.com/", "tutorials-jenkov-com.html",
                "text/html", "UTF-8", 5 * UrlCache.ONE_MINUTE);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

//        if(url.indexOf("jenkov.com") > -1 ) return false;
//
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        activity.startActivity(intent);
//        return true;

        view.loadUrl(request.toString());
        return true;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Log.i("WebViewClientImpl==>", request.toString());
        return this.urlCache.load(request.toString());
    }
}
