package com.briatka.pavol.favouriteplaces.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.customobjects.CustomPlace;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripOnMapActivity extends AppCompatActivity {

    public static final String MAP_COORDINATES_KEY = "destinations_key";
    public static final String TRIP_NAME_KEY = "trip_name_key";

    @BindView(R.id.trip_destinations_map)
    MapView destinationMapView;

    private GoogleMap destinationMap;
    private ArrayList<CustomPlace> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_on_map);
        ButterKnife.bind(this);

        Intent inheritedData = getIntent();
        String listString = inheritedData.getStringExtra(MAP_COORDINATES_KEY);
        String tripName = inheritedData.getStringExtra(TRIP_NAME_KEY);

        setTitle(tripName);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CustomPlace>>() {}.getType();

        dataList = gson.fromJson(listString, type);

        destinationMapView.onCreate(savedInstanceState);
        destinationMapView.onResume();


        try {
            MapsInitializer.initialize(TripOnMapActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        destinationMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                destinationMap = googleMap;

                int permissionCheck = ContextCompat.checkSelfPermission(TripOnMapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    for (int i = 0; i < dataList.size(); i++) {
                        destinationMap.addMarker(new MarkerOptions()
                                .position(dataList.get(i).placeLatLng)
                                .title(dataList.get(i).placeName));
                    }

                    destinationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            dataList.get(0).placeLatLng,7 ));
                }


            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        destinationMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        destinationMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destinationMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        destinationMapView.onLowMemory();
    }
}
