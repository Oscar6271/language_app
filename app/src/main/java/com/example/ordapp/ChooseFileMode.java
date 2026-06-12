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

    static {
        System.loadLibrary("ordapp");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChooseFileModeBinding binding = ActivityChooseFileModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ConstraintLayout layout = findViewById(R.id.main);

        Intent intent = getIntent();
        String folder = intent.getStringExtra("FOLDER_NAME");
        String fileName = intent.getStringExtra("FILE_NAME");
        String fileNameWOextension = fileName.substring(0, fileName.length() - 4);

        String filePath = new File(getFilesDir(), folder + "/" + fileName).getAbsolutePath();
        String filePathWOextension = filePath.substring(0, filePath.length() - 4);

        SharedPreferences prefs = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);

        float density = getResources().getDisplayMetrics().density;
        int buttonCount = 0, buttonSize = 180;

        Button Translation = Library.createButton(prefs, "translation", this, density, layout, buttonSize, buttonCount, "Practice translation");
        buttonCount++;

        Button Original = Library.createButton(prefs, "original", this, density, layout, buttonSize, buttonCount, "Practice original");
        buttonCount++;


        Button EditWordset = Library.createButton(prefs, "", this, density, layout, buttonSize, buttonCount, "Edit wordset");
        buttonCount++;

        Button DeleteWordset = Library.createButton(prefs, "", this, density, layout, buttonSize, buttonCount, "Delete wordset");
        buttonCount++;

        Translation.setOnClickListener(view -> {
            Intent practiceTranslationIntent = new Intent(ChooseFileMode.this, Practice.class);
            practiceTranslationIntent.putExtra("FILE_PATH", filePathWOextension);
            practiceTranslationIntent.putExtra("LANGUAGE", "translation");
            startActivity(practiceTranslationIntent);
        });

        Original.setOnClickListener(view -> {
            Intent pracitceOriginalIntent = new Intent(ChooseFileMode.this, Practice.class);
            pracitceOriginalIntent.putExtra("FILE_PATH", filePathWOextension);
            pracitceOriginalIntent.putExtra("LANGUAGE", "original");
            startActivity(pracitceOriginalIntent);
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
    }
}