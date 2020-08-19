package com.hit.project.remote;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.Consumer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainRemoteDataSource implements IMainRemoteDataSource
{
    private FirebaseFirestore db;

    public MainRemoteDataSource() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void fetchAdoptionAds(Date fromDate, Consumer<List<AdoptionItemData>> consumer) {

        Query query = db.collection("ads").orderBy("updateDate", Query.Direction.DESCENDING);

        if(fromDate != null)
            query = query.whereGreaterThan("updateDate", fromDate);

        query.get()
             .addOnCompleteListener(task -> {
                 List<AdoptionItemData> data = null;
                 if (task.isSuccessful()) {
                    data = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        data.add(document.toObject(AdoptionItemData.class).withId(document.getId()));
                    }
                } else {
                    task.getException().printStackTrace();
                }

                 if(consumer != null)
                     consumer.apply(data);
             });
    }

    @Override
    public void removeAd(String id, Runnable onFinish) {
        Map<String, Object> data = new HashMap<>();
        data.put("updateDate", FieldValue.serverTimestamp());
        data.put("removed", true);
        db.collection("ads").document(id).set(data, SetOptions.merge())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(onFinish != null)
                        onFinish.run();
                }
            });
    }
}
