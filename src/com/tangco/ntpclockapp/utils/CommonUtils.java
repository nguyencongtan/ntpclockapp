package com.tangco.ntpclockapp.utils;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

@SuppressLint("SimpleDateFormat")
public class CommonUtils {
	public static final String DATE_TIME_PATTERN = "HH:mm:ss\nMM/dd/yyyy ";
	public static final String DATE_TIME_PATTERN_2 = "mm:ss";
	public static final SimpleDateFormat fmt = new SimpleDateFormat();

	public static Boolean isNetAvailable(Context con) {

		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) con
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobileInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
