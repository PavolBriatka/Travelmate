package com.briatka.pavol.favouriteplaces.roomdatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.briatka.pavol.favouriteplaces.customobjects.FavPlaceObject;

import java.util.List;

@Dao
public interface FavPlaceDao {

    @Query("SELECT * FROM favourite_places")
    LiveData<List<FavPlaceObject>> loadAllPlaces();

    @Query("SELECT * FROM favourite_places WHERE id = :id")
    LiveData<FavPlaceObject> loadById(int id);

    @Insert
    void insertPlace(FavPlaceObject newPlace);

    @Delete
    void deletePlace (FavPlaceObject deletedPlace);


}
