package com.example.ordapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordapp.databinding.ActivityPracticeBinding;
import com.example.ordapp.databinding.ActivitySimpleInputBinding;


public class Practice extends AppCompatActivity {

    static {
        System.loadLibrary("ordapp");
    }
    public native String stringFromJNI();
    private ActivityPracticeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String result = stringFromJNI();
        Log.d("DEBUG", "Resultat från C++: " + result);

    }
}