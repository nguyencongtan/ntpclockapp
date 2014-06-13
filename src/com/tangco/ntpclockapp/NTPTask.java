package com.tangco.ntpclockapp;

import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.Toast;

import com.tangco.ntpclockapp.utils.Constants;
import com.tangco.ntpclockapp.utils.SntpClient;

@SuppressLint("ShowToast")
public class NTPTask extends AsyncTask<Void, Void, Boolean> {

	private SntpClient m_client;
	private Activity m_activity;

	public NTPTask(SntpClient client, Activity activity) {
		this.m_client = client;
		this.m_activity = activity;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (m_client.requestTime(Constants.NTP_HOST,
				Constants.SYNC_TIME_OUT_MILS)) {
			return true;
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean isSynced) {
		if (isSynced) {
			final long now = m_client.getNtpTime()
					+ SystemClock.elapsedRealtime()
					- m_client.getNtpTimeReference();
			final MainActivity mainActivity = (MainActivity) m_activity;
			mainActivity.setTime(now);
			mainActivity.showTimeSync();
			// Show message Sync completed
			Toast.makeText(mainActivity, "Sync Completed", 1000).show();
			//Schedule the timers
			scheduleTimerTasks(mainActivity.getTimerTaskTimeSync(),
					mainActivity, Constants.UPDATE_CLOCK_MILS);
			TimerTaskTimeSyncLeft timerLeft = mainActivity
					.getTimerTaskTimeSyncLeft();

			if (timerLeft.getCountdownTimer() != null) {
				timerLeft.cancelCountDownClock();
			}
			((MainActivity) m_activity).resetTimeLeft();
			mainActivity.getTimerTaskTimeSyncLeft().triggerCountDownTimer();
		}
	}

	private void scheduleTimerTasks(TimerTask timerTask, MainActivity activity,
			long interval) {
		if (timerTask instanceof TimerTaskTimeSync) {
			if (timerTask.scheduledExecutionTime() != 0) {
				timerTask.cancel();
				activity.allocateTimerTaskTimeSync();
			}
			activity.getTimer().scheduleAtFixedRate(
					activity.getTimerTaskTimeSync(), 0, interval);
		}

	}
}
