package com.briatka.pavol.favouriteplaces.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;

public class PlaceDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TravelMateDatabase mDatabase;
    private final int mPlaceId;

    public PlaceDetailViewModelFactory(TravelMateDatabase database, int placeId){
        mDatabase = database;
        mPlaceId = placeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PlaceDetailViewModel(mDatabase, mPlaceId);
    }
}
