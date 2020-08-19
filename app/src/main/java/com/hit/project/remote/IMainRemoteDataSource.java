package com.hit.project.remote;

import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.Consumer;

import java.util.Date;
import java.util.List;


public interface IMainRemoteDataSource {
    public void fetchAdoptionAds(Date fromDate, Consumer<List<AdoptionItemData>> consumer);
    public void removeAd(String id, Runnable onFinish);
}
