package com.lxb.bjhlb.js;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by lion on 2017/9/23.
 */

public class LocationJs {
    private Context context;

    public LocationJs(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void startLocation() {
    }
}
