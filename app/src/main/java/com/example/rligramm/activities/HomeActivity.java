package com.example.rligramm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.rligramm.R;
import com.example.rligramm.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
    }
    private void setListeners(){
        binding.mapNavigateButton.setOnClickListener(view -> Navigate(MapActivity.class));
        binding.chatNavigateButton.setOnClickListener(view -> Navigate(MainActivity.class));
    }
    private void Navigate(Class page){
        Intent intent = new Intent(getApplicationContext(), page);
        startActivity(intent);
        finish();
    }
}