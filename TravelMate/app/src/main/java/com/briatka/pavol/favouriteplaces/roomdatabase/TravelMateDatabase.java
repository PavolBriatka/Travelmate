package com.briatka.pavol.favouriteplaces.roomdatabase;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.briatka.pavol.favouriteplaces.customobjects.FavPlaceObject;
import com.briatka.pavol.favouriteplaces.customobjects.TripObject;

@Database(entities = {FavPlaceObject.class, TripObject.class}, version = 2, exportSchema = false)
public abstract class TravelMateDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "travelmateDb";
    private static TravelMateDatabase dbInstance;

    public static TravelMateDatabase getInstance (Context context){
        if(dbInstance == null){
            synchronized (LOCK){
                dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                        TravelMateDatabase.class, TravelMateDatabase.DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .build();
            }
        }
        return dbInstance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'trips' ('id' INTEGER NOT NULL, "
            + "'title' TEXT, "
            + "'destination_data' TEXT, PRIMARY KEY('id'))");

        }
    };

    public abstract FavPlaceDao favPlaceDao();
    public abstract TripDao tripDao();
}
