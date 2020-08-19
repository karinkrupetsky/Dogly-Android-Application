package com.hit.project.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;

public class DogData implements Parcelable
{
    private int yearsAge;
    private int monthsAge;
    private String type;
    private String color;
    private DogSize size;

    public DogData() {
        // default Constructor for Firebase deserialization..
    }

    public DogData(int yearsAge, int monthsAge, String type, String color, DogSize size) {
        this.yearsAge=yearsAge;
        this.monthsAge = monthsAge;
        this.type = type;
        this.color = color;
        this.size = size;
    }

    public int getYearsAge() {
        return yearsAge;
    }

    public void setYearsAge(int yearsAge) {
        this.yearsAge = yearsAge;
    }

    public int getMonthsAge() {
        return monthsAge;
    }

    public void setMonthsAge(int monthsAge) {
        this.monthsAge = monthsAge;
    }

    public String getBreed() {
        return type;
    }

    public void setBreed(String breed) {
        this.type = breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public DogSize getSize() {
        return size;
    }

    public void setSize(DogSize size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Parcelable implementation..
    protected DogData(Parcel in) {
        yearsAge = in.readInt();
        monthsAge = in.readInt();
        type = in.readString();
        color = in.readString();
        size = DogSize.valueOf(in.readString());
    }

    public static final Creator<DogData> CREATOR = new Creator<DogData>() {
        @Override
        public DogData createFromParcel(Parcel in) {
            return new DogData(in);
        }

        @Override
        public DogData[] newArray(int size) {
            return new DogData[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(yearsAge);
        dest.writeInt(monthsAge);
        dest.writeString(type);
        dest.writeString(color);
        dest.writeString(size.name());
    }


    public static enum DogSize {
        SMALL, NORMAL, BIG;  // Names must not be changed due to parceling, but their order can be.
    }
}
