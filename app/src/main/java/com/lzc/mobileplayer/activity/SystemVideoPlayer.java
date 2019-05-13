package com.lzc.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzc.mobileplayer.R;
import com.lzc.mobileplayer.domain.MediaItem;
import com.lzc.mobileplayer.utils.LogUtil;
import com.lzc.mobileplayer.utils.Utils;
import com.lzc.mobileplayer.view.VideoView;

import java.util.ArrayList;
import java.util.Date;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/922:57
 * version: 1.0
 * 作用： 系统播放器
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    //视频进度的更新
    private static final int PROGRESS = 1;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLLER = 2;

    private VideoView videoview;
    private Uri uri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btVoice;
    private SeekBar seekbarVoice;
    private Button btnSwichPlayer;
    private LinearLayout llBottom;
    private RelativeLayout media_controller;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSiwchScreen;

    private Utils mUtils;
    //监听电量变化的广播
    private MyReceiver receiver;
    /**
     * 传进来的视频列表
     */
    private ArrayList<MediaItem> mediaItems;
    /**
     * 要播放的具体位置
     */
    private int position;
    /**
     * 定义手势识别器
     */
    private GestureDetector detector;
    /**
     *  是否显示控制面板
     */
    private boolean isShowMediaController = false;
    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;
    /**
     * 全屏
     */
    private static final int FULL_SCREEN = 1;
    /**
     * 默认屏幕
     */
    private static final int DEFAULT_SCREEN = 2;
    /**
     * 屏幕的大小
     */
    private int screenWidth = 0;
    private int screenHeight = 0;
    /**
     * 真实视频的宽和高
     */
    private int videoWidth = 0 ;
    private int videoHeight = 0;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-05-11 22:32:56 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);

        media_controller = (RelativeLayout) findViewById(R.id.media_controller);
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
        btVoice = (Button)findViewById( R.id.bt_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        btnSwichPlayer = (Button)findViewById( R.id.btn_swich_player );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSiwchScreen = (Button)findViewById( R.id.btn_video_siwch_screen );

        btVoice.setOnClickListener( this );
        btnSwichPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-05-11 22:32:56 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btVoice ) {
            // Handle clicks for btVoice
        } else if ( v == btnSwichPlayer ) {
            // Handle clicks for btnSwichPlayer
        } else if ( v == btnExit ) {
            // Handle clicks for btnExit
            finish();
        } else if ( v == btnVideoPre ) {
            // Handle clicks for btnVideoPre
            playPreVideo();
        } else if ( v == btnVideoStartPause ) {
            // Handle clicks for btnVideoStartPause
            //播放暂停的代码
            startAndPause();
        } else if ( v == btnVideoNext ) {
            // Handle clicks for btnVideoNext
            playNextVideo();

        } else if ( v == btnVideoSiwchScreen ) {
            // Handle clicks for btnVideoSiwchScreen
            setFullScreenAndDefault();
        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
    }

    /**
     * 播放和暂停
     */
    private void startAndPause() {
        if(videoview.isPlaying()){
            //视频在播放，设置为暂停
            videoview.pause();
            //按钮状态要设置为播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        }else {
            //视频的播放
            videoview.start();
            //按钮状态设置为暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if(mediaItems != null&&mediaItems.size() > 0){
            //播放上一个
            position--;
            if(position>=0){
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if (uri != null){
            //上一个和下一个按钮设置为灰色不可以点击
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if(mediaItems != null&&mediaItems.size() > 0){
            //播放下一个
            position++;
            if(position<mediaItems.size()){
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if (uri != null){
            //上一个和下一个按钮设置为灰色不可以点击
            setButtonState();
        }
    }

    private void setButtonState() {
        if(mediaItems!=null&&mediaItems.size()>0){
            if(mediaItems.size()==1){

                //两个按钮为灰色
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPre.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);
            }else if(mediaItems.size()==2){
                if(position==0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                }else if (position==mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                }
            }else {
                if(position==0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                }else if (position==mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                }else {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                }
            }

        }else if (uri!=null){
            //两个按钮为灰色
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }
    }

    private Handler handler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case PROGRESS:

                    //1.得到当前的播放视频进度
                    int currentPosition = videoview.getCurrentPosition();
                    //2.seekBar.setProgress(当前进度);
                    seekbarVideo.setProgress(currentPosition);

                    //更新文本播放进度
                    tvCurrentTime.setText(mUtils.stringForTime(currentPosition));

                    //设置系统时间
                    tvSystemTime.setText(getSysteTime());

                    //3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS,1000);

                    break;
                case HIDE_MEDIACONTROLLER://隐藏控制面板
                    hideMediaController();
                    break;
            }
        }
    };

    /**
     * 得到系统时间
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getSysteTime() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(new Date());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//初始化父类
        LogUtil.e("onCreate--");

        findViews();
        initData();
        setListener();

        getData();
        setData();

        //设置系统控制面板
        //videoview.setMediaController(new MediaController(this));
    }

    private void setData() {
        if(mediaItems != null && mediaItems.size()>0){

            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());//设置视频的名称
            videoview.setVideoPath(mediaItem.getData());

        }else if (uri != null){
            tvName.setText(uri.toString());//设置视频的名称
            videoview.setVideoURI(uri);

        }else {
            Toast.makeText(this, "视频没有数据", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void getData() {
        //得到播放地址
        uri = getIntent().getData();

        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    private void initData() {
        mUtils = new Utils();

        //注册电量的广播
        receiver = new MyReceiver();
        final IntentFilter intentFiler = new IntentFilter();
        //当电量变化的时候发广播
        intentFiler.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver,intentFiler);

        //实例化手势识别器
        //并且重写双击，单击，长按
        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startAndPause();
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //单击屏幕显示和隐藏
                if (isShowMediaController){
                    hideMediaController();
                    //把隐藏消息移除
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                }else {
                    showMediaController();
                    //发消息隐藏
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setFullScreenAndDefault();
                return super.onDoubleTap(e);
            }
        });

        //得到屏幕的宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight =displayMetrics.heightPixels;
    }

    private void setFullScreenAndDefault() {
        if (isFullScreen){
            //默认
            setVideoTpye(DEFAULT_SCREEN);
        }else {
            //全屏
            setVideoTpye(FULL_SCREEN);
        }
    }

    private void setVideoTpye(int defaultScreen) {
        switch (defaultScreen){
            case FULL_SCREEN:   //全屏
                //1.设置视频画面大小
                videoview.setVideoSize(screenWidth,screenHeight);
                //2.设置按钮的状态
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN:    //默认
                //1.设置视频画面大小
                //视频真正的高和宽
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;


                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;
                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width,height);
                //2.设置按钮的状态
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                isFullScreen = false;
                break;
        }
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level",0);

            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if(level <= 0){
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if (level <= 10){
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if (level <= 20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if (level <= 40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if (level <= 60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if (level <= 80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if (level <= 100){
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        //播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        //设置seekBar状态变化监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        /**
         * 当手指滑动的时候会引起这个方法的变化
         * @param seekBar
         * @param progress
         * @param fromUser  如果是用户引起的，是true，否则不响应
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (fromUser){
                videoview.seekTo(progress);
            }
        }

        /**
         * 当手指触碰的时候回调这个方法
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        /**
         * 当手指离开的时候回调这个方法
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //当底层界面准备好啦
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            videoview.start();//播放开始

            //1视频的总时间，关联总长度
            int duration = videoview.getDuration();//获得总时长
            tvDuration.setText(mUtils.stringForTime(duration));
            seekbarVideo.setMax(duration);

            //默认隐藏控制面板
            hideMediaController();

            //2发消息
            handler.sendEmptyMessage(PROGRESS);

            //设置视频的大小
//            videoview.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());
            //屏幕的默认播放
            setVideoTpye(DEFAULT_SCREEN);
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
            playNextVideo();
            //Toast.makeText(SystemVideoPlayer.this, "播放完成了="+uri, Toast.LENGTH_SHORT).show();
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
        //先释放子类，再释放父类
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }

        LogUtil.e("onDestroy--");
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 显示控制面板
     */
    private void showMediaController(){
        media_controller.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }
    /**
     * 隐藏控制面板
     */
    private void hideMediaController(){
        media_controller.setVisibility(View.GONE);
        isShowMediaController = false;
    }
}
