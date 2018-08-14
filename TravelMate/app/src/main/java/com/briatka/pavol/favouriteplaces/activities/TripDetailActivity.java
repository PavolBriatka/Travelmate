package com.briatka.pavol.favouriteplaces.activities;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.briatka.pavol.favouriteplaces.R;
import com.briatka.pavol.favouriteplaces.adapters.TripListAdapter;
import com.briatka.pavol.favouriteplaces.customobjects.CustomPlace;
import com.briatka.pavol.favouriteplaces.customobjects.TripObject;
import com.briatka.pavol.favouriteplaces.executors.AppExecutors;
import com.briatka.pavol.favouriteplaces.roomdatabase.TravelMateDatabase;
import com.briatka.pavol.favouriteplaces.viewmodels.TripDetailViewModel;
import com.briatka.pavol.favouriteplaces.viewmodels.TripDetailViewModelFactory;
import com.briatka.pavol.favouriteplaces.widget.TripWidgetProvider;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripDetailActivity extends AppCompatActivity {

    public static final String TRIP_ID = "trip_id";

    private static final int TRIP_ID_DEFAULT_VALUE = -1;
    @BindView(R.id.detail_trip_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.show_on_map_button)
    FloatingActionButton showOnMapButton;
    @BindView(R.id.trip_save_changes_button)
    ImageView saveChangesButton;
    @BindView(R.id.step_back_button)
    ImageView stepBackButton;
    @BindView(R.id.change_trip_name_button)
    ImageView changeNameButton;
    @BindView(R.id.add_destination_button)
    ImageView addDestinationButton;

    @BindView(R.id.change_name_edit_text)
    EditText changeNameEditText;

    private TravelMateDatabase mDatabase;

    private String tripId;
    private ArrayList<CustomPlace> tripList = new ArrayList<>();
    private ArrayList<CustomPlace> tripChanges = new ArrayList<>();
    private String tripData;
    private String tripName;
    private TripListAdapter adapter;

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        ButterKnife.bind(this);

        mDatabase = TravelMateDatabase.getInstance(getApplicationContext());

        Intent passedData = getIntent();
        int id = passedData.getIntExtra(TRIP_ID, TRIP_ID_DEFAULT_VALUE);
        tripId = String.valueOf(id);

        TripDetailViewModelFactory factory = new TripDetailViewModelFactory(mDatabase, id);
        final TripDetailViewModel viewModel =
                ViewModelProviders.of(this,factory).get(TripDetailViewModel.class);
        viewModel.getTrip().observe(this, new Observer<TripObject>() {
            @Override
            public void onChanged(@Nullable TripObject tripObject) {
                populateUI(tripObject);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new TripListAdapter(this, null);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int id = (int) viewHolder.itemView.getTag();
                CustomPlace deletedItem = tripList.get(id);
                tripList.remove(id);

                tripChanges.add(deletedItem);

                adapter.setTripListData(tripList);
                recyclerView.setAdapter(adapter);

            }
        }).attachToRecyclerView(recyclerView);

        stepBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepBack();
            }
        });

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTripName();
            }
        });
        addDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDestination();
            }
        });
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTrip();
            }
        });
    }


    private void populateUI(TripObject tripObject){
        tripName = tripObject.getTitle();
        tripData = tripObject.getDestinationData();

        Type type = new TypeToken<ArrayList<CustomPlace>>() {
        }.getType();
        Gson gson = new Gson();
        tripList = gson.fromJson(tripData, type);

        adapter.setTripListData(tripList);
        adapter.setTripId(tripId);
        adapter.setTripName(tripName);
        recyclerView.setAdapter(adapter);

        setTitle(tripName);

        showOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMap = new Intent(getApplicationContext(), TripOnMapActivity.class);
                openMap.putExtra(TripOnMapActivity.MAP_COORDINATES_KEY, tripData);
                openMap.putExtra(TripOnMapActivity.TRIP_NAME_KEY, tripName);
                startActivity(openMap);
            }
        });

        saveToPreferences();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TripWidgetProvider.class));
        TripWidgetProvider.updateTripWidget(this, appWidgetManager, appWidgetIds);
    }

    private void saveToPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.trip_name_widget_key), tripName);
        editor.putString(getString(R.string.trip_data_widget_key), tripData);
        editor.putString(getString(R.string.trip_id_widget_key), tripId);
        editor.apply();
    }

    private void updateTrip() {
        if(tripList.size() == 0) {
            Toast.makeText(this,
                    getString(R.string.edit_trip_empty_save_array),
                    Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            final int id = Integer.parseInt(tripId);
            tripData = gson.toJson(tripList);
            final TripObject updatedObject = new TripObject(tripName, tripData);
            AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    updatedObject.setId(id);
                    mDatabase.tripDao().updateTrip(updatedObject);
                }
            });

            Toast.makeText(TripDetailActivity.this,
                    getString(R.string.changes_saved_msg),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void updateTripName() {
        final int id = Integer.parseInt(tripId);
        final TripObject updatedObject = new TripObject(tripName,tripData);
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                updatedObject.setId(id);
                mDatabase.tripDao().updateTrip(updatedObject);
            }
        });
    }

    private void newDestinationUpdate() {
        final int id = Integer.parseInt(tripId);
        Gson gson = new Gson();
        tripData = gson.toJson(tripList);
        final TripObject updatedObject = new TripObject(tripName,tripData);
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                updatedObject.setId(id);
                mDatabase.tripDao().updateTrip(updatedObject);
            }
        });
    }

    private void stepBack() {
        if (tripChanges.size() != 0) {
            int lastChangeIndex = tripChanges.size() - 1;
            CustomPlace restoredItem = tripChanges.get(lastChangeIndex);
            tripList.add(restoredItem);
            tripChanges.remove(lastChangeIndex);


            adapter.setTripListData(tripList);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_changes_message),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void changeTripName() {
        changeNameEditText.setVisibility(View.VISIBLE);
        changeNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tripName = changeNameEditText.getText().toString();
                    if (tripName.isEmpty()){
                        Toast.makeText(TripDetailActivity.this,
                                getString(R.string.edit_trip_empty_new_name_string),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        updateTripName();
                        setTitle(tripName);
                        changeNameEditText.setVisibility(View.GONE);
                        return true;
                    }

                }
                return false;
            }
        });
    }

    private void addDestination() {

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                tripList.add(getPlaceData(place));
                newDestinationUpdate();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(TripDetailActivity.this,
                        "An error occurred:" + status.getStatusMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private CustomPlace getPlaceData(Place place) {

        return new CustomPlace(place.getName().toString(),
                place.getAddress().toString(),
                place.getLatLng(),
                false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}


