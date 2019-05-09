package com.lzc.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lzc.mobileplayer.R;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/913:54
 * version: 1.0
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {

    private View tv_search;
    private View rl_game;
    private View iv_record;

    private Context context;

    /**
     * 在代码中实例该类时使用该类的方法
     * @param context
     */
    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defstyleAttr) {
        super(context, attrs, defstyleAttr);
        this.context = context;
    }

    /**
     * 当布局文件加载完成，回掉
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子的实例对象
        tv_search = getChildAt(1);
        rl_game = getChildAt(2);
        iv_record = getChildAt(3);

        //设置点击事件
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search://搜索
                Toast.makeText(context,"搜索",Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game://游戏
                Toast.makeText(context,"游戏",Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record://播放历史
                Toast.makeText(context,"播放历史",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
