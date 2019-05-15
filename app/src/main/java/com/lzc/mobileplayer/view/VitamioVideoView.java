package com.lzc.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.vov.vitamio.widget.VideoView;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/1320:35
 * version: 1.0
 */
public class VitamioVideoView extends VideoView {

    private Context context;

    /**
     * 在代码中创建的时候一般用这个方法
     * @param context
     */
    public VitamioVideoView(Context context) {
        this(context,null);
    }

    /**
     * 当这个类在布局文件中，系统通过该构造方法实例化该类
     * @param context
     * @param attrs
     */
    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当需要设计样式的时候调用该构造器
     * @param context
     * @param attrs
     * @param defstyleAttr
     */
    public VitamioVideoView(Context context, AttributeSet attrs, int defstyleAttr) {
        super(context, attrs, defstyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     * 设置视频的宽和高
     * by 刘子川
     * @param videoWidth    //指定视频的宽
     * @param videoHeight   //指定视频的高
     */
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = videoWidth;
        params.height = videoHeight;
        setLayoutParams(params);
    }
}
