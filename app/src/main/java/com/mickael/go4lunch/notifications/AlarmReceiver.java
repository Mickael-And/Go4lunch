package com.mickael.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsViewModel.KEY_MAP_RESTAURANT_ID;
import static com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsViewModel.KEY_MAP_RESTAURANT_NAME;

/**
 * Class allowing the creation of notifications when an intention is received from an alarm.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_TAG = "GO4LUNCH_NOTIFICATIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        UserFirestoreDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null && user.getLunchRestaurant() != null) {
                String restaurantId = user.getLunchRestaurant().get(KEY_MAP_RESTAURANT_ID);
                UserFirestoreDAO.getUsersCollection().get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        User usr = document.toObject(User.class);
                        if (usr != null && usr.getLunchRestaurant() != null && usr.getLunchRestaurant().get(KEY_MAP_RESTAURANT_ID).equals(restaurantId)) {
                            users.add(usr);
                        }
                    }
                    this.sendVisualNotification(context, user, users);
                });
            } else {
                Log.i(this.getClass().getSimpleName(), "User is null");
            }
        });
    }

    /**
     * Creation of the notification.
     *
     * @param context application context
     * @param user    user receiving notification
     * @param users   users dining with the user
     */
    private void sendVisualNotification(Context context, User user, List<User> users) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification
        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        StringBuilder stringBuilder = new StringBuilder("Vous déjeunez ce midi au ");
        stringBuilder.append(user.getLunchRestaurant().get(KEY_MAP_RESTAURANT_NAME));
        if (users.size() > 1) {
            stringBuilder.append(" avec: ");
            for (User userList : users) {
                if (!userList.getUserId().equals(user.getUserId())) {
                    stringBuilder.append(userList.getUsername());
                    stringBuilder.append(" ");
                }
            }
        }
        inboxStyle.bigText(stringBuilder.toString());

        // 3 - Create a Channel (Android 8)
        String channelId = "007";

        // 4 - Build a Notification object
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_restaurant_white_24dp)
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Channel notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, builder.build());
    }

}
