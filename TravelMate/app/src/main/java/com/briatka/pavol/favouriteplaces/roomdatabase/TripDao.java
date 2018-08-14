package com.briatka.pavol.favouriteplaces.roomdatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.briatka.pavol.favouriteplaces.customobjects.TripObject;

import java.util.List;

@Dao
public interface TripDao {

    @Query("SELECT * FROM trips")
    LiveData<List<TripObject>> loadAllTrips();

    @Query("SELECT * FROM trips WHERE id = :id")
    LiveData<TripObject> loadById(int id);

    @Insert
    void insertTrip(TripObject tripObject);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTrip (TripObject tripObject);

    @Delete
    void deleteTrip (TripObject tripObject);
}
