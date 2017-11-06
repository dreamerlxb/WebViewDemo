package com.lxb.bjhlb.webview_native;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.lxb.bjhlb.LocationJs;
import com.lxb.bjhlb.R;
import com.lxb.bjhlb.js.ToastJs;
import com.lxb.bjhlb.utils.X5WebView;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;

import java.net.MalformedURLException;
import java.net.URL;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class NativeWebViewActivity extends Activity {
    /**
     * 作为一个浏览器的示例展示出来，采用android+web的模式
     */
    private WebView mWebView;
    private ViewGroup mViewParent;
    private ValueCallback<Uri[]> uploadFilesCb;
    //"file:///android_asset/location.html";
    private static final String mHomeUrl = "http://192.168.1.189:8080/sdrpoms/mobileIndex.jsp"; //"http://192.168.1.113:8080/sdrpoms/";//"file:///android_asset/location.html";
    private static final String TAG = "WebView";
    public static final int RESULT_CODE = 1000;
    public static final int REQUEST_CODE = 1003;

    private ValueCallback<Uri> uploadFileCb;

    private URL mIntentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        // locationJs = new LocationJs(this);
        Intent intent = getIntent();
        if (intent != null) {
            try {
                mIntentUrl = new URL(intent.getData().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            } catch (Exception e) {
            }
        }
        //
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }

		/*
         * getWindow().addFlags(
		 * android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
        setContentView(R.layout.activity_main);
        mViewParent = (ViewGroup) findViewById(R.id.webView1);
        init();
    }

    private void openFileChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "文件选择"), REQUEST_CODE);
    }

    private void init() {
        mWebView = new WebView(this, null);
        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG + "URL LOADING", url);
                return false;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG + "Page Finished", url);
                if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16) {
                }
            }
        });

//        locationJs.setWebView(mWebView);

        mWebView.setWebChromeClient(new WebChromeClient() {

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                NativeWebViewActivity.this.uploadFileCb = valueCallback;
                openFileChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                NativeWebViewActivity.this.uploadFileCb = valueCallback;
                openFileChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                NativeWebViewActivity.this.uploadFileCb = valueCallback;
                openFileChooserActivity();
            }

            // For Android  >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                Log.i("File Chooser", fileChooserParams.toString());
                NativeWebViewActivity.this.uploadFilesCb = filePathCallback;
                openFileChooserActivity();
                return true;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                Log.i("GeoPermShowPrompt", origin);
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NativeWebViewActivity.this);
                builder.setMessage("允许访问地址信息");
                DialogInterface.OnClickListener dialogButtonOnClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int clickedButton) {
                        if (DialogInterface.BUTTON_POSITIVE == clickedButton) {
                            callback.invoke(origin, true, true);
                        } else if (DialogInterface.BUTTON_NEGATIVE == clickedButton) {
                            callback.invoke(origin, false, false);
                        }
                    }
                };
                builder.setPositiveButton("允许", dialogButtonOnClickListener);
                builder.setNegativeButton("拒绝", dialogButtonOnClickListener);
                builder.show();
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, final JsResult jsResult) {
                AlertDialog.Builder b = new AlertDialog.Builder(NativeWebViewActivity.this);
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
                AlertDialog.Builder b = new AlertDialog.Builder(NativeWebViewActivity.this);
                b.setTitle("确认");
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


            View myVideoView;
            View myNormalView;
            CustomViewCallback callback;

            /**
             * 全屏播放配置
             */
            @Override
            public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
                FrameLayout normalView = (FrameLayout) findViewById(R.id.web_filechooser);
                ViewGroup viewGroup = (ViewGroup) normalView.getParent();
                viewGroup.removeView(normalView);
                viewGroup.addView(view);
                myVideoView = view;
                myNormalView = normalView;
                callback = customViewCallback;
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
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2, String arg3, long arg4) {
                Log.d(TAG, "url: " + arg0);
                new AlertDialog.Builder(NativeWebViewActivity.this)
                        .setTitle("是否允许下载？")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(NativeWebViewActivity.this,"fake message: i'll download...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(NativeWebViewActivity.this,"fake message: refuse download...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        Toast.makeText(NativeWebViewActivity.this,"fake message: refuse download...", Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
            }
        });

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
       // webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", Context.MODE_PRIVATE).getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        //webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

// js 调用 Android 方法
        mWebView.addJavascriptInterface(new ToastJs(this), "android");
//        mWebView.addJavascriptInterface(locationJs, "myLocation");

        long time = System.currentTimeMillis();
        if (mIntentUrl == null) {
            mWebView.loadUrl(mHomeUrl);
        } else {
            mWebView.loadUrl(mIntentUrl.toString());
        }
        Log.i("time-cost", "cost time: " + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult, requestCode:" + requestCode + ",resultCode:" + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    if (null != uploadFileCb) {
                        Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                        uploadFileCb.onReceiveValue(result);
                        uploadFileCb = null;
                    } else if (null != uploadFilesCb) {
                        Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                        uploadFilesCb.onReceiveValue(new Uri[]{ result });
                        uploadFilesCb = null;
                    }
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (null != uploadFileCb) {
                uploadFileCb.onReceiveValue(null);
                uploadFileCb = null;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || mWebView == null || intent.getData() == null) {
            return;
        }
        mWebView.loadUrl(intent.getData().toString());
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
        }
//        if(locationJs != null) {
//            locationJs.onDestroy();
//        }
        super.onDestroy();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void needsPermission() {
        Toast.makeText(this, "已经获取定位权限", Toast.LENGTH_SHORT).show();
        //locationJs.onPermissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NativeWebViewActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void showRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("获取位置信息")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 再次执行请求
                        request.proceed();
                    }
                })
                .show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void deniedPermission() {
        Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
        //locationJs.onPermissionDenied();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void neverAskAgain() {
        Toast.makeText(this, "不再询问", Toast.LENGTH_SHORT).show();
    }
}
