package com.lzc.mobileplayer.utils;
 
import android.graphics.Bitmap;
import android.util.LruCache;
 
public class LruCacheUtils {
	//创建Cache缓存,第一个泛型表示缓存的标识key，第二个泛型表示需要缓存的对象
	private LruCache<String, Bitmap> mCaches;
    public LruCacheUtils() {
    			int maxMemory=(int) Runtime.getRuntime().maxMemory();//获取最大的应用运行时的最大内存
		//通过获得最大的运行时候的内存，合理分配缓存的内存空间大小
		int cacheSize=maxMemory/4;//取最大运行内存的1/4;
		mCaches=new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {//加载正确的内存大小
				return value.getByteCount();//在每次存入缓存的时候调用
			}
 
		};
    }
	//将图片保存在LruCache中
	public void addBitmapToCache(String url,Bitmap bitmap){
		if (getBitmapFromCache(url)==null) {//判断当前的Url对应的Bitmap是否在Lru缓存中，如果不在缓存中，就把当前url对应的Bitmap对象加入Lru缓存
			mCaches.put(url, bitmap);
		}
 
	}
	//将图片从LruCache中读取出来
	public  Bitmap getBitmapFromCache(String url){
		Bitmap bitmap=mCaches.get(url);//实际上LruCache就是一个Map,底层是通过HashMap来实现的
		return bitmap;
	}
}