package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ordapp.databinding.ActivitySelectFileBinding;

public class SelectFile extends AppCompatActivity {
    private ActivitySelectFileBinding binding;
    public native String printFile(String fileName);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ConstraintLayout dropdownMenu = (ConstraintLayout)findViewById(R.id.dropdownMenu);

        binding.WordsFileButton.setOnClickListener(view -> {
            if (dropdownMenu.getVisibility() == View.GONE) {
                dropdownMenu.setVisibility(View.VISIBLE);
            } else {
                dropdownMenu.setVisibility(View.GONE);
            }
        });

        binding.WriteTranslationButton.setOnClickListener(view ->{
            Intent intent = new Intent(SelectFile.this, Practice.class);
            intent.putExtra("FILE_NAME", "words");
            intent.putExtra("LANGUAGE", "translation");
            startActivity(intent);
        });

        binding.WriteOriginalButton.setOnClickListener(view -> {
            Intent intent = new Intent(SelectFile.this, Practice.class);
            intent.putExtra("FILE_NAME", "words");
            intent.putExtra("LANGUAGE", "original");
            startActivity(intent);
        });

        binding.EditSetButton.setOnClickListener(view -> {
            Intent intent = new Intent(SelectFile.this, SimpleInput.class);

            intent.putExtra("FILE_NAME", "words");
            intent.putExtra("CONTENT", printFile(getFilesDir().getAbsolutePath() + "/words"));
            intent.putExtra("APPEND", false);

            startActivity(intent);
        });
    }
}