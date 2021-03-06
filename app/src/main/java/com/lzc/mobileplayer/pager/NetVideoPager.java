package com.lzc.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzc.mobileplayer.R;
import com.lzc.mobileplayer.activity.SystemVideoPlayer;
import com.lzc.mobileplayer.adapter.NetVideoPagerAdapter;
import com.lzc.mobileplayer.base.BasePager;
import com.lzc.mobileplayer.domain.MediaItem;
import com.lzc.mobileplayer.utils.CacheUtils;
import com.lzc.mobileplayer.utils.Constants;
import com.lzc.mobileplayer.utils.LogUtil;
import com.lzc.mobileplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
/**
 * author : 刘子川
 * e-mail : 775269512@qq.com
 * date   : 2019/5/816:53
 * version: 1.0
 * 作用   ：网络视频的页面
 */
public class NetVideoPager extends BasePager {


    @ViewInject(R.id.list_view)
    private XListView mListview;

    @ViewInject(R.id.tv_nonet)
    private TextView mTv_nonet;

    @ViewInject(R.id.pd_loading)
    private ProgressBar mProgressBar;

    /**
     * 装数据集合
     */
    private ArrayList<MediaItem> mediaItems;

    private NetVideoPagerAdapter adapter;

    /**
     * 是否已经加载更多了
     */
    private boolean isLoadMore = false;


    public NetVideoPager(Context context) {
        super(context);
    }

    /**
     * 初始化当前页面的控件，由父类调用
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netvideo_pager,null);
        //第一个参数是：NetVideoPager.this,第二个参数：布局
        x.view().inject(NetVideoPager.this, view);
        mListview.setOnItemClickListener(new MyOnItemClickListener());
        mListview.setPullLoadEnable(true);
        mListview.setXListViewListener(new MyIXListViewListener());
        return view;
    }

    class MyIXListViewListener implements XListView.IXListViewListener {
        @Override
        public void onRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {

            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
        //联网
        //视频内容
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);
                isLoadMore = true;
                //主线程
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
                isLoadMore = false;
            }
        });
    }


    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //3.传递列表数据-对象-序列化
            Intent intent = new Intent(context,SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position-1);
            context.startActivity(intent);

        }
    }



    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频的数据被初始化了。。。");
        String saveJson = CacheUtils.getString(context,Constants.NET_URL);
        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        getDataFromNet();

    }

    private void getDataFromNet() {
        //联网
        //视频内容
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);
                //缓存数据
                CacheUtils.putString(context,Constants.NET_URL,result);
                //主线程
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败==" + ex.getMessage());
                showData();
                mTv_nonet.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());

            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    private void processData(String json) {

        if(!isLoadMore){
            mediaItems = parseJson(json);
            showData();


        }else{
            //加载更多
            //要把得到更多的数据，添加到原来的集合中
            //            ArrayList<MediaItem> moreDatas = parseJson(json);
            isLoadMore = false;
            mediaItems.addAll(parseJson(json));
            //刷新适配器
            adapter.notifyDataSetChanged();
            onLoad();



        }



    }

    private void showData() {
        //设置适配器
        if(mediaItems != null && mediaItems.size() >0){
            //有数据
            //设置适配器
            adapter = new NetVideoPagerAdapter(context,mediaItems);
            mListview.setAdapter(adapter);
            onLoad();
            //把文本隐藏
            mTv_nonet.setVisibility(View.GONE);
        }else{
            //没有数据
            //文本显示
            mTv_nonet.setVisibility(View.VISIBLE);
        }


        //ProgressBar隐藏
        mProgressBar.setVisibility(View.GONE);
    }


    private void onLoad() {
        mListview.stopRefresh();
        mListview.stopLoadMore();
        mListview.setRefreshTime(getSysteTime());
    }

    /**
     * 得到系统时间
     *
     * @return
     */
    public String getSysteTime() {
        SimpleDateFormat format = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            format = new SimpleDateFormat("HH:mm:ss");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return format.format(new Date());

        }
        return null;
    }
    /**
     * 解决json数据：
     * 1.用系统接口解析json数据
     * 2.使用第三方解决工具（Gson,fastjson）
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();

        //一个黄色数据
        MediaItem media = new MediaItem();
        media.setImageUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec" +
                "=1558270874144&di=b8fb657481bc346828c1414876ebddc4&imgtype=0&src" +
                "=http%3A%2F%2Fs14.sinaimg.cn%2Fmw690%2F005NkUZ8gy6NaoKygYBbd%26690");
        media.setData("https://ddr8v13bs4e1n.cloudfront.net/A185841_S876929.mp4");
        media.setName("别点开！");
        media.setDesc("黄色数据测试，别点！！");
        mediaItems.add(media);

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if(jsonArray!= null && jsonArray.length() >0){

                for (int i=0;i<jsonArray.length();i++){

                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);

                    if(jsonObjectItem != null){

                        MediaItem mediaItem = new MediaItem();


                        String movieName = jsonObjectItem.optString("movieName");//name
                        mediaItem.setName(movieName);

                        String videoTitle = jsonObjectItem.optString("videoTitle");//desc
                        mediaItem.setDesc(videoTitle);

                        String imageUrl = jsonObjectItem.optString("coverImg");//imageUrl
                        mediaItem.setImageUrl(imageUrl);

                        String hightUrl = jsonObjectItem.optString("hightUrl");//data
                        mediaItem.setData(hightUrl);

                        //把数据添加到集合
                        mediaItems.add(mediaItem);
                    }
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mediaItems;
    }
}