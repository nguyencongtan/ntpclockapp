package com.tangco.ntpclockapp;

import java.util.TimerTask;

import android.app.Activity;

public class TimerTaskTimeSync extends TimerTask {

	private Activity m_activity;

	public TimerTaskTimeSync(Activity activity) {
		this.m_activity = activity;
	}

	@Override
	public void run() {
		m_activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (m_activity instanceof MainActivity) {
					((MainActivity) m_activity).updateTimeSync();
					((MainActivity) m_activity).showTimeSync();
				}

			}
		});

	}

}
