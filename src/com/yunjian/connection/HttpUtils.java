package com.yunjian.connection;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

class ResponseData {
	public int connStatus;
	public String jsonString = null;
}

class HttpAsyncTask extends AsyncTask<Object, Object, ResponseData> {
	private HttpUtils.ResponseHandler responseHandler = null;
	private static final String TAG = "HttpAsyncTask";

	public HttpAsyncTask(HttpUtils.ResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
	}

	@Override
	protected ResponseData doInBackground(Object... params) {
		ResponseData responseData = null;
		HttpResponse response = HttpUtils.getResponse((String) params[0],
				(List<BasicNameValuePair>) params[1]);
		if (response != null) {
			responseData = new ResponseData();
			responseData.connStatus = response.getStatusLine().getStatusCode();
			responseData.jsonString = HttpUtils.getJsonFromResponse(response);
		}
		return responseData;
	}

	@Override
	protected void onPostExecute(ResponseData result) {
		String jsonResult = null;
		ResponseData response = result;
		HttpUtils.EHttpError error = HttpUtils.EHttpError.KErrorNone;
		if (responseHandler != null) {
			if (response != null) {
				jsonResult = response.jsonString;
				if (response.connStatus != 200) {
					error = HttpUtils.EHttpError.KErrorConnectionFailed;
				}
			} else {
				error = HttpUtils.EHttpError.KErrorConnectionFailed;
			}
			responseHandler.handleResponse(jsonResult, error);
		}
	}
}

public class HttpUtils {
	private static final String TAG = "HttpUtils";
	private static ConnectionConfigReader configReader = null;

	static {
		configReader = new ConnectionConfigReader();
	}

	/**
	 * 
	 * @author youni
	 * 
	 */
	public enum EHttpError {
		KErrorNone, KErrorConnectionFailed
	}

	/**
	 * 
	 * @author youni
	 * 
	 */
	public interface ResponseHandler {
		public abstract void handleResponse(String jsonResult, EHttpError error);
	}

	/**
	 * 
	 * @param actionName
	 * @param params
	 * @param responseHandler
	 * @return
	 */
	public static EHttpError makeAsyncPost(String actionName,
			List<BasicNameValuePair> params, ResponseHandler responseHandler) {

		HttpAsyncTask asyncCall = new HttpAsyncTask(responseHandler);
		asyncCall.execute(actionName, params);
		return EHttpError.KErrorNone;
	}

	/**
	 * 
	 * @param actionName
	 * @param params
	 * @return
	 */
	public static Object makeSyncPost(String actionName,
			List<BasicNameValuePair> params) {

		HttpResponse response = getResponse(actionName, params);

		if (response == null) {
			return null;
		}

		return getJsonFromResponse(response);
	}

	/**
	 * 
	 * @param actionName
	 * @param params
	 * @return
	 */
	static HttpResponse getResponse(String actionName,
			List<BasicNameValuePair> params) {
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, e.getMessage());
			return null;
		}

		// 创建客户端连接对象
		HttpClient httpClient = HttpUtils.getHttpClient();
		HttpResponse response = null;

		String serverAddress = configReader.getServer();
		HttpPost request = new HttpPost(serverAddress + actionName);
		System.out.println(serverAddress + actionName + params.toString());
		// 设置上传的数据，上传的数据封装到list集合中,并转换为UTF-8字符集
		Log.i(TAG, actionName + params.toString());
		request.addHeader("Accept", "text/json");
		request.setEntity(entity);

		Log.i(TAG, "Try to post request to the server");
		try {
			response = httpClient.execute(request);
		} catch (Exception e) {
			response = null;
			e.printStackTrace();
			// Log.e(TAG, e.getMessage());
		}

		if (response != null) {
			Log.i(TAG,
					"response code from server : "
							+ Integer.toString(response.getStatusLine()
									.getStatusCode()));
		}

		return response;
	}

	/**
	 * 
	 * @param response
	 * @return
	 */
	static String getJsonFromResponse(HttpResponse response) {
		String result = null;

		if (response != null) {
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获取服务器返回信息
				HttpEntity entity = response.getEntity();
				try {
					result = EntityUtils.toString(entity, "UTF-8");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	static private HttpClient getHttpClient() {
		// 设置HttpClient的连接参数
		HttpParams httpParams = new BasicHttpParams();
		// 设置一些基本参数
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(httpParams, true);
		ConnManagerParams.setTimeout(httpParams, 1000);
		/* 连接超时 */
		int ConnectionTimeOut = 10000;
		// if (WiFiStatusUtil.getAPNType(HomePageActivity.context) != 2) {
		// ConnectionTimeOut = 10000;
		// }
		HttpConnectionParams
				.setConnectionTimeout(httpParams, ConnectionTimeOut);
		/* 请求超时 */
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		// 设置我们的HttpClient支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				httpParams, schReg);
		BasicHttpParams params=new BasicHttpParams();
		return new DefaultHttpClient(conMgr, httpParams);
	}
}
