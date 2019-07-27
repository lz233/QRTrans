package com.lz233.qrtrans;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.lz233.qrtrans.tools.StatusBarUtil;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

public class ScrollingActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private LinearLayout request_permissions_linearlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //状态栏icon黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //导航栏白色
        getWindow().setNavigationBarColor(Color.parseColor("#b5b5b5"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        //StatusBarUtil.setTranslucentStatus(this);
        //fb
        LinearLayout start_scan_linearlayout = (LinearLayout) findViewById(R.id.start_scan_linearlayout);
        LinearLayout send_file_linearlayout = (LinearLayout) findViewById(R.id.send_file_linearlayout);
        LinearLayout setting_linearlayout = (LinearLayout) findViewById(R.id.setting_linearlayout);
        request_permissions_linearlayout = (LinearLayout) findViewById(R.id.request_permissions_linearlayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appbar= (AppBarLayout) findViewById(R.id.app_bar);
        //申请权限
        final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions);
        //状态栏沉浸
        setSupportActionBar(toolbar);
        StatusBarUtil.setStatusBarDarkTheme(this,true);
        toolbar.setPadding(0,StatusBarUtil.getStatusBarHeight(this),0,0);
        //获取屏幕分辨率
        //DisplayMetrics metrics =new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        //int width = metrics.widthPixels;
        //int height = metrics.heightPixels;
        //调整appbar大小
        //ViewGroup.LayoutParams appbarlayoutparams = appbar.getLayoutParams();
        //appbarlayoutparams.height = height;
        //appbar.setLayoutParams(appbarlayoutparams);
        //按钮事件
        start_scan_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(ScrollingActivity.this, permissions)) {
                    startActivity(new Intent().setClass(ScrollingActivity.this, ScanActivity.class));
                }else {
                    Snackbar.make(v, getApplicationContext().getResources().getText(R.string.nopermissions), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        send_file_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(ScrollingActivity.this, permissions)) {
                    startActivity(new Intent().setClass(ScrollingActivity.this, SendActivity.class));
                }else {
                    Snackbar.make(v, getApplicationContext().getResources().getText(R.string.nopermissions), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        setting_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ScrollingActivity.this,SettingActivity.class));
            }
        });
    }
    public void requestPermissions(final String[] permissions){
        //申请权限
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            //Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
            request_permissions_linearlayout.setVisibility(View.GONE);
        } else {
            //没有打开相关权限、申请权限
            request_permissions_linearlayout.setVisibility(View.VISIBLE);
            request_permissions_linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EasyPermissions.requestPermissions(ScrollingActivity.this, getApplicationContext().getResources().getString(R.string.request_permissions_text), 1, permissions);
                }
            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //下面两个方法是实现EasyPermissions的EasyPermissions.PermissionCallbacks接口
    //分别返回授权成功和失败的权限
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Log.i(TAG, "获取成功的权限" + perms);
        request_permissions_linearlayout.setVisibility(View.GONE);
    }

    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //Log.i(TAG, "获取失败的权限" + perms);
    }
}
