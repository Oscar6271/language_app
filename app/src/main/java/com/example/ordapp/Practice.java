package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordapp.databinding.ActivityPracticeBinding;
import com.example.ordapp.databinding.ActivitySimpleInputBinding;


public class Practice extends AppCompatActivity {

    static {
        System.loadLibrary("ordapp");
    }
    public native void readFile(String fileName, String language_to_write_in);

    private ActivityPracticeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("FILE_NAME");
        String language_to_write_in = intent.getStringExtra("LANGUAGE");

        readFile(fileName, language_to_write_in);
    }
}