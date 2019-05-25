package com.lzc.mobileplayer.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.ImageView;

import com.lzc.mobileplayer.IMusicPlayerService;
import com.lzc.mobileplayer.R;
import com.lzc.mobileplayer.service.MusicPlayerService;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/2312:27
 * version: 1.0
 */
public class AudioPlayerActivity extends Activity {
    private ImageView iv_icon;

    private int position;
    private IMusicPlayerService service;    //服务的代理类，通过它可以调用服务的方法

    /**
     * 针动画
     */
    private AnimationDrawable rocketAnimation;

    /**
     * 绑定连接
     */
    private ServiceConnection con = new ServiceConnection() {
        /**
         * 连接成功的时候回调这个
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if(service!=null){
                try {
                    service.openAudio(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 断开连接的时候回调这个
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if(service!=null){
                    service.stop();
                    service = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        getData();

        bindAndStartService();
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this,MusicPlayerService.class);
        intent.setAction("com.lzc.mobileplayer_OPENAUDIO");
        bindService(intent,con,Context.BIND_ABOVE_CLIENT);
        startService(intent);//这个方法不会启动多次服务
    }

    /**
     * 得到数据
     */
    private void getData() {
        position = getIntent().getIntExtra("position",0);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.activity_audioplayer);

        //设置针动画
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_icon.setBackgroundResource(R.drawable.animation_list);
        rocketAnimation = (AnimationDrawable) iv_icon.getBackground();
        rocketAnimation.start();  //  莫名其妙的bug
    }
}
