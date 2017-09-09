package com.lxb.bjhlb;

import android.app.Activity;
import android.webkit.JavascriptInterface;

/**
 * Created by sxb on 2017/9/8.
 */

public class LocationJsProxy {

    private Activity activity = null;

    public LocationJsProxy(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void showMessage(String message) {

    }
}
