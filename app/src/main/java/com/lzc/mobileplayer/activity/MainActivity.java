package com.lzc.mobileplayer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.lzc.mobileplayer.R;
import com.lzc.mobileplayer.base.BasePager;
import com.lzc.mobileplayer.pager.AudioPager;
import com.lzc.mobileplayer.pager.NetAudioPager;
import com.lzc.mobileplayer.pager.NetVideoPager;
import com.lzc.mobileplayer.pager.ReplaceFragment;
import com.lzc.mobileplayer.pager.VideoPager;

import java.util.ArrayList;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/80:09
 * version: 1.0
 */
public class MainActivity extends FragmentActivity {
    private FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;

    /**
     * 页面的集合
     */
    private ArrayList<BasePager> mBasePagers;

    //选中rg的位置
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);

        mBasePagers = new ArrayList<>();
        mBasePagers.add(new VideoPager(this));//添加本地视频页面-0
        mBasePagers.add(new AudioPager(this));//添加本地音频页面-1
        mBasePagers.add(new NetVideoPager(this));//添加网络视频页面-2
        mBasePagers.add(new NetAudioPager(this));//添加网络音频页面-3

        //设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //默认选中的首页
        rg_bottom_tag.check(R.id.rb_video);
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_audio: //本地音频
                    position = 1;
                    break;
                case R.id.rb_net_video: //网络音频
                    position = 2;
                    break;
                case R.id.rb_net_audio: //网络音频
                    position = 3;
                    break;
                default:            //本地视频
                    position = 0;
                    break;
            }

            setFragment();
        }
    }

    private void setFragment() {
        //1.得到FragmentManger
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换内容
        ft.replace(R.id.fl_main_content,new ReplaceFragment(getBasePager()));
        //4.提交事务
        ft.commit();
    }

    /**
     * 根据位置得到对应的页面
     * @return 返回对应页面
     */
    private BasePager getBasePager() {
        BasePager basePager = mBasePagers.get(position);
        if(basePager != null&&basePager.isInitData==false){
            basePager.initData();//联网请求数据，绑定数据
            basePager.isInitData = true;
        }
        return basePager;
    }
}
