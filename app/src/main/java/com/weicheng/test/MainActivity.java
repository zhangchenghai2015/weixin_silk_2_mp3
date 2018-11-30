package com.weicheng.test;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.weicheng.amrconvert.AmrConvertUtils;

import java.io.File;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int AMR_TO_MP3_SUCCESS = 0;
    private static final int AMR_TO_MP3_FAIL = 1;
    private static final int AMR_TO_MP3_NO_EXITS = 2;
    private static final int MP3_TO_AMR_SUCCESS = 3;
    private static final int MP3_TO_AMR_FAIL = 4;
    private static final int MP3_TO_AMR_NO_EXITS = 5;
    private static final int MP3_TO_MULT_AMR_SUCCESS = 6;
    private static final int MP3_TO_MULT_AMR_FAIL = 7;
    private static final int MP3_TO_MULT_AMR_NO_EXITS = 8;

    MyHandler mHandler = new MyHandler();
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private EditText mAmrDir;
    private EditText mMp3Dir;
    private EditText mMultAmrMp3Dir;
    private TextView mCreateMp3Dir;
    private TextView mCreateAmrDir;
    private TextView mCreateMultAmrDir;
    private Button mBtnConvertToAmr;
    private Button mBtnConvertToMp3;
    private Button mBtnConvertToMultAmr;
    private String mAmrDirPath;
    private String mMp3DirPath;
    private String mMultAmrDirPath;
    private String mMp3CreateDir;
    private String mAmrCreateDir;
    private ProgressDialog mAmrConvertPdg;
    private ProgressDialog mMp3ConvertPdg;
    private ProgressDialog mMp3ConvertMultAmrPdg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();
        initViews();
    }

    /**
     * 获取设备读写存储空间的权限
     */

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }

    /**
     * 显示转换进度条
     * @return 返回ProgressDialog对象
     */

    private ProgressDialog progressDialog() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("转换");//设置一个标题
        pd.setMessage("正在转换中请等待。。。");//设置消息
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(true);//这是是否可撤销/也就是这个对话框是否可以关闭
        pd.setIndeterminate(false);//设置是否是确定值
        pd.show();
        return pd;
    }

    /**
     * 初始化所有view
     */

    private void initViews() {
        mCreateMp3Dir = (TextView)findViewById(R.id.create_mp3_dir);
        mCreateAmrDir = (TextView)findViewById(R.id.create_amr_dir);
        mCreateMultAmrDir = (TextView)findViewById(R.id.create_mult_amr_dir);
        mAmrDir = (EditText)findViewById(R.id.amr_dir);
        mMp3Dir = (EditText)findViewById(R.id.mp3_dir);
        mMultAmrMp3Dir = (EditText)findViewById(R.id.mp3_to_mult_amr_dir);
        mBtnConvertToAmr = (Button)findViewById(R.id.btn_convert_amr);
        mBtnConvertToAmr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMp3ConvertPdg = progressDialog();
                mMp3DirPath = mMp3Dir.getText().toString();
                mp3ToAmrConvert();
            }
        });
        mBtnConvertToMp3 = (Button)findViewById(R.id.btn_convert_mp3);
        mBtnConvertToMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAmrConvertPdg = progressDialog();
                mAmrDirPath = mAmrDir.getText().toString();
                amrToMp3Convert();

            }
        });

        mBtnConvertToMultAmr = (Button)findViewById(R.id.btn_convert_mult_amr);
        mBtnConvertToMultAmr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMp3ConvertMultAmrPdg = progressDialog();
                mMultAmrDirPath = mMultAmrMp3Dir.getText().toString();
                mp3ConvertMultAmr();

            }
        });
    }

    private void mp3ConvertMultAmr() {
        new Thread( new Runnable() {
            public void run() {
                //mp3转换amr
                String pathMp3 = mMultAmrDirPath;//= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1.mp3";
                File fMp3 = new File(pathMp3);
                Log.v(TAG, "f.exists : " + fMp3.exists());
                String amrDir= mMultAmrDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "convertmultamr";
                File fAmrDir = new File(amrDir);
                if(!fAmrDir.exists())
                    fAmrDir.mkdirs();
                String destAmr = fAmrDir + File.separator  + "weixin.amr";
                long systemSec1 = System.currentTimeMillis();
                boolean retMp3ToAmr = true;
                //for (int i = 0; i <= 100; i++) {
                    if (fMp3.exists() || fMp3.length()>0) {
                        //Log.v("zch", "開始");
                        long start = System.currentTimeMillis();
                        boolean ret = AmrConvertUtils.mp32MultAmr(MainActivity.this, pathMp3, destAmr);
                        long dur = System.currentTimeMillis() - start;
                        //Log.v("zch", "mp3转amr单个文件dur : " + dur);
                        //0表示转换成功 -1 表示失败
                        if (!ret) {
                            retMp3ToAmr = false;
                            mHandler.sendEmptyMessage(MP3_TO_MULT_AMR_FAIL);
                            //break;
                        }
                    } else {
                        retMp3ToAmr = false;
                        mHandler.sendEmptyMessage(MP3_TO_MULT_AMR_NO_EXITS);
                    }

                    //destAmr = amrDir + File.separator + String.valueOf(i) + ".amr";
                //}
                if (retMp3ToAmr) {
                    mHandler.sendEmptyMessage(MP3_TO_MULT_AMR_SUCCESS);
                }
                systemSec1 = System.currentTimeMillis() - systemSec1;
                Log.v(TAG, "mp3转amr的systemSec : " + systemSec1);
            }
        }).start();
    }

    /**
     * mp3转换成amr文件
     */
    private void mp3ToAmrConvert() {
        new Thread( new Runnable() {
            public void run() {
        //mp3转换amr
        String pathMp3 = mMp3DirPath;//= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1.mp3";
        File fMp3 = new File(pathMp3);
        Log.v(TAG, "f.exists : " + fMp3.exists());
        String amrDir= mAmrCreateDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "convertamr";
        File fAmrDir = new File(amrDir);
        if(!fAmrDir.exists())
            fAmrDir.mkdirs();
        String destAmr = fAmrDir + File.separator  + "1.amr";
        long systemSec1 = System.currentTimeMillis();
        boolean retMp3ToAmr = true;
        for (int i = 0; i <= 100; i++) {
            if (fMp3.exists() || fMp3.length()>0) {
                //Log.v("zch", "開始");
                long start = System.currentTimeMillis();
                boolean ret = AmrConvertUtils.mp32Amr(MainActivity.this, pathMp3, destAmr);
                long dur = System.currentTimeMillis() - start;
                //Log.v("zch", "mp3转amr单个文件dur : " + dur);
                //0表示转换成功 -1 表示失败
                if (!ret) {
                    retMp3ToAmr = false;
                    mHandler.sendEmptyMessage(MP3_TO_AMR_FAIL);
                    break;
                }
            } else {
                retMp3ToAmr = false;
                mHandler.sendEmptyMessage(MP3_TO_AMR_NO_EXITS);
            }

            destAmr = amrDir + File.separator + String.valueOf(i) + ".amr";
        }
        if (retMp3ToAmr) {
            mHandler.sendEmptyMessage(MP3_TO_AMR_SUCCESS);
        }
        systemSec1 = System.currentTimeMillis() - systemSec1;
        Log.v(TAG, "mp3转amr的systemSec : " + systemSec1);
            }
        }).start();
    }

    /**
     * amr文件转换成mp3文件
     */
    private void amrToMp3Convert() {
        new Thread( new Runnable() {
            public void run() {
                //amr 转换 mp3
                //String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1.amr";
                File f = new File(mAmrDirPath);
                Log.v(TAG, "f.exists : " + f.exists());
                String mp3Dir= mMp3CreateDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "convertmp3";
                File fmp3Dir = new File(mp3Dir);
                if(!fmp3Dir.exists())
                    fmp3Dir.mkdirs();
                String dest = mp3Dir + File.separator  + "1.mp3";
                long systemSec = System.currentTimeMillis();
                boolean retAmrToMp3 = true;
                for (int i = 0; i <= 100; i++) {
                    if (f.exists() || f.length()>0) {
                        //Log.v("zch", "開始");
                        long start = System.currentTimeMillis();
                        boolean ret = AmrConvertUtils.amr2Mp3(MainActivity.this, mAmrDirPath, dest);
                        long dur = System.currentTimeMillis() - start;
                        //Log.v("zch", "amr转MP3单个文件dur : " + dur);
                        //0表示转换成功 -1 表示失败
                        if (!ret) {
                            retAmrToMp3 = false;
                            mHandler.sendEmptyMessage(AMR_TO_MP3_FAIL);
                            break;
                        }
                    } else {
                        retAmrToMp3 = false;
                        mHandler.sendEmptyMessage(AMR_TO_MP3_NO_EXITS);
                    }

                    dest = mp3Dir + File.separator + String.valueOf(i) + ".mp3";
                }

                if(retAmrToMp3) {
                    mHandler.sendEmptyMessage(AMR_TO_MP3_SUCCESS);
                }
                systemSec = System.currentTimeMillis() - systemSec;
                Log.v(TAG, "systemSec : " + systemSec);



            }
        }).start();
    }

