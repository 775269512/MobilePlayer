package com.lzc.mobileplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzc.mobileplayer.R;
import com.lzc.mobileplayer.domain.MediaItem;
import com.lzc.mobileplayer.utils.Utils;

import java.util.ArrayList;

public class VideoPagerAdapter extends BaseAdapter {

    private Context context;
    private final ArrayList<MediaItem> mediaItems;
    private Utils utils;
    private MediaMetadataRetriever media;
    private Bitmap bitmap;


    public VideoPagerAdapter(Context context,ArrayList<MediaItem> mediaItems){
        this.context = context;
        this.mediaItems = mediaItems;
        utils = new Utils();
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
            convertView = View.inflate(context, R.layout.item_video_pager,null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHoder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHoder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);

            convertView.setTag(viewHoder);
        }else{
            viewHoder = (ViewHoder) convertView.getTag();
        }

        //根据position得到列表中对应位置的数据
        MediaItem mediaItem = mediaItems.get(position);

        //设置视频的第一秒为封面
        media = new MediaMetadataRetriever();
        media.setDataSource(mediaItem.getData());// videoPath 本地视频的路径
        bitmap  = media.getFrameAtTime(10, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        viewHoder.iv_icon.setImageBitmap(bitmap);
        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
        viewHoder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));

        return convertView;
    }


    static class ViewHoder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }

}