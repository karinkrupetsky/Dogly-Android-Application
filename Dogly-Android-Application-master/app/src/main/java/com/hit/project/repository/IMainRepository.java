package com.hit.project.repository;


import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.Consumer;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface IMainRepository {
    public void loadAds(Runnable onFinish);
    public LiveData<List<AdoptionItemData>> getAdsLiveData();
    public void deleteAd(AdoptionItemData adoptionItemData, Runnable onFinish);
    public void searchAndRun(Consumer<List<AdoptionItemData>> consumerList, String district, String type, int maxYear, String freeText);
    public void searchMyAdsAndRun(Consumer<List<AdoptionItemData>> consumerList, String currentUserId);
}
