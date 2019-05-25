package com.lzc.mobileplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzc.mobileplayer.R;
import com.lzc.mobileplayer.activity.AudioPlayerActivity;
import com.lzc.mobileplayer.adapter.VideoPagerAdapter;
import com.lzc.mobileplayer.base.BasePager;
import com.lzc.mobileplayer.domain.MediaItem;
import com.lzc.mobileplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/816:53
 * version: 1.0
 * 作用   ：本地音乐的页面
 */
public class AudioPager extends BasePager {
    private ListView mListView;
    private TextView tv_nomedia;
    private ProgressBar pd_loading;

    private VideoPagerAdapter videoPagerAdapter;
    private ArrayList<MediaItem> mMediaItems;

    public AudioPager(Context context) {
        super(context);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMediaItems != null && mMediaItems.size() > 0) {
                //有数据
                //设置适配器
                videoPagerAdapter = new VideoPagerAdapter(context, mMediaItems,false);
                mListView.setAdapter(videoPagerAdapter);
                //把文本隐藏
                tv_nomedia.setVisibility(View.GONE);
            } else {
                //没有数据
                //文本显示
                tv_nomedia.setVisibility(View.VISIBLE);
                tv_nomedia.setText("没有发现音频...");
            }

            //progressBar都隐藏
            pd_loading.setVisibility(View.GONE);
        }
    };


    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        mListView = (ListView) view.findViewById(R.id.list_view);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pd_loading = (ProgressBar) view.findViewById(R.id.pd_loading);

        //设置listView的Item的点击事件
        mListView.setOnItemClickListener(new AudioPager.MyOnItemClickListener());

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频的数据被初始化");
        //加载本地视频数据
        getDataFromLocal();
    }

    /**
     * 从本地的sdcard得到数据
     * 1.遍历sdcrad，根据后缀名
     * 2.从内容提供者里面获取
     * 3.如果是6.0以上，需要加上安卓权限
     */
    private void getDataFromLocal() {

        mMediaItems = new ArrayList<>();

        //用子线程加载数据
        new Thread() {
            @Override
            public void run() {
                super.run();

                //设置动态权限
                isGrantExternalRW((Activity) context);
                //SystemClock.sleep(500);

                ContentResolver resolver = context.getContentResolver();
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

                //发消息
                //handler
                handler.sendEmptyMessage(10);
            }
        }.start();
    }


    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            //3.传递列表数据，如果是对象，需要序列化
            Intent intent = new Intent(context,AudioPlayerActivity.class);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }
}
