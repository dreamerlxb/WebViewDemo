package com.lxb.bjhlb;

/**
 * Created by lion on 2017/11/3.
 */

public interface PermissionsResultListener {
    void onPermissionGranted();

    void onPermissionDenied();
}
