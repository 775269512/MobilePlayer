package com.lzc.mobileplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
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
    private TextView tv_netspeed;
    private TextView tv_loading_newspeed;
    private LinearLayout ll_loading;
    private LinearLayout ll_buffer;


    /**
     * 调节声音
     */
    private AudioManager am;
    private int currentVoice;
    private int maxVoice;

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
     * 显示网速
     */
    private static final int SHOW_SPEED = 3;
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
     * 是否是静音
     */
    private boolean isMute = false;
    /**
     * 是否是网络资源
     */
    private boolean isNetUri;


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
        ll_buffer = (LinearLayout)findViewById( R.id.ll_buffer );
        ll_loading = (LinearLayout)findViewById( R.id.ll_loading );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
        btVoice = (Button)findViewById( R.id.bt_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        btnSwichPlayer = (Button)findViewById( R.id.btn_swich_player );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        tv_loading_newspeed = (TextView)findViewById( R.id.tv_loading_newspeed );
        tv_netspeed = (TextView)findViewById( R.id.tv_netspeed );
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

        //开始更新网速
        handler.sendEmptyMessage(SHOW_SPEED);

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
            isMute = (!isMute);
            // Handle clicks for btVoice
            updataVoice(currentVoice,isMute);
        } else if ( v == btnSwichPlayer ) {
            // Handle clicks for btnSwichPlayer
            showSwichPlayerDialog();
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

    private void showSwichPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("AV有码？万能播放器！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
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
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = mUtils.isNetUri(mediaItem.getData());
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
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = mUtils.isNetUri(mediaItem.getData());
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
                case SHOW_SPEED:
                    String netSpeed = mUtils.getNetSpeed(SystemVideoPlayer.this);
                    //显示网络速度
                    tv_loading_newspeed.setText("AV玩命加载中.."+netSpeed);
                    tv_netspeed.setText("AV缓冲中.."+netSpeed);
                    //两秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED,2000);

                    break;
                case PROGRESS:

                    //1.得到当前的播放视频进度
                    int currentPosition = videoview.getCurrentPosition();
                    //2.seekBar.setProgress(当前进度);
                    seekbarVideo.setProgress(currentPosition);

                    //更新文本播放进度
                    tvCurrentTime.setText(mUtils.stringForTime(currentPosition));

                    //设置系统时间
                    tvSystemTime.setText(getSysteTime());

                    //缓冲进度的更新
                    if(isNetUri){
                        //网络缓冲
                        int buffer = videoview.getBufferPercentage();
                        int totalBuffer = seekbarVideo.getMax()*buffer;
                        int secondaryProgress = totalBuffer/100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    }else {
                        //本地没缓冲
                        seekbarVideo.setSecondaryProgress(0);
                    }

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
            isNetUri = mUtils.isNetUri(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());

        }else if (uri != null){
            tvName.setText(uri.toString());//设置视频的名称
            isNetUri = mUtils.isNetUri(uri.toString());
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

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //设置最大音量
        seekbarVoice.setMax(maxVoice);
        //设置当前音量
        seekbarVoice.setProgress(currentVoice);

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

        //设置音乐seekBar状态变化监听
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

        //监听视频卡 - 系统的api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoview.setOnInfoListener(new MyOnInfoListener());
        }
    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener{

        /**
         * Called to indicate an info or a warning.
         *
         * @param mp    the MediaPlayer the info pertains to.
         * @param what  the type of info or warning.

         * @param extra an extra code, specific to the info. Typically
         *              implementation dependent.
         * @return True if the method handled the info, false if it didn't.
         * Returning false, or not having an OnInfoListener at all, will
         * cause the info to be discarded.
         */
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                if(progress>0){
                    isMute = false;
                }else {
                    isMute = true;
                }
                updataVoice(progress,isMute);
            }
        }

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
        }
    }

    /**
     * 设置音量大小
     * @param progress
     */
    private void updataVoice(int progress,boolean isMute) {
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
            currentVoice = progress;
        }else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }
    }

    /**
     * 设置音量大小
     * @param progress
     */
    private void updataVoice2(int progress,boolean isMute) {
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 1);
            seekbarVoice.setProgress(0);
            currentVoice = progress;
        }else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 1);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }
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

            //消失加载页面
            ll_loading.setVisibility(View.GONE);

//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//
//                }
//            });
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
            //1.播放的视频格式不支持--跳转到万能播放器继续播放
            startVitamioPlayer();
            //2.播放网络视频的时候，网络中断---1.如果网络确实断了，可以提示用于网络断了；2.网络断断续续的，重新播放
            //3.播放的时候本地文件中间有空白---下载做完成
            return true;
        }
    }

    /**
     * 把数据原封不动的传入Vitamio
     */
    private void startVitamioPlayer() {
        if (videoview!=null){
            videoview.stopPlayback();
        }

        Intent intent = new Intent(this,VitamioVideoPlayer.class);

        if(mediaItems != null && mediaItems.size()>0){
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
        }else if (uri != null){
            intent.setData(uri);
        }
        startActivity(intent);

        finish();//关闭页面
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

    /**
     * 开始位置
     */
    private float startv;
    /**
     * 滑动距离
     */
    private float touchRang;
    /**
     * 当前音量
     */
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:   //手指按下
                //1.记录当前值
                startv = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight,screenWidth);
                handler.removeMessages(HIDE_MEDIACONTROLLER);

                break;
            case MotionEvent.ACTION_MOVE:   //手指移动

                float endY = event.getY();
                float distanceY = startv - endY;
                float delta = (distanceY/touchRang)*maxVoice;
                int voice = (int) Math.min(Math.max(0,mVol+delta),maxVoice);
                if(delta!=0){
                    updataVoice2(voice,false);
                }
                break;
            case MotionEvent.ACTION_UP:     //手指离开
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,1000);
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 监听物理键，实现声音的调节大小
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updataVoice2(currentVoice,false);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            updataVoice(currentVoice,false);
            return true;
        }

        return super.onKeyDown(keyCode, event);
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
