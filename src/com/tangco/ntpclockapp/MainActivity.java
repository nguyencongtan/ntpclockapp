package com.tangco.ntpclockapp;

import java.util.Date;
import java.util.Timer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tangco.ntpclockapp.utils.CommonUtils;
import com.tangco.ntpclockapp.utils.Constants;
import com.tangco.ntpclockapp.utils.SntpClient;

public class MainActivity extends ActionBarActivity {

	private TextView m_txtSyncTime;
	private TextView m_txtSyncTimeLeft;
	private Button m_sync;
	private AlertDialog.Builder m_dialogBuilder;
	private AlertDialog m_alertDialog;
	private long m_time;
	private long m_timeLeft = Constants.SYNC_TIME_LEFT_MILS;
	private Timer m_timer;
	private Date m_dcurrentDateTime;
	private TimerTaskTimeSync timerTaskTimeSync;
	private TimerTaskTimeSyncLeft timerTaskTimeSyncLeft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		setTxtSyncTime((TextView) findViewById(R.id.clock));
		setTxtSyncTimeLeft((TextView) findViewById(R.id.syncTimeLeft));
		setTimer(new Timer());
		setTimerTaskTimeSync(new TimerTaskTimeSync(this));
		setTimerTaskTimeSyncLeft(new TimerTaskTimeSyncLeft(this));
		m_dcurrentDateTime = new Date();
		m_sync = (Button) findViewById(R.id.sync);
		allocateTimerTaskTimeSync();
		m_sync.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new NTPTask(SntpClient.getInstance(), MainActivity.this)
						.execute();
			}
		});
		if (!CommonUtils.isNetAvailable(getApplicationContext())) {
			handleRetryConnectionDialog();
		} else {
			new NTPTask(SntpClient.getInstance(), this).execute();
		}
	}

	public void allocateTimerTaskTimeSync() {
		timerTaskTimeSync = new TimerTaskTimeSync(this);
	}

	public void allocateTimerTaskTimeSyncLeft() {
		timerTaskTimeSyncLeft = new TimerTaskTimeSyncLeft(this);
	}

	public void resetTimeLeft() {
		setTimeLeft(Constants.SYNC_TIME_LEFT_MILS);
	}

	private void handleRetryConnectionDialog() {
		if (m_dialogBuilder == null) {
			m_dialogBuilder = new AlertDialog.Builder(this);
			m_dialogBuilder.setMessage("No connection");
			m_dialogBuilder.setCancelable(true);
			m_dialogBuilder.setNegativeButton("Exit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							MainActivity.this.finish();
						}
					});
			m_dialogBuilder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
			m_alertDialog = m_dialogBuilder.create();
			m_alertDialog.show();
			Button theButton = m_alertDialog
					.getButton(DialogInterface.BUTTON_POSITIVE);
			theButton
					.setOnClickListener(new RetryButtonListener(m_alertDialog));

		} else {
			m_alertDialog.show();
		}

	}

	public void showTimeSync() {
		m_dcurrentDateTime.setTime(getTimeSync());
		CommonUtils.fmt.applyPattern(CommonUtils.DATE_TIME_PATTERN);
		getTxtSyncTime().setText(CommonUtils.fmt.format(m_dcurrentDateTime));
	}

	public void showTimeSyncLeft() {
		CommonUtils.fmt.applyPattern(CommonUtils.DATE_TIME_PATTERN_2);
		getTxtSyncTimeLeft().setText(
				CommonUtils.fmt.format(new Date(getTimeSyncLeft())));
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

	@Override
	public void onBackPressed() {
		finish();
	}

	public TextView getSyncTime() {
		return getTxtSyncTime();
	}

	public void updateSyncTimeLeft() {
		setTimeLeft(getTimeLeft() - 1000);
	}

	public void updateTimeSync() {
		setTime(getTime() + 1000);
	}

	public long getTimeSyncLeft() {
		return getTimeLeft();
	}

	public long getTimeSync() {
		return getTime();
	}

	public long getTime() {
		return m_time;
	}

	public void setTime(long time) {
		this.m_time = time;
	}

	public TextView getTxtSyncTime() {
		return m_txtSyncTime;
	}

	public void setTxtSyncTime(TextView m_txtSyncTime) {
		this.m_txtSyncTime = m_txtSyncTime;
	}

	public TextView getTxtSyncTimeLeft() {
		return m_txtSyncTimeLeft;
	}

	public void setTxtSyncTimeLeft(TextView txtSyncTimeLeft) {
		this.m_txtSyncTimeLeft = txtSyncTimeLeft;
	}

	public Timer getTimer() {
		return m_timer;
	}

	public void setTimer(Timer m_timer) {
		this.m_timer = m_timer;
	}

	public TimerTaskTimeSync getTimerTaskTimeSync() {
		return timerTaskTimeSync;
	}

	public void setTimerTaskTimeSync(TimerTaskTimeSync timerTaskTimeSync) {
		this.timerTaskTimeSync = timerTaskTimeSync;
	}

	public TimerTaskTimeSyncLeft getTimerTaskTimeSyncLeft() {
		return timerTaskTimeSyncLeft;
	}

	public void setTimerTaskTimeSyncLeft(
			TimerTaskTimeSyncLeft timerTaskTimeSyncLeft) {
		this.timerTaskTimeSyncLeft = timerTaskTimeSyncLeft;
	}

	public long getTimeLeft() {
		return m_timeLeft;
	}

	public void setTimeLeft(long m_timeLeft) {
		this.m_timeLeft = m_timeLeft;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	class RetryButtonListener implements View.OnClickListener {
		private final Dialog dialog;

		public RetryButtonListener(Dialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onClick(View v) {
			if (CommonUtils.isNetAvailable(getApplicationContext())) {
				dialog.dismiss();
				new NTPTask(new SntpClient(), MainActivity.this).execute();
			}
		}
	}

}
