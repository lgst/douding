package com.ddgj.dd.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by lzq on 2016/6/30.
 */
public class PermissionUtils {

    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 200;

    /**
     * 获取所有权限
     *
     * @param requestCode 请求权限结果返回码
     */
    public static void needPermission(Activity context, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        requestAllPermissions(context, requestCode);
    }

    public static boolean requestAllPermissions(Activity context, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (hasPermission(context, Manifest.permission.CALL_PHONE) &&
                hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                hasPermission(context, Manifest.permission.CAMERA) &&
                hasPermission(context, Manifest.permission.READ_CONTACTS) &&
                hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    requestCode);
            return false;
        }
    }

    public static boolean hasPermission(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
            return true;
        else return false;
    }

    /**
     * 获取拨打电话权限
     *
     * @param context
     * @param requestCode 请求权限结果返回码
     * @return 如果已经获取到该权限返回true，否则返回true
     */
    public static boolean requesCallPhonePermissions(Activity context, int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.CALL_PHONE},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取文件读写权限
     *
     * @param context
     * @param requestCode 请求权限结果返回码
     * @return 如果已经获取到该权限返回true，否则返回true
     */
    public static boolean requestReadSDCardPermissions(Activity context, int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取拍照权限
     *
     * @param context
     * @param requestCode 请求权限结果返回码
     * @return 如果已经获取到该权限返回true，否则返回true
     */
    public static boolean requestCamerPermissions(Activity context, int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.CAMERA},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取联系人权限
     *
     * @param context
     * @param requestCode 请求权限结果返回码
     * @return 如果已经获取到该权限返回true，否则返回true
     */
    public static boolean requestReadConstantPermissions(Activity context, int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean requestGET_ACCOUNTSPermissions(Activity context, int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 位置信息权限
     *
     * @param context
     * @param requestCode 请求权限结果返回码
     * @return 如果已经获取到该权限返回true，否则返回true
     */
    public static boolean requestLocationPermissions(Activity context, int requestCode) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//没有权限
            ActivityCompat.requestPermissions(context,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    requestCode);
            return false;
        } else {
            return true;
        }
    }
}  