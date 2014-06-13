package com.tangco.ntpclockapp;

import android.app.Activity;
import android.os.CountDownTimer;

import com.tangco.ntpclockapp.utils.Constants;
import com.tangco.ntpclockapp.utils.SntpClient;

public class TimerTaskTimeSyncLeft {

	private Activity m_activity;
	private CountDownTimer m_countdownTimer;

	public TimerTaskTimeSyncLeft(Activity activity) {
		this.m_activity = activity;
	}

	public void triggerCountDownTimer() {
		setCountdownTimer(new CountDownTimer(Constants.SYNC_TIME_LEFT_MILS,
				1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				m_activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (m_activity instanceof MainActivity) {
							((MainActivity) m_activity).updateSyncTimeLeft();
							((MainActivity) m_activity).showTimeSyncLeft();
						}

					}
				});
			}

			@Override
			public void onFinish() {
				new NTPTask(SntpClient.getInstance(), m_activity).execute();
			}
		}.start());
	}

	public void cancelCountDownClock() {
		getCountdownTimer().cancel();
	}

	public CountDownTimer getCountdownTimer() {
		return m_countdownTimer;
	}

	public void setCountdownTimer(CountDownTimer m_countdownTimer) {
		this.m_countdownTimer = m_countdownTimer;
	}

}
