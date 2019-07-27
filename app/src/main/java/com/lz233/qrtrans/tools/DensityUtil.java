package com.lz233.qrtrans.tools;

import android.content.Context;
//作者：要什么昵称嘛
//来源：CSDN
//原文：https://blog.csdn.net/arui319/article/details/6777133
//版权声明：本文为博主原创文章，转载请附上博文链接！
public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}