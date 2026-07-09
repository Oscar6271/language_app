package com.example.ordapp;

import static com.google.android.material.snackbar.Snackbar.make;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordapp.databinding.ActivityPracticeBinding;
import com.google.android.material.snackbar.Snackbar;

public class Practice extends AppCompatActivity {
    public Button compareButtonVariable;
    private String wordToTranslate = "";

    private boolean hasBeenCorrected = false, running = true, first_time = true;
    int totalWords, totalCorrect, totalAnswered, redoAnswered, redoSize;
    Intent intent;
    ActivityPracticeBinding binding;
    String filePath;
    public static final int EMPTY = 0, FIRST_TIME_DONE = 2;

    private TextView ResponseTextBox, totalAnsweredBox;
    static {
        System.loadLibrary("ordapp");
    }

    private void init_file()
    {
        intent = getIntent();
        filePath = intent.getStringExtra("FILE_PATH");
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
        compareButtonVariable = (Button)findViewById(R.id.compareButton);
        totalAnsweredBox = (TextView)findViewById(R.id.totalAnsweredText);
        totalAnsweredBox.setText("");
    }

    private void resetGame()
    {
        SharedPreferences prefs = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);
        String button = intent.getStringExtra("LANGUAGE");
        String file = intent.getStringExtra("FILE_NAME");
        String key = file + "_" + button;

        Library.setNextColor(totalCorrect, totalWords, prefs, key);

        // om setNeen knapp är röd sätts och den andra grön sätt den gröna till gul
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

        String folder = intent.getStringExtra("FOLDER");
        if(Library.rewriteFile(filePath))
        {
            Library.createSummaryFile(getFilesDir(), folder);
        }
    }

    private void wordsetCompleted()
    {
        if(!running)
        {
            resetGame();
            finish();
        }
    }

    private void nextWord()
    {
        hasBeenCorrected = false;
        wordToTranslate = Library.pickWord();
        set_text();
        binding.TranslationInputField.getEditText().setText("");
        compareButtonVariable.setText("Check");
        ResponseTextBox.setText("");
        binding.IWasRightButton.setVisibility(View.INVISIBLE);
    }

    private void checkWord()
    {
        String input = binding.TranslationInputField.getEditText().getText().toString();
        String response = Library.compare(input);
        hasBeenCorrected = true;
        compareButtonVariable.setText("Next word");

        ResponseTextBox.setText(response);

        if(first_time && response.startsWith("Correct!"))
        {
            totalCorrect++;
        }

        if(response.startsWith("Wrong"))
        {
            binding.IWasRightButton.setVisibility(View.VISIBLE);
            IwasRightButton(input);
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

    private void checkStatus(int status)
    {
        if(status == EMPTY)
        {
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
            totalAnswered = 0;
            redoSize = Library.checkSize();

            double percentage = ((double) totalAnswered / redoSize) * 100;
            totalAnsweredBox.setText("Completed: " + totalAnswered + "/" + redoSize + String.format(" (%.1f%%)", percentage));
        }
    }
    private void compareButton()
    {
        binding.compareButton.setOnClickListener(view -> {

            wordsetCompleted();

            if(hasBeenCorrected)
            {
                nextWord();
            }
            else
            {
                checkWord();
            }

            checkStatus(Library.checkEmpty());
        });
    }

    private void createSnackBar(String message)
    {
        Snackbar snackbar = Snackbar.make(
                binding.getRoot(),
                message,
                Snackbar.LENGTH_SHORT);
        snackbar.setDuration(1000); // 1 sekund

        snackbar.setAction("Close", v -> snackbar.dismiss());

        View snackBarView = snackbar.getView();

        FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams) snackBarView.getLayoutParams();

        params.gravity = Gravity.TOP;
        params.topMargin = 600;

        snackBarView.setLayoutParams(params);

        snackbar.show();
    }

    private void IwasRightButton(String input)
    {
        binding.IWasRightButton.setOnClickListener(view -> {
            if(first_time || Library.wordsLeft() <= 1)
            {
                totalCorrect++;
            }
            ResponseTextBox.setText("");
            new androidx.appcompat.app.AlertDialog.Builder(Practice.this)
                    .setTitle("Add word")
                    .setMessage("Do you want to add " + input + " as alternative?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Library.addAlternative(input, wordToTranslate);
                        createSnackBar("Added " + input + " as alternative for " + wordToTranslate);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        Library.clean_wrong_lists();
                        createSnackBar("Answered corrected, but not added as alternative");

                        dialog.dismiss();
                    })
                    .show();
            if(Library.wordsLeft() <= 1 && Library.mistakes() == 0)
            {
                checkStatus(EMPTY);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        binding.IWasRightButton.setVisibility(View.INVISIBLE);

        compareButton();
    }
}