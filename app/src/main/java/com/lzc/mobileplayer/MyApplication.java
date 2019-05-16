package com.lzc.mobileplayer;

import android.app.Application;

import org.xutils.x;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/1617:16
 * version: 1.0
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
