package com.briatka.pavol.favouriteplaces.customobjects;

import com.google.android.gms.maps.model.LatLng;

public class CustomPlace {

    public String placeName;
    public String placeAddress;
    public LatLng placeLatLng;
    public Boolean isVisited;

    public CustomPlace(String pName, String pAddress, LatLng pLatLng, Boolean pIsVisited) {
        placeName = pName;
        placeAddress = pAddress;
        placeLatLng = pLatLng;
        isVisited = pIsVisited;
    }
}
