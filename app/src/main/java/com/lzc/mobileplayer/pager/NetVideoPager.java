package com.lzc.mobileplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lzc.mobileplayer.base.BasePager;
import com.lzc.mobileplayer.utils.LogUtil;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/816:53
 * version: 1.0
 * 作用   ：网络视频的页面
 */
public class NetVideoPager extends BasePager {
    private TextView mTextView;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络视频页面初始化了");
        mTextView = new TextView(context);
        mTextView.setTextSize(25);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.RED);
        return mTextView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频的数据被初始化");
        mTextView.setText("网络视频页面");
    }
}
