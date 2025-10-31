package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordapp.databinding.ActivityPracticeBinding;
import com.example.ordapp.databinding.ActivitySimpleInputBinding;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Practice extends AppCompatActivity {

    private void init_file()
    {
        Intent intent = getIntent();
        String fileName = intent.getStringExtra("FILE_NAME");
        String language_to_write_in = intent.getStringExtra("LANGUAGE");

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
    public native String compare(String userInput);
    public native boolean checkEmpty();

    private String wordToTranslate;
    private boolean hasBeenCorrected = false, running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityPracticeBinding binding;
        TextView ResponseTextBox, infoTextBox;
        Button compareButtonVariable;


        super.onCreate(savedInstanceState);
        binding = ActivityPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ResponseTextBox = (TextView)findViewById(R.id.responseText);
        ResponseTextBox.setText("");
        infoTextBox = (TextView) findViewById(R.id.infoText);
        compareButtonVariable = (Button)findViewById(R.id.compareButton);

        init_file();
        set_text();

        binding.compareButton.setOnClickListener(view -> {
            if(!running)
            {
                Intent intent = new Intent(Practice.this, SelectFile.class);
                startActivity(intent);
            }

            if(hasBeenCorrected)
            {
                hasBeenCorrected = false;
                wordToTranslate = pickWord();
                set_text();
                binding.TranslationInputField.getEditText().setText("");
                compareButtonVariable.setText("Correct");
                ResponseTextBox.setText("");
            }
            else
            {
                String response = compare(binding.TranslationInputField.getEditText().getText().toString());
                ResponseTextBox.setText(response);
                hasBeenCorrected = true;
                compareButtonVariable.setText("Next word");
            }

            if(checkEmpty())
            {
                infoTextBox.setText("Wordset completed!");
                running = false;
                compareButtonVariable.setText("Practice other sets");
                set_text();
            }
        });
    }
}