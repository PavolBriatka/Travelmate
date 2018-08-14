package com.briatka.pavol.favouriteplaces.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.briatka.pavol.favouriteplaces.customobjects.TripObject;
import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;

import java.util.List;

public class TripMainViewModel extends AndroidViewModel {

    private LiveData<List<TripObject>> tripList;

    public TripMainViewModel(@NonNull Application application) {
        super(application);
        TravelMateDatabase database = TravelMateDatabase.getInstance(this.getApplication());
        tripList = database.tripDao().loadAllTrips();
    }

    public LiveData<List<TripObject>> getTripList() {return tripList;}
}
