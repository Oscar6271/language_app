package com.example.ordapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ordapp.databinding.ActivityChooseFileModeBinding;
import com.example.ordapp.databinding.ActivitySelectFileBinding;

import java.io.File;

public class ChooseFileMode extends AppCompatActivity {

    Button Translation, Original, EditWordset, DeleteWordset;
    ConstraintLayout layout;
    String fileNameWOextension;
    static {
        System.loadLibrary("ordapp");
    }

    private void createButtons()
    {
        SharedPreferences prefs = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);

        float density = getResources().getDisplayMetrics().density;
        int buttonCount = 0, buttonSize = 180;

        Translation = Library.createButton(prefs, fileNameWOextension + "_translation", this, density, layout, buttonSize, buttonCount, "Practice translation", true);
        buttonCount++;

        Original = Library.createButton(prefs, fileNameWOextension + "_original", this, density, layout, buttonSize, buttonCount, "Practice original", true);
        buttonCount++;

        EditWordset = Library.createButton(prefs, "", this, density, layout, buttonSize, buttonCount, "Edit wordset", false);
        buttonCount++;

        DeleteWordset = Library.createButton(prefs, "", this, density, layout, buttonSize, buttonCount, "Delete wordset", false);
    }

    private void setPreference()
    {
        SharedPreferences currentPrefs = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);

        int translationColor = Library.evauluatePref(currentPrefs, fileNameWOextension + "_translation");
        int originalColor = Library.evauluatePref(currentPrefs, fileNameWOextension + "_original");

        if(translationColor == Library.RED && originalColor == Library.GREEN)
        {
            originalColor = Library.YELLOW;
        }
        else if(originalColor == Library.RED && translationColor == Library.GREEN)
        {
            translationColor = Library.YELLOW;
        }

        int maxValue = 2 * Library.GREEN;
        int currentValue = translationColor + originalColor;

        SharedPreferences prefs = getSharedPreferences("SelectFile", MODE_PRIVATE);
        Library.setNextColor(currentValue, maxValue, prefs, fileNameWOextension);
    }

    private void createUI()
    {
        Intent intent = getIntent();
        String folder = intent.getStringExtra("FOLDER_NAME");
        String fileName = intent.getStringExtra("FILE_NAME");
        fileNameWOextension = fileName.substring(0, fileName.length() - 4);

        String filePath = new File(getFilesDir(), folder + "/" + fileName).getAbsolutePath();
        String filePathWOextension = filePath.substring(0, filePath.length() - 4);

        createButtons();

        setPreference();

        Intent practiceIntent = new Intent(ChooseFileMode.this, Practice.class);
        practiceIntent.putExtra("FILE_PATH", filePathWOextension);
        practiceIntent.putExtra("FILE_NAME", fileNameWOextension);
        practiceIntent.putExtra("FOLDER", folder);

        Translation.setOnClickListener(view -> {
            practiceIntent.putExtra("LANGUAGE", "translation");
            startActivity(practiceIntent);
        });

        Original.setOnClickListener(view -> {
            practiceIntent.putExtra("LANGUAGE", "original");
            startActivity(practiceIntent);
        });

        EditWordset.setOnClickListener(view -> {
            Intent editIntent = new Intent(ChooseFileMode.this, SimpleInput.class);
            editIntent.putExtra("FILE_NAME", fileNameWOextension);
            editIntent.putExtra("CONTENT", Library.printFile(filePathWOextension));
            editIntent.putExtra("APPEND", false);
            editIntent.putExtra("FOLDER_NAME", folder);
            startActivity(editIntent);
        });

        DeleteWordset.setOnClickListener(view -> {
            new androidx.appcompat.app.AlertDialog.Builder(ChooseFileMode.this)
                    .setTitle("Delete wordset")
                    .setMessage("Are you sure you want to delete this wordset?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        Library.DeleteFile(new File(filePath));
                        Library.createSummaryFile(getFilesDir(), folder);
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        String pathName = folder + "/" + fileName;
        String pathNameWOextension = pathName.substring(0, pathName.length() - 4);
        getSupportActionBar().setTitle("Choose action in " + pathNameWOextension);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChooseFileModeBinding binding = ActivityChooseFileModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        layout = findViewById(R.id.main);

        createUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout.removeAllViews();

        createUI();
    }
}