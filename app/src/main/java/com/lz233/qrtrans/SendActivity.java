package com.lz233.qrtrans;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.time.Instant;

public class SendActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TextView file_path_text;
    private Button select_button;
    private SeekBar code_size_seekbar;
    private TextView code_size_text;
    private SeekBar code_time_seekbar;
    private TextView code_time_text;
    private TextView estimated_time_text;
    private String file_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //状态栏icon黑色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //导航栏白色
        getWindow().setNavigationBarColor(Color.parseColor("#b5b5b5"));
        //fb
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        file_path_text = (TextView) findViewById(R.id.file_path_text);
        select_button = (Button) findViewById(R.id.select_button);
        code_size_seekbar = (SeekBar) findViewById(R.id.code_size_seekbar);
        code_size_text = (TextView) findViewById (R.id.code_size_text);
        code_time_seekbar = (SeekBar) findViewById(R.id.code_time_seekbar);
        code_time_text = (TextView) findViewById(R.id.code_time_text);
        estimated_time_text = (TextView)findViewById(R.id.estimated_time_text);
        //初始化sp
        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        //初始化code_size_seekbar&text
        int code_size = sharedPreferences.getInt("code_size",1);
        code_size_seekbar.setProgress(code_size);
        code_size_text.setText(String.valueOf(code_size)+" B");
        //初始化code_time_seekbar&text
        Float code_time = sharedPreferences.getFloat("code_time", (float) 0.1);
        code_time_seekbar.setProgress((int)(code_time*10));
        code_time_text.setText(String.valueOf(code_time)+" S");
        //设置监听器
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(file_path != null) {
                    Intent intent = new Intent(SendActivity.this, ShowActivity.class);
                    intent.putExtra("file_path",file_path);
                    startActivity(intent);
                }else {
                    Snackbar.make(view, getApplicationContext().getResources().getText(R.string.no_file_selected), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });
        code_size_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    code_size_seekbar.setProgress(1);
                    code_size_text.setText("1 B");
                }else {
                    code_size_text.setText(String.valueOf(progress) + " B");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("code_size",code_size_seekbar.getProgress());
                editor.apply();
                CalculateTheEstimatedTime();
            }
        });
        code_time_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    code_time_seekbar.setProgress(1);
                    code_time_text.setText("0.1 S");
                }else {
                    code_time_text.setText(String.valueOf(((float)progress)/10) + " S");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("code_time",((float)code_time_seekbar.getProgress())/10);
                editor.apply();
                CalculateTheEstimatedTime();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                try {
                    file_path = getPath(this,uri);
                    file_path_text.setText(file_path);
                    CalculateTheEstimatedTime();
                    //Toast.makeText(this, "文件路径："+uri.getPath().toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void CalculateTheEstimatedTime () {
        if(file_path != null) {
            File file = new File(file_path);
            double how_much = Math.ceil(file.length()/(sharedPreferences.getInt("code_size",1)));
            double estimated_time = (how_much*(sharedPreferences.getFloat("code_time",(float)0.1)));
            estimated_time_text.setText(String.valueOf(estimated_time)+" S\n"+String.valueOf(estimated_time/60)+" MIN\n"+String.valueOf(estimated_time/3600)+" H");

        }
    }


    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
}
