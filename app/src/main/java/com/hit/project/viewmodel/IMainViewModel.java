package com.hit.project.viewmodel;


import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.Consumer;

import java.util.List;

import androidx.lifecycle.LiveData;


public interface IMainViewModel {
    public LiveData<List<AdoptionItemData>> getAdsLiveData();
    public void deleteAd(AdoptionItemData adoptionItemData);
    public void searchAndRun(Consumer<List<AdoptionItemData>> consumerList, String district, String type, int maxYear, String freeText);
    public void searchMyAdsAndRun(Consumer<List<AdoptionItemData>> consumerList, String currentUserId);
    public void loadAds(Runnable onFinish);
}
