package com.tech2020.fwupdater.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = DeviceFirmware.class, version = 1)
public abstract class DeviceDatabase extends RoomDatabase {

    private static DeviceDatabase instance;

    public abstract DeviceDao DeviceDao();
    public static synchronized DeviceDatabase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(),DeviceDatabase.class,"post_database")
                    .fallbackToDestructiveMigration().build();
        }
        return instance;
    }

}
