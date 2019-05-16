package com.lzc.mobileplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzc.mobileplayer.R;
import com.lzc.mobileplayer.domain.MediaItem;

import java.util.ArrayList;

public class NetVideoPagerAdapter extends BaseAdapter {

    private Context context;
    private final ArrayList<MediaItem> mediaItems;


    public NetVideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems){
        this.context = context;
        this.mediaItems = mediaItems;

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;

    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder;
        if(convertView ==null){
            convertView = View.inflate(context, R.layout.item_netvideo_pager,null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHoder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);

            convertView.setTag(viewHoder);
        }else{
            viewHoder = (ViewHoder) convertView.getTag();
        }

        //根据position得到列表中对应位置的数据
        MediaItem mediaItem = mediaItems.get(position);

//        //设置视频的第一秒为封面
//        media.setDataSource(mediaItem.getData());// videoPath 本地视频的路径
//        bitmap  = media.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//        //压缩图片
//        bitmap = compressBitmap(bitmap,256);

        //viewHoder.iv_icon.setImageBitmap(bitmap);
        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_desc.setText(mediaItem.getDesc());


        //使用xUtiles3请求图片
        //x.image().bind(viewHoder.iv_icon,mediaItem.getImageUrl());

        //使用Glide请求图片
        Glide.with(context)
                .load(mediaItem.getImageUrl())
                .into(viewHoder.iv_icon);

        return convertView;
    }


    static class ViewHoder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }


}