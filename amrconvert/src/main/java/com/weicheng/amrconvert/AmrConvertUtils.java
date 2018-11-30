package com.weicheng.amrconvert;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class AmrConvertUtils {
    private static final String TAG = "AmrConvertUtils";

    static {
        System.loadLibrary("amrconvert");
    }

    /**
     *
     * @param src amr的绝对路径
     * @param dest mp3的绝对路经
     * @param tmp pcm的绝对路径
     * @return -1是失败，0是成功
     */
    private static native int amr2Mp3(String src, String dest, String tmp);

    /**
     *
     * @param src mp3的绝对路经
     * @param dest amr的绝对路径
     * @param tmp pcm的绝对路径
     * @return -1是失败，0是成功
     */
    private static native int mp32Amr(String src, String dest, String tmp);

    /**
     *
     * @param context 上下文对象
     * @param src amr的绝对路径
     * @param dest mp3的绝对路径
     * @return -1是失败，0是成功
     */

    private static native int mp32MultAmr(String src, String dest, String tmp);

    /**
     *
     * @param context 上下文对象
     * @param src amr的绝对路径
     * @param dest mp3的绝对路径
     * @return -1是失败，0是成功
     */

    public static boolean amr2Mp3(Context context, String src, String dest) {

        String tmp = "/data/data/" + context.getPackageName() + File.separator + "t.t";
        File f = new File(tmp);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                Log.v(TAG, "createNewFile mp32Amr :" + e.toString());
                return false;
            }
        }
        if (f.exists()) {
            int ret = amr2Mp3(src, dest, tmp);
            f.delete();
            return ret == 0;
        }

        return false;
    }

    /**
     *
     * @param context 上下文对象
     * @param src mp3的绝对路径
     * @param dest amr的绝对路径
     * @return -1是失败，0是成功
     */

    public static boolean mp32Amr(Context context, String src, String dest) {
        String tmp = "/data/data/" + context.getPackageName() + File.separator + "t1.t";
        File f = new File(tmp);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                Log.v(TAG, "createNewFile mp32Amr :" + e.toString());
                return false;
            }
        }
        if (f.exists()) {
            int ret = mp32Amr(src, dest, tmp);
            f.delete();
            return ret == 0;
        }
        return false;
    }

    public static boolean mp32MultAmr(Context context, String src, String dest) {
        String tmp = "/data/data/" + context.getPackageName() + File.separator + "t0.pcm";
        File f = new File(tmp);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                Log.v(TAG, "createNewFile mp32MultAmr :" + e.toString());
                return false;
            }
        }
        if (f.exists()) {
            int ret = mp32MultAmr(src, dest, tmp);
            //f.delete();
            return ret == 0;
        }
        return false;
    }
}
