package com.hit.project.db;

import android.content.Context;

import com.hit.project.model.AdoptionItemData;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {AdoptionItemData.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase
{
    private static final String DATABASE_NAME = "appDB";
    //The instance reference is volatile..
    private static volatile AppDatabase instance;


    public static AppDatabase getInstance(Context context) {
        if(instance == null) {
            synchronized (AppDatabase.class) {
                if(instance == null) {
                    //NOTE! Must use application context..
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()  // remove all data when upgrade DB version..
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract AdDAO getAdDAO();
}
