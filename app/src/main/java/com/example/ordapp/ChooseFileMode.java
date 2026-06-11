package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

        Intent intent = getIntent();
        String folder = intent.getStringExtra("FOLDER_NAME");
        String fileName = intent.getStringExtra("FILE_NAME");
        String filePath = new File(getFilesDir(), folder + "/" + fileName).getAbsolutePath();

        String filePathWOextension = filePath.substring(0, filePath.length() - 4);
        String fileNameWOextension = fileName.substring(0, fileName.length() - 4);

        binding.PracticeTranslation.setOnClickListener(view -> {
            Intent practiceTranslationIntent = new Intent(ChooseFileMode.this, Practice.class);
            practiceTranslationIntent.putExtra("FILE_PATH", filePathWOextension);
            practiceTranslationIntent.putExtra("LANGUAGE", "translation");
            Log.d("FILE", filePathWOextension);
            startActivity(practiceTranslationIntent);
        });

        binding.PracticeOriginal.setOnClickListener(view -> {
            Intent pracitceOriginalIntent = new Intent(ChooseFileMode.this, Practice.class);
            pracitceOriginalIntent.putExtra("FILE_PATH", filePathWOextension);
            pracitceOriginalIntent.putExtra("LANGUAGE", "original");
            startActivity(pracitceOriginalIntent);
        });

        binding.EditWordSet.setOnClickListener(view -> {
            Intent editIntent = new Intent(ChooseFileMode.this, SimpleInput.class);
            editIntent.putExtra("FILE_NAME", fileNameWOextension);
            editIntent.putExtra("CONTENT", Library.printFile(filePathWOextension));
            editIntent.putExtra("APPEND", false);
            editIntent.putExtra("FOLDER_NAME", folder);
            Log.d("FILE_NAME", filePathWOextension);
            startActivity(editIntent);
        });

        binding.DeleteWordSet.setOnClickListener(view -> {
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