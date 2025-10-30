package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ordapp.databinding.ActivityPracticeBinding;
import com.example.ordapp.databinding.ActivitySelectFileBinding;

public class SelectFile extends AppCompatActivity {

    private ActivitySelectFileBinding binding;
    public native String printFile();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.WordsFileButton.setOnClickListener(view -> {
            Intent intent = new Intent(SelectFile.this, Practice.class);
            intent.putExtra("FILE_NAME", "words");
            intent.putExtra("LANGUAGE", "spanish");
            startActivity(intent);
        });
    }
}