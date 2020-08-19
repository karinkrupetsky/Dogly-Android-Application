package com.hit.project.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;


import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hit.project.R;

import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.DogData;
import com.hit.project.ui.SplashScreenActivity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class UploadPostService extends Service
{
    private static final int ID = 1;
    private StorageReference imagesRef;
    private static boolean isRunning = false;


    @Override
    public void onCreate() {
        isRunning = true;
        super.onCreate();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReference().child("images");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(ID, createNotification());

        ArrayList<Uri> images = intent.getParcelableArrayListExtra("images");
        DogData dogData = (DogData)intent.getParcelableExtra("dogData");
        String userId = intent.getStringExtra("userId");
        String title = intent.getStringExtra("title");
        String city = intent.getStringExtra("city");
        String district = intent.getStringExtra("district");
        String freeText = intent.getStringExtra("freeText");
        String userName = intent.getStringExtra("userName");
        String userPhone = intent.getStringExtra("phoneNumber");

        final AdoptionItemData data = new AdoptionItemData(userId, userName, userPhone, title, freeText, city, district, null, dogData);
        if(images == null || images.size() == 0) {
            postAd(data);
            return Service.START_NOT_STICKY;
        }

        // else - upload photos first..
        final List<UrlData> urlsList =  Collections.synchronizedList(new LinkedList<>());
        for(int i=0; i<images.size(); i++) {
            final StorageReference photoRef = imagesRef.child(String.valueOf(System.currentTimeMillis()));
            final UrlData url = new UrlData(i);
            UploadTask uploadTask = photoRef.putFile(images.get(i));
            //upload the img + perform a task of getting the download url from the cloud
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    task.getException().printStackTrace();
                    return null;
                }
                return photoRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String downloadUrl = task.getResult().toString();
                    url.url = downloadUrl;
                    urlsList.add(url);
                    if(urlsList.size() == images.size()) {
                        // all images were uploaded..

                        // sort the list by original elements' position
                        Collections.sort(urlsList, (e1, e2) -> {
                             return Integer.compare(e1.position, e2.position);
                        });

                        data.setImagesURL(mappedURLs(urlsList));
                        postAd(data);
                    }
                } else {
                    showMessageAndFinish(getString(R.string.post_ad_fail));
                }
            });
        }
        return Service.START_NOT_STICKY;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    private List<String> mappedURLs(List<UrlData> urlsList) {
        List<String> result = new ArrayList<>();
        for(UrlData data : urlsList)
            result.add(data.url);

        return result;
    }

    private void postAd(AdoptionItemData adData) {
        FirebaseFirestore.getInstance().collection("ads")
            .add(adData)
            .addOnSuccessListener(docRef -> {
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(new Intent("com.hit.project.ACTION_RELOAD"));
                stopSelf();
            })
            .addOnFailureListener(ex -> {
                ex.printStackTrace();
                showMessageAndFinish(getString(R.string.post_ad_fail));
            });
    }

    private void showMessageAndFinish(String msg) {
        try {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            stopSelf();
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    private Notification createNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.hit.project.service.UploadNotifID";
        if(Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getResources().getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription(getResources().getString(R.string.channel_name));
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);

        // Create notification action intent..
        Intent notificationIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(getString(R.string.posting_title))
                .setContentText(getString(R.string.posting_text))
                .setContentIntent(pi);

        return notificationBuilder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private static class UrlData {
        private String url;
        private int position;

        public UrlData(int pos) {
            position = pos;
        }
    }
}
