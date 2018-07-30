package com.briatka.pavol.favouriteplaces.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.Toast;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.contentprovider.FavPlacesContract;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
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

    private String tempFilePath;
    private Bitmap bitmapPicture;
    private FusedLocationProviderClient locationProviderClient;
    private byte[] imgByteArray = null;
    private GoogleMap googleMap;
    private Location lastKnownLocation;
    private int permissionCheck;
    private double latitude;
    private double longitude;

    private static final String FILE_PROVIDER_AUTHORITY = "com.briatka.pavol.fileprovider";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);
        ButterKnife.bind(this);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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

                getLocation();

            }
        });
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
        }
    }

    private void processImage() {

        placePhoto.setVisibility(View.VISIBLE);

        bitmapPicture = PhotoUtils.fixRotation(PhotoUtils.mResampleImage(tempFilePath, this), tempFilePath);

        Bitmap resized = ThumbnailUtils.extractThumbnail(bitmapPicture, 300, 300);

        bitmapPicture = ThumbnailUtils.extractThumbnail(bitmapPicture, bitmapPicture.getWidth() / 2, bitmapPicture.getHeight() / 2);

        placePhoto.setImageBitmap(resized);


    }

    private void getLocation() {
        int permissionCheck = ContextCompat.checkSelfPermission(AddNewFavPlaceActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            Task locationResult = locationProviderClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                float defaultZoom = 15;
                                lastKnownLocation = task.getResult();
                                latitude = lastKnownLocation.getLatitude();
                                longitude = lastKnownLocation.getLongitude();
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(latitude,
                                                longitude), defaultZoom
                                ));
                            }
                        }
                    });

        }
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

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavPlacesContract.FavPlacesEntry.PLACE_NAME, title);
        contentValues.put(FavPlacesContract.FavPlacesEntry.PLACE_COORDINATES, coordinates);
        contentValues.put(FavPlacesContract.FavPlacesEntry.PLACE_PHOTO, imgByteArray);

        Uri uri = getContentResolver().insert(FavPlacesContract.FavPlacesEntry.PLACES_CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(getBaseContext(),
                    getBaseContext().getResources().getString(R.string.new_fav_place_added),
                    Toast.LENGTH_SHORT).show();
        }

        if (bitmapPicture != null) {
            PhotoUtils.deleteFile(this, tempFilePath);
        }

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

}
