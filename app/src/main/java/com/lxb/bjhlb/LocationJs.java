package com.lxb.bjhlb;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lxb.bjhlb.utils.X5WebView;
import com.tencent.smtt.sdk.ValueCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lion on 2017/9/23.
 */

public class LocationJs implements AMapLocationListener, PermissionsResultListener {
    private Context context;

    public AMapLocationClient mLocationClient;
    public AMapLocationClientOption mLocationOption = null;
    private X5WebView mWebView;

    public LocationJs(Context context) {
        this.context = context;
        initLocation();
    }

    public void setWebView(X5WebView mWebView) {
        this.mWebView = mWebView;
    }

    @JavascriptInterface
    public void startLocation() {
        if(mLocationClient == null) {
            initLocation();
        }
        BrowserActivityPermissionsDispatcher.needsPermissionWithPermissionCheck((BrowserActivity) context);
    }

    @JavascriptInterface
    public void stopLocation() {
        mLocationClient.stopLocation();
    }

    public void initLocation() {
        mLocationClient = new AMapLocationClient(this.context);
////初始化定位参数
        mLocationOption = new AMapLocationClientOption();
////设置定位监听
        mLocationClient.setLocationListener(this);
////设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
////设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(10000);
////设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
//// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//// 在定位结束后，在合适的生命周期调用onDestroy()方法
    }


    public void onDestroy() {
        if(mLocationClient != null) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                // 定位成功回调信息，设置相关消息
                JSONObject jo = aMapLocation.toJson(1);
                JSONObject lngLat = new JSONObject();
                try {
                    lngLat.put("lat", aMapLocation.getLatitude());
                    lngLat.put("lon", aMapLocation.getLongitude());
                    jo.put("lngLat", lngLat);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mWebView != null) {
//                    mWebView.loadUrl("javascript:showFromHtml('" + ss + "')");
                    mWebView.evaluateJavascript("onLocationComplete('" + jo.toString() + "')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            if (s != null && !Boolean.valueOf(s)) {
                                mLocationClient.stopLocation();
                            }
                        }
                    });
                }
            } else {
                JSONObject jsonObject =new JSONObject();
                try {
                    jsonObject.put("errorCode", aMapLocation.getErrorCode());
                    jsonObject.put("errorInfo", aMapLocation.getErrorInfo());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                if (mWebView != null) {
                    mWebView.evaluateJavascript("onLocationError(" + jsonObject.toString() + ")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                }
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onPermissionGranted() {
        mLocationClient.startLocation();
    }

    @Override
    public void onPermissionDenied() {

    }
}
