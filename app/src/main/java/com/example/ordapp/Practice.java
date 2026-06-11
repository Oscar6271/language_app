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
    public Button compareButtonVariable;
    private String wordToTranslate;

    private boolean hasBeenCorrected = false, running = true;
    private TextView ResponseTextBox, infoTextBox;
    static {
        System.loadLibrary("ordapp");
    }

    private void init_file()
    {
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("FILE_PATH");
        String language_to_write_in = intent.getStringExtra("LANGUAGE");
        Log.d("FILE PATH", filePath);

        Library.readFile(filePath, language_to_write_in);
        wordToTranslate = Library.pickWord();
    }

    private void set_text()
    {
        TextView wordToTranslateTextBox;

        wordToTranslateTextBox = (TextView)findViewById(R.id.WordToTranslateText);
        wordToTranslateTextBox.setText(wordToTranslate);
    }

    private void setupButtons()
    {
        ResponseTextBox = (TextView)findViewById(R.id.responseText);
        ResponseTextBox.setText("");
        infoTextBox = (TextView) findViewById(R.id.infoText);
        compareButtonVariable = (Button)findViewById(R.id.compareButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityPracticeBinding binding;

        super.onCreate(savedInstanceState);
        binding = ActivityPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupButtons();

        init_file();
        set_text();
        Log.d("WORD", wordToTranslate);

        binding.compareButton.setOnClickListener(view -> {

            if(!running)
            {
                finish();
            }

            if(hasBeenCorrected)
            {
                hasBeenCorrected = false;
                wordToTranslate = Library.pickWord();
                set_text();
                binding.TranslationInputField.getEditText().setText("");
                compareButtonVariable.setText("Check");
                ResponseTextBox.setText("");
            }
            else
            {
                String response = Library.compare(binding.TranslationInputField.getEditText().getText().toString());
                ResponseTextBox.setText(response);
                hasBeenCorrected = true;
                compareButtonVariable.setText("Next word");
            }

            if(Library.checkEmpty())
            {
                infoTextBox.setText("Wordset completed!");
                running = false;
                compareButtonVariable.setText("Practice other sets");
                set_text();
            }
        });
    }
}