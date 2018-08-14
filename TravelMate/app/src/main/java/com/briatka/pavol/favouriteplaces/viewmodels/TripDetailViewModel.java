package com.briatka.pavol.favouriteplaces.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.briatka.pavol.favouriteplaces.customobjects.TripObject;
import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;

public class TripDetailViewModel  extends ViewModel {

    private LiveData<TripObject> trip;

    public TripDetailViewModel(TravelMateDatabase database, int tripId){
        trip = database.tripDao().loadById(tripId);
    }

    public LiveData<TripObject> getTrip() {
        return trip;
    }
}
