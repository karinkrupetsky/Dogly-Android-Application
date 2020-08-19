package com.hit.project.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hit.project.R;
import com.hit.project.ui.MainActivity;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;


public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    public static final String SP_TOKEN_KEY = "newFCMToken";

    // Called when the FCM token is expired or the app was cleaned/reinstalled..
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("!!!", "New Token: "+token);
        updateTokenInFirestore(getApplicationContext(), token);  // Send the new FCM token to the server
    }

    /**
     * Called when receiving a message while foreground.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(11, createNotification());
    }

    /**
     * Check whether user has active notification-requests and update those documents' FCM token.
     */
    public static void updateTokenInFirestore(Context context, String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null) {
            // Keep the new FCM token for later update (after sign-in)
            Log.e("!!!", "Couldn't get user auth id in order to update notifications request FCM token.");
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString("newFCMToken", token)
                    .apply();
            return;
        }

        String userId = user.getUid();
        CollectionReference notifRequestDBRef = db.collection("notif_req");
        notifRequestDBRef.whereEqualTo("uid", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<Object, String> map = new HashMap<>();
                        map.put("fcmToken", token);
                        notifRequestDBRef.document(document.getId()).set(map, SetOptions.merge());
                    }
                });
    }

    private Notification createNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.hit.project.service.FCM_Channel";
        if(Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getResources().getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription(getResources().getString(R.string.channel_name));
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);

        // Create notification action intent..
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(getString(R.string.fcm_notif_title))
                .setContentText(getString(R.string.fcm_notif_text))
                .setContentIntent(pi);

        return notificationBuilder.build();
    }

    public static Task<InstanceIdResult> requestFCMToken() {
        return FirebaseInstanceId.getInstance().getInstanceId();
    }
}
