package com.example.rligramm.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rligramm.R;
import com.example.rligramm.models.Coordinates;
import com.example.rligramm.models.Place;
import com.example.rligramm.utilities.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainMapActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener , OnMapReadyCallback {
    GoogleMap gMap;
    FrameLayout map;
    String placeName;
    String placeDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        map = findViewById(R.id.map);
        setListeners();
        getPlaces();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void setListeners() {
        findViewById(R.id.mapNavigateButton).setOnClickListener(view -> Navigate(MapActivity.class));
        findViewById(R.id.chatNavigateButton).setOnClickListener(view -> Navigate(MainActivity.class));
        findViewById(R.id.homeNavigateButton).setOnClickListener(view -> Navigate(HomeActivity.class));
        findViewById(R.id.imageBack).setOnClickListener(view -> Navigate(MapActivity.class));
        findViewById(R.id.addMarker).setOnClickListener(view -> Navigate(AddMarkerActivity.class));
    }

    private void Navigate(Class page) {
        Intent intent = new Intent(getApplicationContext(), page);
        startActivity(intent);
        finish();
    }

    private void showErrorMessage() {
        Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    void getPlaces() {
        ArrayList<Place> placeArrayList = new ArrayList<>();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.ADMIN_PLACE_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Place place = new Place();
                            place.name = queryDocumentSnapshot.getString(Constants.NAME_PLACE);
                            place.description = queryDocumentSnapshot.getString(Constants.DESCRIPTION_PLACE);
                            double lotitude = (double) queryDocumentSnapshot.get(Constants.LATITUDE_PLACE);
                            double longitude = (double) queryDocumentSnapshot.get(Constants.LONGITUDE_PLACE);
                            place.coordinates = new Coordinates(lotitude, longitude);
                            placeArrayList.add(place);
                        }
                        if (placeArrayList.size() > 0) {
                            LatLng pos = new LatLng(56.852676, 53.206900);
                            for (int i = 0; i < placeArrayList.size(); i++) {
                                pos = new LatLng(placeArrayList.get(i).coordinates.latitude, placeArrayList.get(i).coordinates.longitude);
                                gMap.addMarker(new MarkerOptions()
                                                .position(pos)
                                                .snippet(shortDesc(placeArrayList.get(i).description))
                                        .title(placeArrayList.get(i).name));
                                gMap.setOnMarkerClickListener(this::onMarkerClick);
                            }
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));

                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });


    }

    String shortDesc(String string){
        if (string == null){
            return "Нет описания";
        }
        else if(string.length()<15){
            return string;
        }
        else{
            String newString = string.substring(0,15)+"...";
            return newString;
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        findViewById(R.id.placeInfoBottom).setVisibility(View.VISIBLE);
        TextView placeN = findViewById(R.id.textPlaceName);
        TextView placeD = findViewById(R.id.textPlaceDesc);

        placeName = marker.getTitle();
        placeDesc = marker.getSnippet();

        placeN.setText(placeName);
        placeD.setText(placeDesc);

        return false;
    }
}