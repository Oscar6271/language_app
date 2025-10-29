package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.TextView;

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
    private void init_file()
    {
        Intent intent = getIntent();
        String fileName = intent.getStringExtra("FILE_NAME");
        String language_to_write_in = intent.getStringExtra("LANGUAGE");
        Log.e("DEBUG", getFilesDir().getAbsolutePath() + "/" + fileName);

        readFile(getFilesDir().getAbsolutePath() + "/" + fileName, language_to_write_in);
        wordToTranslate = pickWord();
    }

    private void set_text()
    {
        TextView wordToTranslateTextBox;

        wordToTranslateTextBox = (TextView)findViewById(R.id.WordToTranslateText);
        wordToTranslateTextBox.setText(wordToTranslate);
    }

    static {
        System.loadLibrary("ordapp");
    }

    public native void readFile(String fileName, String language_to_write_in);
    public native String pickWord();
    public native boolean compare(String userInput);

    private String wordToTranslate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityPracticeBinding binding;

        super.onCreate(savedInstanceState);
        binding = ActivityPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView ResponseTextBox;
        ResponseTextBox = (TextView)findViewById(R.id.responseText);

        init_file();
        set_text();
        binding.compareButton.setOnClickListener(view -> {
            boolean correct = compare(binding.TranslationInputField.getEditText().getText().toString());
            if(correct)
            {
                ResponseTextBox.setText("Correct");
            }
            else
            {
                ResponseTextBox.setText("Wrong");
            }
        });
    }
}