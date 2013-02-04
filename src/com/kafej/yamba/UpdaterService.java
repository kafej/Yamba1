package com.kafej.yamba;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	static final String TAG = "UpdaterService";
	static final int DELAY = 30;
	boolean running = false;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		running = true;

		new Thread() {
			public void run() {
				try {
					while (running) {
						((YambaApp) getApplication()).pullAndInsert();

						int delay = Integer
								.parseInt(((YambaApp) getApplication()).prefs
										.getString("delay", "30"));
						Thread.sleep(delay * 1000);
					}
				} catch (InterruptedException e) {
					Log.d(TAG, "Updater interrupted", e);
				}
			}
		}.start();

		Log.d(TAG, "onStarted");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		running = false;
		Log.d(TAG, "onDestroyed");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
