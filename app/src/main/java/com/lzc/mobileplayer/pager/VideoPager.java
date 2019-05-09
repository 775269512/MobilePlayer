package com.lzc.mobileplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzc.mobileplayer.R;
import com.lzc.mobileplayer.base.BasePager;
import com.lzc.mobileplayer.domain.Mediaitem;
import com.lzc.mobileplayer.utils.LogUtil;

import java.util.ArrayList;


/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/816:53
 * version: 1.0
 * 作用   ：本地视频的页面
 */
public class VideoPager extends BasePager {

    private ListView mListView;
    private TextView tv_nomedia;
    private ProgressBar pd_loading;

    private ArrayList<Mediaitem> mediaitems;

    public VideoPager(Context context) {
        super(context);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaitems != null && mediaitems.size() >0){
                //有数据
                //设置适配器
//                videoPagerAdapter = new VideoPagerAdapter(context,mediaitems,true);
//                mListView.setAdapter(videoPagerAdapter);
                //把文本隐藏
                tv_nomedia.setVisibility(View.GONE);
            }else{
                //没有数据
                //文本显示
                tv_nomedia.setVisibility(View.VISIBLE);
            }

            //progressBar都隐藏
            pd_loading.setVisibility(View.GONE);
        }
    };


    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager,null);
        mListView = (ListView) view.findViewById(R.id.list_view);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        pd_loading = (ProgressBar) view.findViewById(R.id.pd_loading);
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

        mediaitems = new ArrayList<>();

        //用子线程加载数据
        new Thread(){
            @Override
            public void run() {
                super.run();

                //设置动态权限
                isGrantExternalRW((Activity) context);
                SystemClock.sleep(2000);

                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//视频的文件大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST,//歌曲的演唱者

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor !=null){
                    while (cursor.moveToNext()){
                        Mediaitem mediaitem = new Mediaitem();

                        String name = cursor.getString(0);
                        mediaitem.setName(name);

                        long duration = cursor.getLong(1);
                        mediaitem.setDuration(duration);

                        long size = cursor.getLong(2);
                        mediaitem.setSize(size);

                        String data = cursor.getString(3);
                        mediaitem.setData(data);

                        String artist = cursor.getString(4);
                        mediaitem.setArtist(artist);

                        mediaitems.add(mediaitem);
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
}
