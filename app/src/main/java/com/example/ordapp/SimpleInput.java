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