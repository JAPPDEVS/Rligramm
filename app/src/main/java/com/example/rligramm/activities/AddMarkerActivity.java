package com.example.rligramm.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.rligramm.R;
import com.example.rligramm.custom.CustomScrollView;
import com.example.rligramm.utilities.Constants;
import com.example.rligramm.utilities.PreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class AddMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap gMap;
    FrameLayout map;
    Marker placeMarker;
    EditText placeNameContent;
    EditText placeDescContent;
    CustomScrollView scrollView;
    View enableButton;
    ImageView placeImageView;
    private String encodedImage;
    private static boolean isScrollable = true;

    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        init();
        setListeners();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.miniMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        enableButton.setOnClickListener(view -> onButtonEnabledClick());
    }


    void init() {
        enableButton = findViewById(R.id.imageEnableScroll);
        enableButton.setBackgroundColor(Color.RED);
        map = findViewById(R.id.miniMap);
        placeImageView = findViewById(R.id.placeImageView);
        scrollView = findViewById(R.id.scrollView);
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(this);
        placeNameContent = findViewById(R.id.inputPlaceName);
        placeDescContent = findViewById(R.id.inputPlaceDesc);
    }

    void onButtonEnabledClick() {
        if(isScrollable){
            enableButton.setBackgroundColor(Color.GREEN);
            scrollView.setEnableScrolling(false);
            isScrollable = false;
        } else {
            enableButton.setBackgroundColor(Color.RED);
            scrollView.setEnableScrolling(true);
            isScrollable = true;
        }

    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            placeImageView.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    void setListeners() {
        findViewById(R.id.buttonAddMarker).setOnClickListener(view -> addMarker());
        findViewById(R.id.buttonBack).setOnClickListener(view -> Navigate(MainMapActivity.class));
        findViewById(R.id.placeImageView).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void Navigate(Class page) {
        Intent intent = new Intent(getApplicationContext(), page);
        startActivity(intent);
        finish();
    }

    void addMarker() {
        String namePlace = placeNameContent.getText().toString().trim();
        String descPlace = placeDescContent.getText().toString();
        HashMap<String, Object> data_place = new HashMap<>();

        if (!namePlace.equals("")) {
            data_place.put(Constants.NAME_PLACE, namePlace);
            data_place.put(Constants.LATITUDE_PLACE, placeMarker.getPosition().latitude);
            data_place.put(Constants.LONGITUDE_PLACE, placeMarker.getPosition().longitude);
            data_place.put(Constants.DESCRIPTION_PLACE, descPlace);
            data_place.put(Constants.FOUNDER_PLACE, preferenceManager.getString(Constants.KEY_EMAIL));
            data_place.put(Constants.IMAGE_PLACE,encodedImage);
            data_place.put(Constants.LIKES_PLACE, 0);

            database.collection(Constants.ADMIN_PLACE_COLLECTION).add(data_place);

            placeNameContent.setText(null);
            placeDescContent.setText(null);
            placeImageView.setImageBitmap(null);

            placeMarker.setPosition(new LatLng(56.852676, 53.206900));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(56.852676, 53.206900), 9));
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;
        LatLng placeCoors = new LatLng(56.852676, 53.206900);
        placeMarker = gMap.addMarker(new MarkerOptions()
                .draggable(true)
                .position(placeCoors));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeCoors, 9));
    }
}