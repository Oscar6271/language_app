package com.example.ordapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.documentfile.provider.DocumentFile;

import com.example.ordapp.databinding.ActivitySelectFileBinding;

import java.io.File;


public class SelectFile extends AppCompatActivity {
    private ConstraintLayout layout;  // Huvud-ConstraintLayout inuti ScrollView
    private int buttonCount = 0, lastCompletedWeek;     // För att positionera knappar vertikalt
    private String folder, fileNameWOextension;
    float density;

    private void createButtons(File file, SharedPreferences prefs) {
        String fileName = file.getName();
        fileNameWOextension = fileName.substring(0, fileName.length() - 4);

        Button button = Library.createButton(prefs, fileNameWOextension, this, density, layout, 180, buttonCount, fileNameWOextension);
        buttonCount++;

        button.setOnClickListener(view -> {
            Intent ChooseFileModeIntent = new Intent(SelectFile.this, ChooseFileMode.class);
            ChooseFileModeIntent.putExtra("FILE_NAME", fileName);
            ChooseFileModeIntent.putExtra("FOLDER_NAME", getIntent().getStringExtra("FOLDER_NAME"));
            startActivity(ChooseFileModeIntent);
        });
    }

    private void createDropDowns() {
        Intent intent = getIntent();
        String folderName = intent.getStringExtra("FOLDER_NAME");

        File folder = new File(getFilesDir(), folderName);
        File[] files = folder.listFiles();
        SharedPreferences currentPrefs = getSharedPreferences("SelectFile", MODE_PRIVATE);
        int currentValue = 0;
        int maxValue = 0;
        for (File file : files) {
            if (file.isFile() && !file.getName().equals("profileInstalled")) {
                createButtons(file, currentPrefs);
                maxValue += 3;
                currentValue += Library.evauluatePref(currentPrefs, fileNameWOextension);
            }
        }

        SharedPreferences prefs = getSharedPreferences("ChooseFolder", MODE_PRIVATE);
        Library.setNextColor(currentValue, maxValue, prefs, folderName);
    }

    private void deleteAlert(String folderName)
    {
        File folder = new File(getFilesDir(), folderName);
        int filesCount = folder.listFiles().length;

        new androidx.appcompat.app.AlertDialog.Builder(SelectFile.this)
                .setTitle("Delete folder")
                .setMessage("Are you sure you want to delete this folder? There are " + filesCount + " files in this folder")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteFolder(folder);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    private boolean deleteFolder(File folder) {
        if (folder == null || !folder.exists())
        {
            return false;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }

        return folder.delete();
    }

    private final ActivityResultLauncher<String[]> multipleFilesLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenMultipleDocuments(),
                    uris -> {
                        for (Uri uri : uris) {

                            DocumentFile file =
                                    DocumentFile.fromSingleUri(this, uri);

                            if (file != null) {
                                Library.importFile(file, new File(getFilesDir(), folder), this);
                            }
                        }
                        Library.createSummaryFile(getFilesDir(), folder);

                    }
            );
    private void createUI()
    {
        Intent intent = getIntent();
        density = getResources().getDisplayMetrics().density;

        folder = intent.getStringExtra("FOLDER_NAME");

        Button addFileButton = Library.addExtraButton("Add file", 500, density, layout, buttonCount, this);
        addFileButton.setOnClickListener(view -> {
            // gå till simple_input
            Intent simple_input_intent = new Intent(SelectFile.this, SimpleInput.class);
            simple_input_intent.putExtra("FOLDER_NAME", folder);
            startActivity(simple_input_intent);
        });

        Button deleteFolderButton = Library.addExtraButton("Delete folder", -500, density, layout, buttonCount, this);
        deleteFolderButton.setOnClickListener(view -> {
            deleteAlert(folder);
        });

        buttonCount++;

        Button importFile = Library.addExtraButton("import file", 0, density, layout, buttonCount, this);
        importFile.setOnClickListener(view -> {
            // importera en fil
            multipleFilesLauncher.launch(new String[]{"text/plain"});
        });

        buttonCount++;
        createDropDowns();

        getSupportActionBar().setTitle("Choose file from " + folder);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectFileBinding binding = ActivitySelectFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ScrollView finns i XML, ConstraintLayout som child
        layout = findViewById(R.id.main);
        createUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        layout.removeAllViews();
        buttonCount = 0;

        createUI();
    }
}
