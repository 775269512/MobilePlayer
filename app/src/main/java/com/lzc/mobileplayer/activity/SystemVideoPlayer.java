package com.lzc.mobileplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;
import android.widget.VideoView;

import com.lzc.mobileplayer.R;
import com.lzc.mobileplayer.utils.LogUtil;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/922:57
 * version: 1.0
 * 作用： 系统播放器
 */
public class SystemVideoPlayer extends Activity {
    private VideoView videoview;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("onCreate--");
        setContentView(R.layout.activity_system_video_player);

        videoview = (VideoView) findViewById(R.id.videoview);

        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        //播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        //得到播放地址
        uri = getIntent().getData();
        if(uri != null){
            videoview.setVideoURI(uri);
        }


        //设置系统控制面板
        //videoview.setMediaController(new MediaController(this));
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //当底层界面准备好啦
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoview.start();//播放开始
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
            //1.播放的视频格式不支持--跳转到万能播放器继续播放
            //startVitamioPlayer();
            //2.播放网络视频的时候，网络中断---1.如果网络确实断了，可以提示用于网络断了；2.网络断断续续的，重新播放
            //3.播放的时候本地文件中间有空白---下载做完成
            return false;
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //playNextVideo();
            Toast.makeText(SystemVideoPlayer.this, "播放完成了="+uri, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart--");

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart--");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume--");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onRestart--");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop--");
    }

    @Override
    protected void onDestroy() {
        LogUtil.e("onDestroy--");
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            
        }
        return super.onTouchEvent(event);
    }
}
