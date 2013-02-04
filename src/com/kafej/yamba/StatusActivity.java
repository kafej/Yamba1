package com.kafej.yamba;

import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity {
	static final String TAG = "StatusActivity";
	EditText editStatus;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Debug.startMethodTracing("Yamba.trace");

		setContentView(R.layout.status);

		editStatus = (EditText) findViewById(R.id.edit_status);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Debug.stopMethodTracing();
	}

	public void onClick(View v) {
		final String statusText = editStatus.getText().toString();

		new PostToTwitter().execute(statusText);

		Log.d("StatusActivity", "onClicked with text: " + statusText);
	}

	class PostToTwitter extends AsyncTask<String, Void, String> {

		/* New Thread */
		@Override
		protected String doInBackground(String... params) {
			try {
				((YambaApp) getApplication()).getTwitter().setStatus(params[0]);
				Log.d(TAG, "Successfully posted: " + params[0]);
				return "Successfully posted: " + params[0];
			} catch (TwitterException e) {
				Log.e(TAG, "Died", e);
				e.printStackTrace();
				return "Failing posting: " + params[0];
			}
		}

		/* UI Thread */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG)
					.show();
		}
	}
}