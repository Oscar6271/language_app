package com.example.ordapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.ordapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'ordapp' library on application startup.
    static {
        System.loadLibrary("ordapp");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addFilesButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SimpleInput.class);
            intent.putExtra("APPEND", true);

            startActivity(intent);
        });

        binding.selectFilesButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SelectFile.class);
            startActivity(intent);
        });
    }
}