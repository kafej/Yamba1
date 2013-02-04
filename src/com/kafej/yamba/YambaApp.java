package com.kafej.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class YambaApp extends Application implements
		OnSharedPreferenceChangeListener {
	static final String TAG = "YambaApp";
	public static final String ACTION_NEW_STATUS = "com.kafej.yamba.NEW_STATUS";
	public static final String ACTION_REFRESH = "com.kafej.yambaRefreshService";
	public static final String ACTION_REFRESH_ALARM = "com.kafej.yambaRefreshALARM";
	private Twitter twitter;
	SharedPreferences prefs;
	StatusData statusData;

	@Override
	public void onCreate() {
		super.onCreate();

		// Prefs stufcio
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		statusData = new StatusData(this);

		Log.d(TAG, "onCreated");
	}

	public Twitter getTwitter() {
		if (twitter == null) {
			// Twitter stufcio
			String username = prefs.getString("username", "");
			String password = prefs.getString("password", "");
			String server = prefs.getString("server", "");
			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(server);
		}
		return twitter;
	}

	static final Intent refreshAlarm = new Intent(ACTION_REFRESH_ALARM);
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		twitter = null;
		sendBroadcast(refreshAlarm);
		Log.d(TAG, "onSharedPreferenceChanged for key: " + key);
	}

	long lastTimestampSeen = -1;

	public int pullAndInsert() {
		int count = 0;
		long biggestTimestampSeen = -1;
		try {
			List<Status> timeline = getTwitter().getPublicTimeline();

			for (Status status : timeline) {
				statusData.insert(status);
//				if (biggestTimestampSeen == -1)
//					biggestTimestampSeen = status.createdAt.getTime();
				if (status.createdAt.getTime() > this.lastTimestampSeen) {
					count++;
					biggestTimestampSeen = (status.createdAt.getTime() > biggestTimestampSeen) ? status.createdAt
							.getTime() : biggestTimestampSeen;
					Log.d(TAG, String.format("%s: %s", status.user.name,
							status.text));
				}
			}
		} catch (TwitterException e) {
			Log.e(TAG, "Failed to pull timeline", e);
		}

		if (count > 0) {
			sendBroadcast(new Intent(ACTION_NEW_STATUS)
					.putExtra("count", count));
		}
		this.lastTimestampSeen = biggestTimestampSeen;
		return count;
	}
}
