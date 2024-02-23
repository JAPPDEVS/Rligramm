package com.example.rligramm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.rligramm.databinding.ActivityMapBinding;

public class MapActivity extends AppCompatActivity {
    private ActivityMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        init();
        setListeners();
    }

    private void init(){
    }
    private void setListeners(){
        binding.homeNavigateButton.setOnClickListener((view -> Navigate(HomeActivity.class)));
        binding.chatNavigateButton.setOnClickListener(view -> Navigate(MainActivity.class));
    }

    private void Navigate(Class page){
        Intent intent = new Intent(getApplicationContext(), page);
        startActivity(intent);
        finish();
    }
}