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
    int totalWords, totalCorrect, totalAnswered, redoAnswered, redoSize;
    Intent intent;
    public static final int EMPTY = 0, FIRST_TIME_DONE = 2;
    private TextView ResponseTextBox, infoTextBox, totalAnsweredBox;
    static {
        System.loadLibrary("ordapp");
    }

    private void init_file()
    {
        intent = getIntent();
        String filePath = intent.getStringExtra("FILE_PATH");
        String language_to_write_in = intent.getStringExtra("LANGUAGE");

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
        totalAnsweredBox = (TextView)findViewById(R.id.totalAnsweredText);
        totalAnsweredBox.setText("");
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
        totalAnswered = 0;
        redoAnswered = 0;
        redoSize = 0;

        binding.compareButton.setOnClickListener(view -> {

            if(!running)
            {
                SharedPreferences prefs = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);
                String button = intent.getStringExtra("LANGUAGE");
                String file = intent.getStringExtra("FILE_NAME");
                String key = file + "_" + button;

                Library.setPracticeColor(totalCorrect, totalWords, prefs, key);
                if(Library.getColor(prefs, key).equals("red"))
                {
                    String otherButton = "translation";
                    if(button.equals("translation"))
                    {
                        otherButton = "original";
                    }
                    String otherKey = file + "_" + otherButton;

                    if(Library.getColor(prefs, otherKey).equals("green"))
                    {
                        Library.setColor(prefs, otherKey, "yellow");
                    }
                }
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
                if(first_time && response.startsWith("Correct!"))
                {
                    totalCorrect++;
                }

                totalAnswered++;
                if(first_time)
                {
                    double percentage = ((double) totalAnswered / totalWords) * 100;
                    totalAnsweredBox.setText("Completed: " + totalAnswered + "/" + totalWords + String.format(" (%.1f%%)", percentage));
                }
                else
                {
                    double percentage = ((double) totalAnswered / redoSize) * 100;
                    totalAnsweredBox.setText("Completed: " + totalAnswered + "/" + redoSize + String.format(" (%.1f%%)", percentage));
                }
            }

            int status = Library.checkEmpty();
            if(status == EMPTY)
            {
                infoTextBox.setText("Wordset completed!");
                running = false;
                compareButtonVariable.setText("Practice other sets");

                double percentage = (((double) totalCorrect / totalWords)) * 100;
                ResponseTextBox.setText("You got " + totalCorrect + "/" + totalWords + " correct. That is " + String.format(" (%.1f%%)", percentage));
                if(Library.checkSize() == 0)
                {
                    totalAnsweredBox.setText("");
                }
                set_text();
            }
            else if(status == FIRST_TIME_DONE)
            {
                first_time = false;
                infoTextBox.setText("Redo your mistakes");
                totalAnswered = 0;
                redoSize = Library.checkSize();

                double percentage = ((double) totalAnswered / redoSize) * 100;
                totalAnsweredBox.setText("Completed: " + totalAnswered + "/" + redoSize + String.format(" (%.1f%%)", percentage));
            }
        });
    }
}