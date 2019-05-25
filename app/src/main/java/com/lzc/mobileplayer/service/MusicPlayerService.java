package com.lzc.mobileplayer.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import com.lzc.mobileplayer.IMusicPlayerService;
import com.lzc.mobileplayer.domain.MediaItem;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service {

    private ArrayList<MediaItem> mMediaItems;
    private int position;

    /**
     * 用于播放音乐
     */
    private MediaPlayer mediaPlayer;

    /**
     * 当前播放的音频文件对象
     */
    private MediaItem mediaItem;

    @Override
    public void onCreate() {
        super.onCreate();

        //加载音乐列表
        getDataFromLocal();
    }

    private void getDataFromLocal() {
        mMediaItems = new ArrayList<>();

        //用子线程加载数据
        new Thread() {
            @Override
            public void run() {
                super.run();

                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//视频的文件大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST,//歌曲的演唱者
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();

                        String name = cursor.getString(0);
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);

                        //                        //设置视频的第一秒为封面
                        //                        media.setDataSource(data);// videoPath 本地视频的路径
                        //                        Bitmap bitmap  = media.getFrameAtTime(10, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                        mMediaItems.add(mediaItem);
                    }
                    cursor.close();
                }

            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {

        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCuurrentPosition() throws RemoteException {
            return service.getCuurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMode(int playmode) throws RemoteException {
            service.setPlayMode(playmode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }
    };


    /**
     * 根据位置打开对应的音频文件
     * @param position
     */
    private void openAudio(int position){

        this.position = position;
        if(mMediaItems != null && mMediaItems.size()>0){
            mediaItem = mMediaItems.get(position);

            if(mediaPlayer != null){
                mediaPlayer.release();
                mediaPlayer.reset();
            }

            try {
                mediaPlayer = new MediaPlayer();
                //设置监听
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        next();
                        return true;
                    }
                });
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(MusicPlayerService.this,"没有数据",Toast.LENGTH_SHORT).show();
        }
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        /**
         * Called when the media file is ready for playback.
         *
         * @param mp the MediaPlayer that is ready for playback
         */
        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param mp the MediaPlayer that reached the end of the file
         */
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }
    /**
     * 播放音乐
     */
    private void start(){

        mediaPlayer.start();
    }

    /**
     *  暂停音乐
     */
    private void pause(){

        mediaPlayer.pause();
    }

    /**
     * 停止
     */
    private void stop(){

    }

    /**
     * 得到当前播放进度
     * @return
     */
    private int getCuurrentPosition(){
        return 0;
    }

    /**
     * 德奥当前音频的总时长
     * @return
     */
    private int getDuration(){
        return 0;
    }

    /**
     * 得到艺术家
     * @return
     */
    private String getArtist(){
        return null;
    }


    /**
     * 得到名字
     * @return
     */
    private String getName(){
        return null;
    }

    /**
     * 得到路径
     * @return
     */
    private String getAudioPath(){
        return null;
    }

    /**
     * 播放下一个
     */
    private void next(){

    }

    /**
     * 播放上一个
     */
    private void pre(){

    }

    /**
     * 设置播放模式
     */
    private void setPlayMode(int playmode){

    }

    /**
     * 得到模式
     * @return
     */
    private int getPlayMode(){

        return 0;
    }


}
