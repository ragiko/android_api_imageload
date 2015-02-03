package com.example.apiimages;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static String tiqavSearchUrl = "http://api.tiqav.com/search.json?";
	private ImageView imageView1;
	private ImageView imageView2;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		imageLoader = new ImageLoader(this);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		imageView2 = (ImageView) findViewById(R.id.imageView2);

		HttpGetTask task = new HttpGetTask(
			MainActivity.this,
			tiqavSearchUrl + "q=test",

			// タスク完了時に呼ばれるUIのハンドラ
			new HttpGetHandler() {

				@Override
				public void onGetCompleted(String response) {
					// JSONをパース
					ArrayList<String> items = new ArrayList<String>();
					try {
						JSONArray result = new JSONArray(response);
						for (int i = 0; i < result.length(); i++) {
							JSONObject item = result.getJSONObject(i);
							// {"id":"5i","ext":"jpg","height":186,"width":251,"source_url":"http://mar.2chan.net/jun/b/src/1287236672399.jpg"}
							String url = item.getString("id");
							items.add(url);
						}

						// imageviewにurlから画像をset
						Log.d("test", "http://img.tiqav.com/"+items.get(0)+".th.jp");
						imageLoader.DisplayImage("http://img.tiqav.com/"+items.get(0)+".th.jpg", imageView1);
						imageLoader.DisplayImage("http://img.tiqav.com/"+items.get(1)+".th.jpg", imageView2);

					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(
								getApplicationContext(),
								"エラーが発生しました。",
								Toast.LENGTH_LONG
								).show();
					}
				}

				@Override
				public void onGetFailed(String response) {
					Toast.makeText(
							getApplicationContext(),
							"エラーが発生しました。",
							Toast.LENGTH_LONG
							).show();
				}
			}
		);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
