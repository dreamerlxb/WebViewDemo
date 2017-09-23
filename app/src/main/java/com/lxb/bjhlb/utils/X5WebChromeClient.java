package com.lxb.bjhlb.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by lion on 2017/9/22.
 */

public class X5WebChromeClient extends WebChromeClient {
    private Context context;

    public X5WebChromeClient(Context context) {
        this.context = context;
    }

    @Override
    public boolean onJsAlert(WebView webView, String s, String s1, final JsResult jsResult) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        Log.i("url", s);
        b.setTitle("Alert");
        b.setMessage(s1);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jsResult.confirm();
            }
        });
        b.setCancelable(false);
        b.create().show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView webView, String s, String s1, final JsResult jsResult) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("Confirm");
        b.setMessage(s1);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jsResult.confirm();
            }
        });
        b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jsResult.cancel();
            }
        });
        b.create().show();
        return true;
    }
}
