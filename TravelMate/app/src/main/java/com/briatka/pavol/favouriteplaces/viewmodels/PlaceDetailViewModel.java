package com.briatka.pavol.favouriteplaces.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.briatka.pavol.favouriteplaces.customobjects.FavPlaceObject;
import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;

public class PlaceDetailViewModel extends ViewModel {

    private LiveData<FavPlaceObject> place;

    public PlaceDetailViewModel(TravelMateDatabase database, int placeId){
        place = database.favPlaceDao().loadById(placeId);
    }

    public LiveData<FavPlaceObject> getPlace() {
        return place;
    }
}
