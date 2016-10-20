package com.ddgj.dd.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.ddgj.dd.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/9/28.
 */
public abstract class BaseActivity extends FragmentActivity {
    public static final int SUCCESS = 100;
    public static final int FAILDE = 101;

    public void showToastShort(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public void showToastNotNetWork() {
        Toast.makeText(this, getResources().getString(R.string.network_is_not_connection), Toast.LENGTH_LONG).show();
    }

    /**初始化控件*/
    public abstract void initView();

    /**
     * 网络检查
     * @return: true:有网  false:没网
     */
    public boolean checkNetWork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else {
            return ni.isConnected();
        }
    }

    public SweetAlertDialog showLoadingDialog(String title,String content){
        SweetAlertDialog dialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setContentText(content)
                .setTitleText(title)
                .show();
        dialog.setCancelable(true);
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        dialog.getProgressHelper().setCircleRadius(100);
        return dialog;
    }
}
