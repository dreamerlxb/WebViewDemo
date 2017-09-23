package com.lxb.bjhlb.js;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by lion on 2017/9/22.
 */

public class ToastJs {
    private Context context;

    public ToastJs(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void showToast(String title) {
        Log.i("=====>", title);
        Toast.makeText(context, title, Toast.LENGTH_LONG).show();
    }
}
