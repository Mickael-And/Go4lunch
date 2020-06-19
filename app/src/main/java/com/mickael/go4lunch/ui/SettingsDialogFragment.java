package com.mickael.go4lunch.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.notifications.AlarmReceiver;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Dialog box containing the configurations.
 */
public class SettingsDialogFragment extends DialogFragment {

    @BindView(R.id.cb_notifications)
    MaterialCheckBox checkBoxNotifications;

    private SharedPreferences sharedPreferences;

    public static final String SHARED_PREF_FILE = "com.mickael.go4lunch";

    public static final String NOTIFICATIONS_KEY = "notifications_enabled";

    private PendingIntent pendingIntent;

    public static final int NOTIFICATIONS_REQUEST_CODE = 0;

    public static SettingsDialogFragment newInstance() {
        return new SettingsDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);

        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        this.pendingIntent = PendingIntent.getBroadcast(getContext(), NOTIFICATIONS_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_settings, container, false);
        ButterKnife.bind(this, view);
        this.checkBoxNotifications.setChecked(this.sharedPreferences.getBoolean(NOTIFICATIONS_KEY, true));
        this.checkBoxNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putBoolean(NOTIFICATIONS_KEY, isChecked);
            editor.apply();

            // Set up an alarm for sending notifications
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            if (isChecked) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, this.pendingIntent);
            } else {
                alarmManager.cancel(this.pendingIntent);
            }
        });
        return view;
    }
}
