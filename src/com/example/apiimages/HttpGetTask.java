package com.example.apiimages;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * HTTP通信でGETリクエストを投げる�?��?を非同期で行うタスク�?
 *
 */
public class HttpGetTask extends AsyncTask<Void, Void, Void> {
	// 設定事�??
	  //private String request_encoding = "UTF-8";
	  private String response_encoding = "UTF-8";

	  // 初期化事�??
	  private Activity parent_activity = null;
	  private Service parent_service = null;
	  private String post_url = null;
	  private Handler ui_handler = null;
	  private List<NameValuePair> post_params = null;

	  // 処�?中に使�?メン�?
	  private ResponseHandler<Void> response_handler = null;
	  private String http_err_msg = null;
	  private String http_ret_msg = null;
	  private ProgressDialog dialog = null;
	  private Boolean isActivity = false;	// アク�?ィビティからの呼び出し時のみ�?イアログを表示する


	  // 生�?�時
	  public HttpGetTask( Activity parent_activity, String post_url, Handler ui_handler )
	  {
	    // 初期�?
	    this.parent_activity = parent_activity;
	    this.post_url = post_url;
	    this.ui_handler = ui_handler;
	    isActivity = true;

	    // 送信パラメータは初期化せず，new後にsetさせ�?
	    post_params = new ArrayList<NameValuePair>();
	  }

	  // サービスから使用する用
	  public HttpGetTask( Service parent_service, String post_url, Handler ui_handler )
	  {
	    // 初期�?
	    this.parent_service = parent_service;
	    this.post_url = post_url;
	    this.ui_handler = ui_handler;
	    isActivity = false;

	    // 送信パラメータは初期化せず，new後にsetさせ�?
	    post_params = new ArrayList<NameValuePair>();
	  }

	  /* --------------------- 処�?本�? --------------------- */


	  // タスク開始時
	  protected void onPreExecute() {
	    // �?イアログを表示
		if(isActivity) {
		    dialog = new ProgressDialog( parent_activity );
		    dialog.setMessage("通信中・・・");
		    dialog.show();
		}

	    // レスポンスハンドラを生�?
	    response_handler = new ResponseHandler<Void>() {

	      // HTTPレスポンスから?��受信�?字�?�をエンコードして�?字�?�として返す
	      @Override
	      public Void handleResponse(HttpResponse response) throws IOException
	      {
	        Log.d(
	        "posttest",
	        "レスポンスコード�?" + response.getStatusLine().getStatusCode()
	        );

	        // 正常に受信できた場合�?�200
	        switch (response.getStatusLine().getStatusCode()) {
	        case HttpStatus.SC_OK:
	        Log.d("posttest", "レスポンス取得に成功");

	        // レスポンス�?ータをエンコード済みの�?字�?�として取得する�??
	        // ※IOExceptionの可能性あり
	        HttpGetTask.this.http_ret_msg = EntityUtils.toString(
	          response.getEntity(),
	          HttpGetTask.this.response_encoding
	        );
	        break;

	        case HttpStatus.SC_NOT_FOUND:
	        // 404
	        Log.d("posttest", "存在しな�?");
	        HttpGetTask.this.http_err_msg = "404 Not Found";
	        break;

	        default:
	        Log.d("posttest", "通信エラー");
	        HttpGetTask.this.http_err_msg = "通信エラーが発�?";
	        }

	        return null;
	      }

	    };
	  }

	  // メイン処�?
	  protected Void doInBackground(Void... unused) {

	    Log.d("posttest", "getしま�?");

	    // URL
	    URI url = null;
	    try {
	      url = new URI( post_url );
	      Log.d("gettest", post_url);
	      Log.d("gettest", "URLはOK");
	    } catch (URISyntaxException e) {
	      e.printStackTrace();
	      http_err_msg = "不正なURL";
	      return null;
	    }

	    // GETリクエストを構�?
	    HttpGet request = new HttpGet( url );

	    // GETリクエストを実�?
	    DefaultHttpClient httpClient = new DefaultHttpClient();
	    Log.d("gettest", "GET開�?");
	    try {
	      httpClient.execute(request, response_handler);
	    } catch (ClientProtocolException e) {
	      e.printStackTrace();
	      http_err_msg = "プロトコルのエラー";
	    } catch (IOException e) {
	      e.printStackTrace();
	      http_err_msg = "IOエラー";
	    }

	    // shutdownすると通信できなくな�?
	    httpClient.getConnectionManager().shutdown();

	    return null;
	  }


	  // タスク終�?�?
	  protected void onPostExecute(Void unused) {
		// �?イアログを消す
		if(isActivity) {
			dialog.dismiss();
		}

	    // 受信結果をUIに渡すためにまとめる
	    Message message = new Message();
	    Bundle bundle = new Bundle();
	    if (http_err_msg != null) {
	      // エラー発生時
	      bundle.putBoolean("http_get_success", false);
	      bundle.putString("http_response", http_err_msg);
	    } else {
	      // 通信成功�?
	      bundle.putBoolean("http_get_success", true);
	      bundle.putString("http_response", http_ret_msg);
	    }
	    message.setData(bundle);

	    // 受信結果に基づ�?てUI操作させる
	    ui_handler.sendMessage(message);
	  }
}
