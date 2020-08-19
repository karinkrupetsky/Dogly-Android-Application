package com.hit.project.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hit.project.R;
import com.hit.project.service.MyFirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;



public class NotificationsFragment extends PreferenceFragmentCompat
{
    private PrefState lastState;
    private String fcmToken = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lastState = getState();
        MyFirebaseMessagingService.requestFCMToken()
                .addOnSuccessListener(instanceIdResult -> {
                    fcmToken = instanceIdResult.getToken();
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PrefState currentState = getState();
        if(!currentState.equals(lastState)) {
            if(currentState.isEnabled) {
                createOrUpdateNotification(currentState.dogType, currentState.dogAge, currentState.district);
            } else {
                removeNotification();
            }
        }
    }

    private void removeNotification() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null)
            return;

        String userId = user.getUid();
        CollectionReference notifRequestDBRef = db.collection("notif_req");
        notifRequestDBRef.whereEqualTo("uid", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                });
    }

    private void createOrUpdateNotification(String dogType, int dogAge, String district) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user == null || fcmToken == null) {
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("fcmToken", fcmToken);
        data.put("uid", user.getUid());
        data.put("areaName", district);
        data.put("dogType", dogType == null ? "non" : dogType);
        data.put("maxAge", dogAge);
        String userId = user.getUid();
        CollectionReference notifRequestDBRef = db.collection("notif_req");
        notifRequestDBRef.document(userId)
                .set(data)
                .addOnFailureListener(exception -> {
                    exception.printStackTrace();
                    rollbackPrefState(lastState);
                });
    }

    private void rollbackPrefState(PrefState lastState) {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit()
                .putBoolean("parentSwitch", lastState.isEnabled)
                .putString("dogType", lastState.dogType)
                .putString("district", lastState.district)
                .putInt("dogAge", lastState.dogAge)
                .apply();
    }

    private PrefState getState() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean parentSwitch = preferences.getBoolean("parentSwitch", false);
        String dogType = preferences.getString("dogType", null);
        int age = preferences.getInt("dogAge", 10);
        String district = preferences.getString("district", null);

        final String defaultType = getResources().getStringArray(R.array.dog_type_list)[0];

        if(dogType != null && dogType.equals(defaultType))
            dogType = null;

        return new PrefState(parentSwitch, dogType, age, district);
    }

    private static class PrefState {
        private boolean isEnabled;
        private String dogType;
        private int dogAge;
        private String district;


        public PrefState(boolean isEnabled, String dogType, int dogAge, String district) {
            this.isEnabled = isEnabled;
            this.dogType = dogType;
            this.dogAge = dogAge;
            this.district = district;


        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || obj.getClass() != this.getClass())
                return false;

            if(this == obj)
                return true;

            PrefState other = (PrefState)obj;
            return isEnabled == other.isEnabled && dogAge == other.dogAge
                    && (dogType == other.dogType  || dogType != null && dogType.equals(other.dogType))
                    && (district == other.district  || district != null && district.equals(other.district));
        }
    }
}
