package com.briatka.pavol.favouriteplaces.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;

public class TripDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TravelMateDatabase mDatabase;
    private final int mTripId;

    public TripDetailViewModelFactory(TravelMateDatabase database, int tripId){
        mDatabase = database;
        mTripId = tripId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TripDetailViewModel(mDatabase, mTripId);
    }
}
