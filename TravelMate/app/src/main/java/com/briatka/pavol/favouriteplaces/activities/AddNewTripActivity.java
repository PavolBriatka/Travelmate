package com.briatka.pavol.favouriteplaces.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.adapters.CreateTripAdapter;
import com.briatka.pavol.favouriteplaces.customobjects.CustomPlace;
import com.briatka.pavol.favouriteplaces.customobjects.TripObject;
import com.briatka.pavol.favouriteplaces.executors.AppExecutors;
import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNewTripActivity extends AppCompatActivity {

    @BindView(R.id.create_trip_list_rv)
    RecyclerView tripListRv;
    @BindView(R.id.save_trip_button)
    FloatingActionButton saveTrip;
    @BindView(R.id.trip_name_et)
    EditText tripNameEt;
    @BindView(R.id.create_trip_empty_view)
    TextView emptyView;

    private CreateTripAdapter adapter;
    private ArrayList<CustomPlace> placeArrayList = new ArrayList<>();

    private TravelMateDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);
        ButterKnife.bind(this);

        mDatabase = TravelMateDatabase.getInstance(getApplicationContext());

        tripListRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CreateTripAdapter(this, null);
        tripListRv.setAdapter(adapter);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                emptyView.setVisibility(View.GONE);
                placeArrayList.add(getPlaceData(place));
                adapter.setCreateTripData(placeArrayList);
                tripListRv.setAdapter(adapter);

            }

            @Override
            public void onError(Status status) {
                Toast.makeText(AddNewTripActivity.this,
                        "An error occurred, please try again",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        saveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (placeArrayList.size() > 0 & !tripNameEt.getText().toString().isEmpty()) {
                    saveTripToDatabase();
                }
                if (placeArrayList.size() == 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.create_trip_empty_list_msg), Toast.LENGTH_LONG).show();
                }

                if (tripNameEt.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), getString(R.string.create_trip_empty_name_view), Toast.LENGTH_LONG).show();
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int id = (int) viewHolder.itemView.getTag();

                placeArrayList.remove(id);
                adapter.setCreateTripData(placeArrayList);
                tripListRv.setAdapter(adapter);
                if (placeArrayList.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                }

            }
        }).attachToRecyclerView(tripListRv);
    }

    private void saveTripToDatabase() {
        Gson gson = new Gson();
        String data = gson.toJson(placeArrayList);
        String tripName = tripNameEt.getText().toString();

        final TripObject tripObject = new TripObject(tripName,data);
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.tripDao().insertTrip(tripObject);
            }
        });


        finish();
    }

    private CustomPlace getPlaceData(Place mPlace) {

        return new CustomPlace(mPlace.getName().toString(),
                mPlace.getAddress().toString(),
                mPlace.getLatLng(),
                false);
    }
}