/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.e("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }*/

    public class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case  AMR_TO_MP3_SUCCESS:
                    Toast.makeText(MainActivity.this, "amr转mp3成功", Toast.LENGTH_LONG).show();
                    mCreateMp3Dir.setText(mMp3CreateDir);
                    if(mAmrConvertPdg != null) {
                        mAmrConvertPdg.dismiss();
                        mAmrConvertPdg = null;
                    }
                    break;
                case  AMR_TO_MP3_FAIL:
                    Toast.makeText(MainActivity.this, "amr转mp3失败", Toast.LENGTH_LONG).show();
                    if(mAmrConvertPdg != null) {
                        mAmrConvertPdg.dismiss();
                        mAmrConvertPdg = null;
                    }
                    break;
                case  AMR_TO_MP3_NO_EXITS:
                    Toast.makeText(MainActivity.this, "amr文件不存在或者文件大小为0", Toast.LENGTH_LONG).show();
                    if(mAmrConvertPdg != null) {
                        mAmrConvertPdg.dismiss();
                        mAmrConvertPdg = null;
                    }
                    break;
                case  MP3_TO_AMR_SUCCESS:
                    Toast.makeText(MainActivity.this, "mp3转amr成功", Toast.LENGTH_LONG).show();
                    mCreateAmrDir.setText(mAmrCreateDir);
                    if(mMp3ConvertPdg != null) {
                        mMp3ConvertPdg.dismiss();
                        mMp3ConvertPdg = null;
                    }
                    break;
                case  MP3_TO_AMR_FAIL:
                    Toast.makeText(MainActivity.this, "mp3转amr失败", Toast.LENGTH_LONG).show();
                    if(mMp3ConvertPdg != null) {
                        mMp3ConvertPdg.dismiss();
                        mMp3ConvertPdg = null;
                    }
                    break;
                case  MP3_TO_MULT_AMR_NO_EXITS:
                    Toast.makeText(MainActivity.this, "mp3文件不存在或者文件大小为0", Toast.LENGTH_LONG).show();
                    if(mMp3ConvertPdg != null) {
                        mMp3ConvertPdg.dismiss();
                        mMp3ConvertPdg = null;
                    }
                    break;
                case  MP3_TO_MULT_AMR_SUCCESS:
                    Toast.makeText(MainActivity.this, "mp3转多个amr成功", Toast.LENGTH_LONG).show();
                    mCreateMultAmrDir.setText(mMultAmrDirPath);
                    if(mMp3ConvertMultAmrPdg != null) {
                        mMp3ConvertMultAmrPdg.dismiss();
                        mMp3ConvertMultAmrPdg = null;
                    }
                    break;
                case  MP3_TO_MULT_AMR_FAIL:
                    Toast.makeText(MainActivity.this, "mp3转多个amr失败", Toast.LENGTH_LONG).show();
                    if(mMp3ConvertMultAmrPdg != null) {
                        mMp3ConvertMultAmrPdg.dismiss();
                        mMp3ConvertMultAmrPdg = null;
                    }
                    break;
                case  MP3_TO_AMR_NO_EXITS:
                    Toast.makeText(MainActivity.this, "mp3文件不存在或者文件大小为0", Toast.LENGTH_LONG).show();
                    if(mMp3ConvertMultAmrPdg != null) {
                        mMp3ConvertMultAmrPdg.dismiss();
                        mMp3ConvertMultAmrPdg = null;
                    }
                    break;
            }
        }
    };

}
