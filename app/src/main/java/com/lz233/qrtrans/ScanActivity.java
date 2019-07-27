package com.lz233.qrtrans;

import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lz233.qrtrans.tools.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import cn.simonlee.xcodescanner.core.CameraScanner;
import cn.simonlee.xcodescanner.core.GraphicDecoder;
import cn.simonlee.xcodescanner.core.NewCameraScanner;
import cn.simonlee.xcodescanner.core.OldCameraScanner;
import cn.simonlee.xcodescanner.core.ZBarDecoder;
import cn.simonlee.xcodescanner.view.AdjustTextureView;

public class ScanActivity extends AppCompatActivity implements CameraScanner.CameraListener, TextureView.SurfaceTextureListener, GraphicDecoder.DecodeListener, View.OnClickListener{
    private AdjustTextureView mTextureView;
    private View mScannerFrameView;
    private CameraScanner mCameraScanner;
    protected GraphicDecoder mGraphicDecoder;
    private RecyclerView result_list;
    private ResultListAdapter resultlistadapter;
    private String result_histroy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        //状态栏icon黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //导航栏白色
        getWindow().setNavigationBarColor(Color.parseColor("#b5b5b5"));
        //fb
        mTextureView = findViewById(R.id.textureview);
        mScannerFrameView = findViewById(R.id.scannerframe);
        result_list = (RecyclerView) findViewById(R.id.result_list);
        result_histroy = new String();
        //启动扫码
        mTextureView.setSurfaceTextureListener((TextureView.SurfaceTextureListener) this);
        /*
         * 注意，SDK21的设备是可以使用NewCameraScanner的，但是可能存在对新API支持不够的情况，比如红米Note3（双网通Android5.0.2）
         * 开发者可自行配置使用规则，比如针对某设备型号过滤，或者针对某SDK版本过滤
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraScanner = new NewCameraScanner((CameraScanner.CameraListener) this);
        } else {
            mCameraScanner = new OldCameraScanner((CameraScanner.CameraListener) this);
        }
        //获取屏幕分辨率
        DisplayMetrics metrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        //调整mTextureView大小
        ViewGroup.LayoutParams mTextureView_layoutParams = mTextureView.getLayoutParams();
        mTextureView_layoutParams.height = width-DensityUtil.dip2px(this,120);
        mTextureView.setLayoutParams(mTextureView_layoutParams);
        //调整mScannerFrameView
        mScannerFrameView.setFocusable(true);
        //mScannerFrameView.isFrameLineVisible();
        //result_list
        result_list.setLayoutManager(new LinearLayoutManager(this));
        resultlistadapter = new ResultListAdapter(ScanActivity.this);
    }



    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCameraScanner.setPreviewTexture(surface);
        mCameraScanner.setPreviewSize(width, height);
        mCameraScanner.openCamera(this);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void openCameraSuccess(int frameWidth, int frameHeight, int frameDegree) {
        mTextureView.setImageFrameMatrix(frameWidth, frameHeight, frameDegree);
        if (mGraphicDecoder == null) {
            mGraphicDecoder = new ZBarDecoder(this);//可使用二参构造方法指定条码识别的类型
        }
        //调用setFrameRect方法会对识别区域进行限制，注意getLeft等获取的是相对于父容器左上角的坐标，实际应传入相对于TextureView左上角的坐标。
        mCameraScanner.setFrameRect(mScannerFrameView.getLeft(), mScannerFrameView.getTop(), mScannerFrameView.getRight(), mScannerFrameView.getBottom());
        mCameraScanner.setGraphicDecoder(mGraphicDecoder);
    }

    @Override
    public void openCameraError() {

    }

    @Override
    public void noCameraPermission() {
        Toast.makeText(this,R.string.nopermissions,Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void cameraDisconnected() {

    }

    @Override
    public void cameraBrightnessChanged(int brightness) {

    }

    @Override
    public void decodeComplete(String result, int type, int quality, int requestCode) {
        try {
            //Toast.makeText("[类型" + type + "/精度" + quality + "]" + result, LENGTH_SHORT);
            if ((!result.equals(result_histroy)) & (!result.isEmpty())) {
                //Toast.makeText(this, "[类型" + type + "/精度" + quality + "]" + result, Toast.LENGTH_SHORT).show();
                //result_text.setText(result);
                result_histroy = result;
                //添加到result_list并滚动到最底部
                resultlistadapter.addData(result);
                result_list.setAdapter(resultlistadapter);
                result_list.scrollToPosition(resultlistadapter.getItemCount() - 1);
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void onDestroy() {
        mCameraScanner.setGraphicDecoder(null);
        mCameraScanner.detach();
        if (mGraphicDecoder != null) {
            mGraphicDecoder.setDecodeListener(null);
            mGraphicDecoder.detach();
        }
        super.onDestroy();
    }
    public void onPause() {
        mCameraScanner.closeCamera();
        super.onPause();
    }
    public void onRestart() {
        //部分机型在后台转前台时会回调onSurfaceTextureAvailable开启相机，因此要做判断防止重复开启
        if (mTextureView.isAvailable()) {
            //mCameraScanner.setPreviewTexture(mTextureView.getSurfaceTexture());
            //mCameraScanner.setPreviewSize(mTextureView.getWidth(), mTextureView.getHeight());
            mCameraScanner.openCamera(this.getApplicationContext());
        }
        super.onRestart();
    }
}
