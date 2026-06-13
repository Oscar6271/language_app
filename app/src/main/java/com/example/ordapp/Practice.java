package com.example.ordapp;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private boolean hasBeenCorrected = false, running = true, first_time = true;
    int totalWords, totalCorrect;
    Intent intent;
    public static final int EMPTY = 0;
    public static final int NOT_EMPTY = 1;
    public static final int FIRST_TIME_DONE = 2;
    private TextView ResponseTextBox, infoTextBox;
    static {
        System.loadLibrary("ordapp");
    }

    private void init_file()
    {
        intent = getIntent();
        String filePath = intent.getStringExtra("FILE_PATH");
        String language_to_write_in = intent.getStringExtra("LANGUAGE");
        Log.d("FILE PATH", filePath);

        totalWords = Library.readFile(filePath, language_to_write_in);
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

        totalCorrect = 0;

        binding.compareButton.setOnClickListener(view -> {

            if(!running)
            {
                SharedPreferences prefs = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);
                String button = intent.getStringExtra("LANGUAGE");
                String file = intent.getStringExtra("FILE_NAME");
                String key = file + "_" + button;

                Library.setPracticeColor(totalCorrect, totalWords, prefs, key);
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
                if(first_time && response.equals("Correct!"))
                {
                    totalCorrect++;
                }
            }

            int status = Library.checkEmpty();
            if(status == EMPTY)
            {
                infoTextBox.setText("Wordset completed!");
                running = false;
                compareButtonVariable.setText("Practice other sets");
                set_text();
            }
            else if(status == FIRST_TIME_DONE)
            {
                first_time = false;
                infoTextBox.setText("Redo your mistakes");
            }
        });
    }
}