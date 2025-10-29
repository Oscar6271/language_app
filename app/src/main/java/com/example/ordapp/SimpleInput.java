package com.example.ordapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ordapp.databinding.ActivitySimpleInputBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleInput extends AppCompatActivity {
    private ActivitySimpleInputBinding binding;

    public native boolean writeToFile(String fileName, String contentToWrite);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySimpleInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.createSimpleFileButton.setOnClickListener(v -> {
            String fileName = binding.fileName.getEditText().getText().toString();

            writeToFile(getFilesDir().getAbsolutePath() + "/" + fileName, binding.simpleInput.getEditText().getText().toString());
        });
    }
}