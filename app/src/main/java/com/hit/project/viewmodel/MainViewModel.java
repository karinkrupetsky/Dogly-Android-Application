package com.hit.project.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.Consumer;
import com.hit.project.repository.IMainRepository;
import com.hit.project.repository.MainRepository;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MainViewModel extends AndroidViewModel implements IMainViewModel
{
    private IMainRepository mainRepository;
    private LiveData<List<AdoptionItemData>> adoptionLiveData;


    public MainViewModel(@NonNull Application application) {
        super(application);
        mainRepository = new MainRepository(application);
        adoptionLiveData = mainRepository.getAdsLiveData();
    }

    @Override
    public LiveData<List<AdoptionItemData>> getAdsLiveData() {
        return adoptionLiveData;
    }

    @Override
    public void deleteAd(AdoptionItemData adoptionItemData){
        mainRepository.deleteAd(adoptionItemData, () -> {
            LocalBroadcastManager.getInstance(getApplication().getApplicationContext())
                    .sendBroadcast(new Intent("com.hit.project.ACTION_RELOAD"));
        });
    }

    @Override
    public void searchAndRun(Consumer<List<AdoptionItemData>> consumerList, String district, String type, int maxYear, String freeText) {
        mainRepository.searchAndRun(consumerList, district, type, maxYear, freeText);
    }

    @Override
    public void searchMyAdsAndRun(Consumer<List<AdoptionItemData>> consumerList, String currentUserId) {
        mainRepository.searchMyAdsAndRun(consumerList, currentUserId);
    }

    @Override
    public void loadAds(Runnable onFinish) {
        mainRepository.loadAds(onFinish);
    }
}
