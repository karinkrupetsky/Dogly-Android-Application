package com.hit.project.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@IgnoreExtraProperties  // For Firebase deserialization
@Entity(tableName = AdoptionItemData.TABLE_NAME)
public class AdoptionItemData
{
    public static final String TABLE_NAME = "ads";

    @PrimaryKey
    @NonNull
    private String id; // Document id (EXCLUDED)

    private String postedUserId; // User document id which is also the authentication id.
    private String postedUserName;
    private String postedUserPhoneNumber;
    private String title;
    private String text;
    private String city;
    private String district;

    private List<String> imagesURL;  // dog pics

    @Embedded
    private DogData dogData;

    @ServerTimestamp
    private Date updateDate;  // update (also created) date - from Firebase

    private boolean isRemoved;   // Soft remove - indicate that this document is removed..



    public AdoptionItemData() {
        // Default constructor for Firebase deserialization..
    }

    public AdoptionItemData(String postedUserId, String userName, String phoneNumber,
                            String title, String text, String city,
                            String district, List<String> imagesURL, DogData dogData) {
        this.postedUserId = postedUserId;
        this.dogData = dogData;
        this.title = title;
        this.text = text;
        this.city = city;
        this.district = district;
        this.imagesURL = imagesURL;
        isRemoved = false;
        this.postedUserName = userName;
        this.postedUserPhoneNumber = phoneNumber;
    }

    public <T extends AdoptionItemData> T withId(String id) {
        this.id = id;
        return (T)this;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getPostedUserId() {
        return postedUserId;
    }

    public void setPostedUserId(String postedUserId) {
        this.postedUserId = postedUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public List<String> getImagesURL() {
        return imagesURL;
    }

    public void setImagesURL(List<String> imagesURL) {
        this.imagesURL = imagesURL;
    }

    public DogData getDogData() {
        return dogData;
    }

    public void setDogData(DogData dogData) {
        this.dogData = dogData;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public String getPostedUserName() {
        return postedUserName;
    }

    public void setPostedUserName(String postedUserName) {
        this.postedUserName = postedUserName;
    }

    public String getPostedUserPhoneNumber() {
        return postedUserPhoneNumber;
    }

    public void setPostedUserPhoneNumber(String postedUserPhoneNumber) {
        this.postedUserPhoneNumber = postedUserPhoneNumber;
    }
}
