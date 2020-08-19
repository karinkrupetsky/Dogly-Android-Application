package com.hit.project.db;

import com.hit.project.model.AdoptionItemData;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AdDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAd(AdoptionItemData ad);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAll(List<AdoptionItemData> ads);

    @Delete
    public void deleteAd(AdoptionItemData ad);

    @Delete
    public void deleteAll(List<AdoptionItemData> ads);

    @Update
    public void updateAd(AdoptionItemData ad);

    @Update
    public void updateAll(List<AdoptionItemData> ads);

    @Query("SELECT * FROM "+AdoptionItemData.TABLE_NAME+" WHERE id = :id")
    public AdoptionItemData findById(long id);

    @Query("SELECT * FROM "+AdoptionItemData.TABLE_NAME+" ORDER BY updateDate DESC")
    public LiveData<List<AdoptionItemData>> getAllAds();

    @Query("SELECT * FROM "+AdoptionItemData.TABLE_NAME+" WHERE district LIKE :district " +
            "AND type LIKE :type " + "AND yearsAge <= :maxYear " +
            "AND text LIKE :freeText")
    public List<AdoptionItemData> searchAds(String district, String type, int maxYear, String freeText);

    @Query("SELECT * FROM "+AdoptionItemData.TABLE_NAME+" WHERE postedUserId LIKE :currentUserId " +
            "ORDER BY updateDate DESC")
    public List<AdoptionItemData> getMyAds(String currentUserId);

    @Query("SELECT * FROM " +AdoptionItemData.TABLE_NAME+" WHERE updateDate > :from " +
            "ORDER BY updateDate DESC")
    public List<AdoptionItemData> findFromDate(Date from);

    @Query("SELECT max(updateDate) FROM "+AdoptionItemData.TABLE_NAME)
    public Date findMaxUpdateDate();
}
