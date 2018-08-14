package com.briatka.pavol.favouriteplaces.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.customobjects.FavPlaceObject;
import com.briatka.pavol.favouriteplaces.executors.AppExecutors;
import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNewFavPlaceActivity extends AppCompatActivity {

    @BindView(R.id.camera_button)
    LinearLayout cameraButton;
    @BindView(R.id.save_button)
    LinearLayout saveLocationButton;
    @BindView(R.id.place_photo)
    ImageView placePhoto;
    @BindView(R.id.place_name_et)
    EditText nameEt;
    @BindView(R.id.map_current_position)
    MapView mapView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String tempFilePath;
    private Bitmap bitmapPicture;
    private FusedLocationProviderClient locationProviderClient;
    private byte[] imgByteArray = null;
    private GoogleMap googleMap;
    private int permissionCheck;
    private double latitude;
    private double longitude;

    private static final String FILE_PROVIDER_AUTHORITY = "com.briatka.pavol.fileprovider";
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private LocationRequest locationRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private LocationCallback mLocationCallback;

    private TravelMateDatabase mDatabase;

    private int updateCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);
        ButterKnife.bind(this);

        mDatabase = TravelMateDatabase.getInstance(getApplicationContext());

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                updateCounter += 1;
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    float defaultZoom = 15;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(latitude,
                                    longitude), defaultZoom
                    ));

                }

                if(updateCounter == 3){
                    progressBar.setVisibility(View.GONE);
                }
            }
        };

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(AddNewFavPlaceActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {

                    }
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nameEt.getText().toString().isEmpty()) {
                    saveLocation();
                } else {
                    Toast.makeText(AddNewFavPlaceActivity.this,
                            getString(R.string.save_place_empty_name_view),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(AddNewFavPlaceActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mGoogleMap) {

                if (mGoogleMap == null) {
                    return;
                }

                googleMap = mGoogleMap;

                permissionCheck = ContextCompat.checkSelfPermission(AddNewFavPlaceActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            permissionCheck = ContextCompat.checkSelfPermission(AddNewFavPlaceActivity.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION);
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                locationProviderClient.getLastLocation()
                                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                if (location != null) {
                                                    latitude = location.getLatitude();
                                                    longitude = location.getLongitude();
                                                }
                                            }
                                        });
                            }
                            return false;
                        }
                    });
                } else {
                    googleMap.setMyLocationEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                }

            }
        });
    }


    private void startLocationUpdates() {
        permissionCheck = ContextCompat.checkSelfPermission(AddNewFavPlaceActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        locationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private void takePicture() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File tempFile = null;
            try {
                tempFile = PhotoUtils.mCreateTempFile(this);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            if (tempFile != null) {

                tempFilePath = tempFile.getAbsolutePath();

                Uri pictureUri = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY, tempFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            processImage();
        } else if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(AddNewFavPlaceActivity.this,
                    getString(R.string.targeting_device_toast),
                    Toast.LENGTH_SHORT).show();
            startLocationUpdates();

        } else if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_CANCELED) {
            Toast.makeText(AddNewFavPlaceActivity.this,
                    getString(R.string.gps_denied_toast),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void processImage() {

        placePhoto.setVisibility(View.VISIBLE);

        bitmapPicture = PhotoUtils.fixRotation(PhotoUtils.mResampleImage(tempFilePath, this), tempFilePath);

        Bitmap resized = ThumbnailUtils.extractThumbnail(bitmapPicture, 300, 300);

        bitmapPicture = ThumbnailUtils.extractThumbnail(bitmapPicture, bitmapPicture.getWidth() / 2, bitmapPicture.getHeight() / 2);

        placePhoto.setImageBitmap(resized);

    }

    private void bitmapToByteArray(Bitmap bitmapImg) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImg.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        imgByteArray = stream.toByteArray();
    }

    public void saveLocation() {

        if (bitmapPicture != null) {
            bitmapToByteArray(bitmapPicture);
        }

        String title = nameEt.getText().toString();
        String coordinates = Double.toString(latitude) + "," + Double.toString(longitude);

        final FavPlaceObject newPlace = new FavPlaceObject(title, coordinates, imgByteArray);
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.favPlaceDao().insertPlace(newPlace);
            }
        });



        if (bitmapPicture != null) {
            PhotoUtils.deleteFile(this, tempFilePath);
        }

        stopLocationUpdates();

        Toast.makeText(getBaseContext(),
                getBaseContext().getResources().getString(R.string.new_fav_place_added),
                Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setNumUpdates(3);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


}
