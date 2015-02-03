package com.example.apiimages;

import android.os.Handler;
import android.os.Message;

/**
 * HTTP通信のGETタスク完�?時に?���?�信の成否に応じて?��受信した通信�?容をUI上で取り扱�?ための抽象クラス�?
 *
 */
public abstract class HttpGetHandler extends Handler {
	// こ�?�メソ�?ド�?��?ぺ�?し，Messageなどの低レベルオブジェクトを
	  // 直接扱わな�?でもよ�?ようにさせ�?
	  public void handleMessage(Message msg)
	  {
	    boolean isGetSuccess = msg.getData().getBoolean("http_get_success");
	    String http_response = msg.getData().get("http_response").toString();

	    if( isGetSuccess )
	    {
	      onGetCompleted( http_response );
	    }
	    else
	    {
	      onGetFailed( http_response );
	    }
	  }


	  // 下記をoverrideさせずに抽象化した理由は?��本クラス�?定時に
	  // 「実�?されて�?な�?メソ�?ド�?�追�?」でメソ�?ドスタブを楽に自動生成させるため�?
	  // また，異常系の処�?フローも真剣にコー�?ィングさせるため�??


	  // 通信成功時�?�処�?を記述させる�??
	  // 名前をonPostSuccessではなくonPostCompletedにした�?由は?�?
	  // メソ�?ド�?�動生成時に正常系が�?��?�に来るよ�?にするため�?
	  public abstract void onGetCompleted( String response );

	  // 通信失敗時の処�?を記述させ�?
	  public abstract void onGetFailed( String response );
}
