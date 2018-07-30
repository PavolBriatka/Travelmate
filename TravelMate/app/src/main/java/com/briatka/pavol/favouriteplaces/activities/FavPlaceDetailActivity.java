package com.briatka.pavol.favouriteplaces.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.contentprovider.FavPlacesContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavPlaceDetailActivity extends AppCompatActivity {

    public static final String ID_KEY = "id_key";

    private static final String IS_VISIBLE_KEY = "is_visible";
    private static final String PHOTO_ARRAY_KEY = "photo_byte_array";

    @BindView(R.id.place_detail_name)
    TextView detailName;
    @BindView(R.id.place_detail_photo)
    ImageView detailPhoto;
    @BindView(R.id.map_fav_place)
    MapView favPlaceMap;
    @BindView(R.id.share_button)
    LinearLayout shareButton;
    @BindView(R.id.fullscreen_button)
    LinearLayout fullscreenButton;
    @BindView(R.id.place_detail_fullscreen)
    ImageView fullscreenImage;
    @BindView(R.id.fav_place_detail_app_bar)
    AppBarLayout placeDetailAppBar;
    @BindView(R.id.place_detail_content_linear_layout)
    LinearLayout contentLinearLayout;

    private String placeId;
    private Cursor favouritePlaceCursor = null;
    private Bitmap photoBitmapSource = null;

    private static String googleMapLinkTemplate = "https://maps.google.com/?q={lat},{lng}";

    private String placeName;
    private GoogleMap mapObject;
    private LatLng coordinates;
    private Boolean isVisible = false;
    private byte[] photoArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_place_detail);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            isVisible = savedInstanceState.getBoolean(IS_VISIBLE_KEY);
            photoArray = savedInstanceState.getByteArray(PHOTO_ARRAY_KEY);
            byteArrayToBitmap(photoArray);
        }

        if (isVisible) {
            isVisibleTrue();
        } else {
            isVisibleFalse();
        }

        Intent passedData = getIntent();
        placeId = passedData.getStringExtra(ID_KEY);

        Uri basicUri = FavPlacesContract.FavPlacesEntry.PLACES_CONTENT_URI;
        basicUri = basicUri.buildUpon().appendPath(placeId).build();

        final Uri placePathUri = basicUri;

        favouritePlaceCursor = getContentResolver().query(
                placePathUri,
                null,
                null,
                null,
                null);

        favouritePlaceCursor.moveToFirst();
        getPlaceFromDatabase(favouritePlaceCursor);

        favPlaceMap.onCreate(savedInstanceState);
        favPlaceMap.onResume();

        try {
            MapsInitializer.initialize(FavPlaceDetailActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        favPlaceMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapObject = googleMap;

                int permissionCheck = ContextCompat.checkSelfPermission(FavPlaceDetailActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    mapObject.addMarker(new MarkerOptions()
                            .position(coordinates)
                            .title(placeName));

                    mapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                }

            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mapHyperlink = googleMapLinkTemplate.replace("{lat}", String.valueOf(coordinates.latitude))
                        .replace("{lng}", String.valueOf(coordinates.longitude));

                String mimeType = "text/plain";

                startActivity(ShareCompat.IntentBuilder.from(FavPlaceDetailActivity.this)
                        .setType(mimeType)
                        .setText(mapHyperlink)
                        .createChooserIntent());

            }
        });

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoBitmapSource != null) {
                    isVisible = true;
                    isVisibleTrue();
                } else {
                    Toast.makeText(FavPlaceDetailActivity.this,
                            getString(R.string.no_picture_to_show),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        fullscreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVisible = false;
                isVisibleFalse();
            }
        });


    }

    private void isVisibleTrue() {
        placeDetailAppBar.setVisibility(View.GONE);
        contentLinearLayout.setVisibility(View.INVISIBLE);
        fullscreenImage.setVisibility(View.VISIBLE);
        fullscreenImage.setImageBitmap(photoBitmapSource);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    private void isVisibleFalse() {
        placeDetailAppBar.setVisibility(View.VISIBLE);
        contentLinearLayout.setVisibility(View.VISIBLE);
        fullscreenImage.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void getPlaceFromDatabase(Cursor pCursor) {
        int nameIndex = pCursor.getColumnIndex(FavPlacesContract.FavPlacesEntry.PLACE_NAME);
        int coordinatesIndex = pCursor.getColumnIndex(FavPlacesContract.FavPlacesEntry.PLACE_COORDINATES);
        int photoIndex = pCursor.getColumnIndex(FavPlacesContract.FavPlacesEntry.PLACE_PHOTO);

        //get values
        placeName = pCursor.getString(nameIndex);

        String pCoordinates = pCursor.getString(coordinatesIndex);
        String[] splitCoordinates = pCoordinates.split(",");
        double latitude = Double.valueOf(splitCoordinates[0]);
        double longitude = Double.valueOf(splitCoordinates[1]);
        coordinates = new LatLng(latitude, longitude);

        photoArray = pCursor.getBlob(photoIndex);
        if (photoArray != null) {
            byteArrayToBitmap(photoArray);
        }

        //set values
        detailName.setText(placeName);
        if (photoBitmapSource != null) {
            detailPhoto.setImageBitmap(photoBitmapSource);
        } else {
            detailPhoto.setImageResource(R.drawable.nature);
        }


    }

    private void byteArrayToBitmap(byte[] imgByteArray) {
        photoBitmapSource = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
    }

    @Override
    public void onResume() {
        super.onResume();
        favPlaceMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        favPlaceMap.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        favPlaceMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        favPlaceMap.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_VISIBLE_KEY, isVisible);
        outState.putByteArray(PHOTO_ARRAY_KEY, photoArray);
    }
}
