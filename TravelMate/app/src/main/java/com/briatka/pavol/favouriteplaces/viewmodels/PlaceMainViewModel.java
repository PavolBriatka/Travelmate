package com.briatka.pavol.favouriteplaces.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.briatka.pavol.favouriteplaces.customobjects.FavPlaceObject;
import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;

import java.util.List;

public class PlaceMainViewModel extends AndroidViewModel {

    private LiveData<List<FavPlaceObject>> placeList;

    public PlaceMainViewModel(@NonNull Application application) {
        super(application);
        TravelMateDatabase database = TravelMateDatabase.getInstance(this.getApplication());
        placeList = database.favPlaceDao().loadAllPlaces();
    }

    public LiveData<List<FavPlaceObject>> getPlaceList() {
        return placeList;
    }
}
