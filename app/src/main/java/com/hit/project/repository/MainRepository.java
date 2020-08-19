package com.hit.project.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.hit.project.db.AdDAO;
import com.hit.project.db.AppDatabase;
import com.hit.project.model.AdoptionItemData;
import com.hit.project.model.Consumer;
import com.hit.project.remote.IMainRemoteDataSource;
import com.hit.project.remote.MainRemoteDataSource;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;


public class MainRepository implements IMainRepository
{
    private IMainRemoteDataSource remoteDataSource;
    private AdDAO adDAO;


    public MainRepository(Application application) {
        remoteDataSource = new MainRemoteDataSource();
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        adDAO = db.getAdDAO();
    }

    @Override
    public void loadAds(Runnable onFinish) {
        doAsynch(() -> {
            Date cacheDate = adDAO.findMaxUpdateDate();  // null if no entries
            remoteDataSource.fetchAdoptionAds(cacheDate, result -> {
                if(result == null) {
                    if(onFinish != null)
                        onFinish.run();
                    return;
                }
                doAsynch(() -> {
                    // remove deleted posts from the cache..
                    for(int i = result.size()-1; i>=0; i--) {
                        if(result.get(i).isRemoved()) {
                            adDAO.deleteAd(result.get(i));
                            result.remove(i);
                        }
                    }
                    //insert remaining elements..
                    adDAO.insertAll(result);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if(onFinish != null)
                            onFinish.run();
                    });
                });
            });
        });
    }

    @Override
    public LiveData<List<AdoptionItemData>> getAdsLiveData() {
        return adDAO.getAllAds();
    }

    @Override
    public void deleteAd(AdoptionItemData adoptionItemData, Runnable onFinish){
        //doAsynch(()->adDAO.deleteAd(adoptionItemData));
        remoteDataSource.removeAd(adoptionItemData.getId(), onFinish);
    }

    @Override
    public void searchAndRun(Consumer<List<AdoptionItemData>> consumerList, String district, String type, int maxYear, String freeText) {
        Handler handler = new Handler();
        doAsynch(()->{
            List<AdoptionItemData> data = adDAO.searchAds(district, type, maxYear, freeText);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    consumerList.apply(data);
                }
            });
        });
    }

    @Override
    public void searchMyAdsAndRun(Consumer<List<AdoptionItemData>> consumerList, String currentUserId) {
        Handler handler = new Handler();
        doAsynch(()->{
            List<AdoptionItemData> data = adDAO.getMyAds(currentUserId);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    consumerList.apply(data);
                }
            });
        });
    }

    private void doAsynch(Runnable task) {
        new Thread(task).start();
    }
}
