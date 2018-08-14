package com.briatka.pavol.favouriteplaces.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.adapters.FavPlacesAdapter;
import com.briatka.pavol.favouriteplaces.adapters.FragmentAdapter;
import com.briatka.pavol.favouriteplaces.adapters.TripListMainAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FavPlacesAdapter.OnFavPlaceItemClickListener,
        TripListMainAdapter.OnTripListItemClickListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //Asking for permission to access the location
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }


        FragmentAdapter adapter = new FragmentAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

    }



    @Override
    public void onFavPlaceItemClicked(int id) {
        Intent openDetailActivity = new Intent(getBaseContext(), FavPlaceDetailActivity.class);
        openDetailActivity.putExtra(FavPlaceDetailActivity.ID_KEY, id);
        startActivity(openDetailActivity);
    }

    @Override
    public void onTripListItemClicked(int id) {
        Intent openTripDetail = new Intent(getBaseContext(), TripDetailActivity.class);
        openTripDetail.putExtra(TripDetailActivity.TRIP_ID, id);
        startActivity(openTripDetail);
    }

    //handling the permission request submission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(),
                            getString(R.string.permission_granted), Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(),
                            getString(R.string.permission_not_granted), Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



}
