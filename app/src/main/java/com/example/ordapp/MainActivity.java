package com.example.ordapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.ordapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addFilesButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SimpleInput.class);
            intent.putExtra("APPEND", true);

            startActivity(intent);
        });

        binding.selectFilesButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ChooseF.class);
            startActivity(intent);
        });
    }
}