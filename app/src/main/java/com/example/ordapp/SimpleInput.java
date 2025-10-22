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

    private void write_to_file(String fileName)
    {
        try {
            File file = new File(getFilesDir(), fileName + ".txt");
            FileWriter writer = new FileWriter(file);
            String words = binding.simpleInput.getEditText().getText().toString();
            writer.write(words + '\n');
            writer.close();
        } catch (IOException e) {
            Log.d("DEBUG", "Unsuccesful write to file " + fileName);
        }

    }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySimpleInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.createSimpleFileButton.setOnClickListener(v -> {
            String fileName = binding.fileName.getEditText().getText().toString();
            if(!fileName.isEmpty())
            {
                try {
                    File file = new File(getFilesDir(),fileName + ".txt");

                    if(file.createNewFile())
                    {
                        Log.d("DEBUG", "Created");
                        write_to_file(fileName);
                    }
                    else
                    {
                        //lägg till text på skärmen med meddelande
                        Log.d("DEBUG", fileName + " doing nothing");
                    }


                    Log.d("DEBUG", "Fil sparad på: " + file.getAbsolutePath());
                }
                catch (IOException e)
                {
                    Log.d("DEBUG", "Error when creating file");
                }

                read_file(fileName);
            }
        });
    }
}