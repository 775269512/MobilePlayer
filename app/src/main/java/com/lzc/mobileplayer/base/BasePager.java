package com.lzc.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/816:43
 * version: 1.0
 *
 * 作用：公共类
 *  VideoPager
 *  AudioPager
 *  NetVideoPager
 *  NetAudioPager
 *      继承BasePager
 */
public abstract class BasePager {
    /**
     *  上下文
     */
    public final Context context;
    public View rootview;
    public boolean isInitData;

    public BasePager(Context context){
        this.context = context;
        rootview = initView();
    }

    /**
     * 强制让孩子实现特定的效果
     * @return
     */
    public abstract View initView();

    /**
     * 当子页面需要初始化数据
     *  联网请求数据或者绑定数据的时候，重写该方法
     */
    public void initData(){

    }
}
