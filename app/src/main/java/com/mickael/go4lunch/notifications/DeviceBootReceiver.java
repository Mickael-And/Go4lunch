package com.mickael.go4lunch.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.mickael.go4lunch.ui.SettingsDialogFragment.NOTIFICATIONS_KEY;
import static com.mickael.go4lunch.ui.SettingsDialogFragment.NOTIFICATIONS_REQUEST_CODE;
import static com.mickael.go4lunch.ui.SettingsDialogFragment.SHARED_PREF_FILE;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATIONS_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (sharedPreferences.getBoolean(NOTIFICATIONS_KEY, true)) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                alarmManager.cancel(pendingIntent);
            }
        }
    }
}
