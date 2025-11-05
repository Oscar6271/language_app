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
    public native String printFile(String fileName);

    static {
        System.loadLibrary("ordapp");
    }

    private void DeleteFile(String fileName)
    {
        // Hämta filen
        File file = new File(getFilesDir(), fileName + ".txt");

        // Kontrollera om filen finns och ta bort den
        if(file.exists()){
            boolean deleted = file.delete();
            if(deleted){
                Log.d("FILE_DELETE", "Filen togs bort!");
            } else {
                Log.d("FILE_DELETE", "Kunde inte ta bort filen.");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChooseFileModeBinding binding = ActivityChooseFileModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("FILE_NAME");
        String fileWOextension = fileName.substring(0, fileName.length() - 4);

        binding.PracticeTranslation.setOnClickListener(view -> {
            Intent practiceTranslationIntent = new Intent(ChooseFileMode.this, Practice.class);
            practiceTranslationIntent.putExtra("FILE_NAME", fileWOextension);
            practiceTranslationIntent.putExtra("LANGUAGE", "translation");
            startActivity(practiceTranslationIntent);
        });

        binding.PracticeOriginal.setOnClickListener(view -> {
            Intent pracitceOriginalIntent = new Intent(ChooseFileMode.this, Practice.class);
            pracitceOriginalIntent.putExtra("FILE_NAME", fileWOextension);
            pracitceOriginalIntent.putExtra("LANGUAGE", "original");
            startActivity(pracitceOriginalIntent);
        });

        binding.EditWordSet.setOnClickListener(view -> {
            Intent editIntent = new Intent(ChooseFileMode.this, SimpleInput.class);
            editIntent.putExtra("FILE_NAME", fileWOextension);
            editIntent.putExtra("CONTENT", printFile(getFilesDir().getAbsolutePath() + "/" + fileWOextension));
            editIntent.putExtra("APPEND", false);
            startActivity(editIntent);
        });

        binding.DeleteWordSet.setOnClickListener(view -> {
            DeleteFile(fileWOextension);
            finish();
            startActivity(getIntent());
        });
    }
}