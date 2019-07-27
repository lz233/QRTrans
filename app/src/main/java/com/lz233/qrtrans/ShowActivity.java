package com.lz233.qrtrans;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lz233.qrtrans.tools.DensityUtil;
import com.lz233.qrtrans.tools.QRCodeUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static java.lang.String.*;

public class ShowActivity extends AppCompatActivity {
    private String file_path;
    private int code_size;
    private int code_content_size;
    private ImageView code_imageview;
    private InputStream inputStream;
    private byte[] buff;
    private SharedPreferences sharedPreferences;
    private long code_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        //状态栏icon黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //导航栏白色
        getWindow().setNavigationBarColor(Color.parseColor("#b5b5b5"));
        //获取file_path
        Intent intent = getIntent();
        file_path = intent.getStringExtra("file_path");
        //fb
        code_imageview = (ImageView) findViewById(R.id.code_imageview);
        //初始化sp
        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        //初始化code_size
        code_content_size = sharedPreferences.getInt("code_size",1);
        //初始化code_time
        code_time = (long)(sharedPreferences.getFloat("code_time",(float)0.1)*1000);
        //初始化buff
        buff = new byte[code_content_size];
        //初始化inputStream
        try {
            inputStream = new FileInputStream(file_path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //获取qr码大小
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        code_size = metrics.widthPixels;// - DensityUtil.dip2px(this, 120);
        //code_imageview.setImageBitmap(QRCodeUtil.createQRCodeBitmap(file_path, code_size, code_size));
        //启动qr码进程
        ShowQRCode();
    }
    private void ShowQRCode(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int len ;
                    Gson gson = new Gson();

                    int[] ints = new int[code_content_size];
                    //final StringBuffer stringBuffer = new StringBuffer();
                    //inputStream.read();
                    while (true) {
                        len = inputStream.read(buff);
                        if (len == -1) {
                            break;
                        }
                        for (int i = 0;i<buff.length;i++) {
                            ints[i] = buff[i];
                        }
                        final JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("id",0);
                        jsonObject.addProperty("content",gson.toJson(ints,int[].class));
                        code_imageview.post(new Runnable() {
                            @Override
                            public void run() {

                                code_imageview.setImageBitmap(QRCodeUtil.createQRCodeBitmap(jsonObject.toString(), code_size, code_size,"","","0",Color.BLACK,Color.WHITE));
                            }
                        });
                        Thread.sleep(code_time);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private char[] getChars (byte[] bytes) {
        Charset cs = Charset.forName ("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate (bytes.length);
        bb.put (bytes);
        bb.flip ();
        CharBuffer cb = cs.decode (bb);
        return cb.array();
    }
}
