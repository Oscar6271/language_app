package com.example.ordapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.documentfile.provider.DocumentFile;

import com.example.ordapp.databinding.ActivityChooseFolderBinding;

import java.io.File;
import java.time.LocalDate;

public class ChooseFolder extends AppCompatActivity {

    float density;
    int buttonCount = 0;
    long daysPassed = 0;
    ConstraintLayout layout;
    String folderName;

    private void setAllFilesColor(String folder, String color)
    {
        File folderFile = new File(getFilesDir(), folder);
        File[] files = folderFile.listFiles();
        if(files == null)
        {
            return;
        }

        SharedPreferences prefs = getSharedPreferences("SelectFile", MODE_PRIVATE);
        SharedPreferences FileButtonPrefs = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);

        for (File file : files) {
            if (file.isFile() && !file.getName().equals("profileInstalled")) {
                String fileName = file.getName();
                String fileNameWOextension = fileName.substring(0, fileName.length() - 4);

                Library.setColor(prefs, fileNameWOextension, color);
                Library.setColor(FileButtonPrefs, fileNameWOextension + "_translation", color);
                Library.setColor(FileButtonPrefs, fileNameWOextension + "_original", color);}
        }
    }
    private void resetColor(SharedPreferences prefs, String folder)
    {
        String savedDate = prefs.getString(
                folder + "_LAST_COMPLETED_DATE",
                null
        );

        if(savedDate == null || savedDate.isEmpty())
        {
            return;
        }

        LocalDate completedDate = LocalDate.parse(savedDate);
        daysPassed =
                java.time.temporal.ChronoUnit.DAYS.between(
                        completedDate,
                        LocalDate.now()
                );
        SharedPreferences daysPassedPref = getSharedPreferences("DAYS_PASSED", MODE_PRIVATE);
        daysPassedPref.edit().putLong(folder + "_daysPassed", daysPassed).apply();

        if(daysPassed < 7 )
        {
            return;
        }
        else if(daysPassed < 14)
        {
            Library.setColor(prefs, folder, "yellow");
            setAllFilesColor(folder, "yellow");
        }
        else
        {
            Library.setColor(prefs, folder, "red");
            setAllFilesColor(folder, "red");
        }
    }

    private void importFolder(Uri treeUri)
    {
        DocumentFile folder = DocumentFile.fromTreeUri(this, treeUri);

        if (folder == null) {
            return;
        }

        folderName = folder.getName();

        File targetFolder = new File(getFilesDir(), folderName);
        if(!targetFolder.exists())
        {
            targetFolder.mkdirs();
        }

        // Här kan du kopiera filerna till getFilesDir()
        // eller bara skriva ut vad som finns i mappen
        for (DocumentFile file : folder.listFiles()) {
            if (file.isFile()) {
                Library.importFile(file, targetFolder, this);
            }
        }
    }

    private final ActivityResultLauncher<Uri> importFolderLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocumentTree(),
                    uri -> {
                        if (uri != null) {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );

                            importFolder(uri);
                        }
                    }
            );

    private void createFolder()
    {
        // skapa en knapp och ett textfält under knappen, knappen ska köra raderna ovanför
        Button addFolder = Library.addExtraButton("New folder", 500, density, layout, buttonCount, this);
        buttonCount++;

        EditText textField = new EditText(this);
        textField.setHint("Name of folder");
        Library.addView(textField, density, layout);
        Library.addConstraintSet(textField, 500, layout, buttonCount, density);
        buttonCount--;

        addFolder.setOnClickListener(view -> {
            folderName = textField.getText().toString().trim();
            File folderFile = new File(getFilesDir(), folderName);

            // Skapar en folder
            if (!folderFile.exists())
            {
                folderFile.mkdirs();
                textField.setText("");
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            else
            {
                textField.setText("Folder already exists");
            }
        });

        Button importFolder = Library.addExtraButton("Import folder", -500, density, layout, buttonCount, this);

        importFolder.setOnClickListener(view -> {
            // importera en mapp
            importFolderLauncher.launch(null);
        });

        buttonCount += 2;
    }

    private void displayFolders()
    {
        File[] files = getFilesDir().listFiles();
        SharedPreferences prefs = getSharedPreferences("ChooseFolder", MODE_PRIVATE);

        for(File file : files) {
            if(file.isDirectory()) {
                String folder = file.getName();

                resetColor(prefs, folder);
                Button folderButton = Library.createButton(prefs, folder, this, density, layout, 150, buttonCount, folder, true);
                buttonCount++;

                folderButton.setOnClickListener(view -> {
                    Intent intent = new Intent(ChooseFolder.this, SelectFile.class);
                    intent.putExtra("FOLDER_NAME", file.getName());
                    startActivity(intent);
                    intent.putExtra(folder + "_daysPassed", daysPassed);
                });
            }
        }
    }

    private void createUI()
    {
        buttonCount = 0;
        createFolder();
        displayFolders();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChooseFolderBinding binding = ActivityChooseFolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        layout = findViewById(R.id.main);
        density = getResources().getDisplayMetrics().density;

        createUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout.removeAllViews();

        createUI();
    }
}