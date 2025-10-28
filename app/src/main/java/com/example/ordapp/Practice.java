package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordapp.databinding.ActivityPracticeBinding;
import com.example.ordapp.databinding.ActivitySimpleInputBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Practice extends AppCompatActivity {

    private String read_file(String fileName)
    {
        File file = new File(getFilesDir(), fileName + ".txt");
        String result = "";
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line + '\n';
                    Log.d("DEBUG", "Filinnehåll: " + line);
                }
                reader.close();
            } catch (IOException e) {
                Log.e("DEBUG", "Fel vid läsning av fil", e);
            }
        } else {
            Log.d("DEBUG", "Filen finns inte");
        }
        return result;
    }

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
        Log.e("DEBUG", getFilesDir().getAbsolutePath() + "/" + fileName);

        readFile(getFilesDir().getAbsolutePath() + "/" + fileName, language_to_write_in);

        read_file(fileName);
    }
}