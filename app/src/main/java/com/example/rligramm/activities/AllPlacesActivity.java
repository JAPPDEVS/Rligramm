package com.example.rligramm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.rligramm.R;
import com.example.rligramm.adapters.PlaceAdapter;
import com.example.rligramm.databinding.ActivityAllPlacesBinding;
import com.example.rligramm.models.Coordinates;
import com.example.rligramm.models.Place;
import com.example.rligramm.utilities.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AllPlacesActivity extends AppCompatActivity {

    ActivityAllPlacesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityAllPlacesBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        addPlaces();
        setListeners();
    }

    void showErrorMessage(){
        Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT);
    }

    String shortDesc(String string){
        if (string == null){
            return "Нет описания";
        }
        else if(string.length()<25){
            return string;
        }
        else{
            String newString = string.substring(0,25)+"...";
            return newString;
        }

    }

    private void setListeners(){
        binding.mapNavigateButton.setOnClickListener(view -> Navigate(MapActivity.class));
        binding.chatNavigateButton.setOnClickListener(view -> Navigate(MainActivity.class));
        binding.homeNavigateButton.setOnClickListener(view -> Navigate(HomeActivity.class));
    }

    private void Navigate(Class page){
        Intent intent = new Intent(getApplicationContext(), page);
        startActivity(intent);
        finish();
    }


    void addPlaces(){
        ArrayList<Place> placeArrayList = new ArrayList<>();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.ADMIN_PLACE_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Place place = new Place();
                            place.name = queryDocumentSnapshot.getString(Constants.NAME_PLACE);
                            place.description = shortDesc(queryDocumentSnapshot.getString(Constants.DESCRIPTION_PLACE));
                            place.founder = queryDocumentSnapshot.getString(Constants.FOUNDER_PLACE);
                            double lotitude = (double) queryDocumentSnapshot.get(Constants.LATITUDE_PLACE);
                            double longitude = (double) queryDocumentSnapshot.get(Constants.LONGITUDE_PLACE);
                            place.coordinates = new Coordinates(lotitude, longitude);
                            byte[] bytes = Base64.decode(queryDocumentSnapshot.getString(Constants.IMAGE_PLACE), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            place.image = bitmap;
                            placeArrayList.add(place);
                        }
                        if (placeArrayList.size() > 0) {
                            PlaceAdapter placeAdapter = new PlaceAdapter(placeArrayList);
                            binding.allPlacesRecView.setAdapter(placeAdapter);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }
}