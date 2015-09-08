package com.lei.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.lei.model.Book;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by renlei
 * DATE: 14-12-29
 * Time: 下午3:52
 * Email: lei.ren@renren-inc.com
 */
public class BookUtil {

    /**
     * 请求某个url上的图片资源
     * @param bmurl
     * @return
     */
    public Bitmap downLoadBitmap(String bmurl)
    {
        Bitmap bm=null;
        InputStream is =null;
        BufferedInputStream bis=null;
        try{
            URL url=new URL(bmurl);
            URLConnection connection=url.openConnection();
            bis=new BufferedInputStream(connection.getInputStream());
            //将请求返回的字节流编码成Bitmap
            bm= BitmapFactory.decodeStream(bis);
        }catch (Exception e){
            e.printStackTrace();
        }
        //关闭IO流
        finally {
            try {
                if(bis!=null)
                    bis.close();
                if (is!=null)
                    is.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return bm;
    }

    /**
     * 解析图书JSON数据,把解析的数据封装在一个Book对象中
     * @param str
     * @return
     */
    public Book parseBookInfo(String str)
    {
        Book info=new Book();
        try{
            //先从String得到一个JSONObject对象
            JSONObject mess=new JSONObject(str);
            info.setId(mess.getString("id"));
            info.setTitle(mess.getString("title"));
            info.setBitmap(downLoadBitmap(mess.getJSONObject("images").getString("large")));
            info.setAuthor(parseAuthor(mess.getJSONArray("author")));
            info.setPublisher(mess.getString("publisher"));
            info.setPublishDate(mess.getString("pubdate"));
            info.setISBN(mess.getString("isbn13"));
            info.setSummary(mess.getString("summary"));
            info.setAuthorInfo(mess.getString("author_intro"));
            info.setPage(mess.getString("pages"));
            info.setPrice(mess.getString("price"));
            info.setContent(mess.getString("catalog"));
            info.setRate(mess.getJSONObject("rating").getString("average"));
            info.setTag(parseTags(mess.getJSONArray("tags")));
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return info;
    }


    /**
     * 针对豆瓣图书的特殊信息，抽出一个parseTags方法解析图书标签信息
     * @param obj
     * @return
     */
    public String parseTags (JSONArray obj)
    {
        StringBuffer str =new StringBuffer();
        for(int i=0;i<obj.length();i++)
        {
            try{
                str=str.append(obj.getJSONObject(i).getString("name")).append(" ");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return str.toString();
    }


    /**
     * 针对豆瓣图书的特殊信息，抽出一个parseAuthor方法解析作者信息
     * @param arr
     * @return
     */
    public String parseAuthor (JSONArray arr)
    {
        StringBuffer str =new StringBuffer();
        for(int i=0;i<arr.length();i++)
        {
            try{
                str=str.append(arr.getString(i)).append(" ");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return str.toString();
    }


    /**
     * 对url的地址发送get方式的Http请求
     * @param url
     * @return
     */
    public static String getHttpRequest(String url)
    {
        String content = "";
        try {
            URL getUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();
            // 取得输入流，并使用Reader读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines="";
            while ((lines = reader.readLine()) != null) {
                content+=lines;
            }
            reader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    /**
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
