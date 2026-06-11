package com.example.ordapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.documentfile.provider.DocumentFile;

import com.example.ordapp.databinding.ActivityChooseFolderBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ChooseFolder extends AppCompatActivity {

    int dpToPx(int dp)
    {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    int buttonCount = 0;
    ConstraintLayout layout;

    private void addView(Button choose)
    {
        choose.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(150), dpToPx(70)
        );
        choose.setLayoutParams(btnParams);
        layout.addView(choose);
    }

    private void addConstraintSet(Button choose, int startMargin)
    {
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(80 + buttonCount * 80);
        mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin);
        mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        mainSet.applyTo(layout);
        buttonCount++;

    }

    private void addView(EditText choose)
    {
        choose.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(150), dpToPx(70)
        );
        choose.setLayoutParams(btnParams);
        layout.addView(choose);
    }

    private void addConstraintSet(EditText choose, int startMarging)
    {
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(buttonCount * 140);
        mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMarging);
        mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        mainSet.applyTo(layout);
        buttonCount++;
    }

    private void importFile(DocumentFile file, File targetFolder)
    {
        if (!file.isFile()) {
            return;
        }

        File targetFile = new File(targetFolder, file.getName());

        try (
                InputStream in = getContentResolver().openInputStream(file.getUri());
                FileOutputStream out = new FileOutputStream(targetFile)
        ) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void importFolder(Uri treeUri)
    {
        DocumentFile folder = DocumentFile.fromTreeUri(this, treeUri);

        if (folder == null) {
            return;
        }

        String folderName = folder.getName();

        File targetFolder = new File(getFilesDir(), folderName);
        if(!targetFolder.exists())
        {
            targetFolder.mkdirs();
        }

        // Här kan du kopiera filerna till getFilesDir()
        // eller bara skriva ut vad som finns i mappen.

        for (DocumentFile file : folder.listFiles()) {
            if (file.isFile()) {
                importFile(file, targetFolder);
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
        Button addFolder = new Button(this);
        addFolder.setText("New folder");
        addView(addFolder);
        addConstraintSet(addFolder, 500);

        EditText textField = new EditText(this);
        textField.setHint("Name of folder");
        addView(textField);
        addConstraintSet(textField, 500);

        addFolder.setOnClickListener(view -> {
            String folderName = textField.getText().toString().trim();
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

        buttonCount -= 2;

        Button importFolder = new Button(this);
        importFolder.setText("Import folder");
        addView(importFolder);
        addConstraintSet(importFolder, -500);

        importFolder.setOnClickListener(view -> {
            // importera en mapp

            importFolderLauncher.launch(null);
        });

        buttonCount++;
    }

    private void displayFolders()
    {
        File[] files = getFilesDir().listFiles();

        for(File file : files) {
            // Log.d("DEBUG", "File: " + file.getName());
            if(file.isDirectory()) {
                Button choose = new Button(this);
                choose.setText(file.getName());

                addView(choose);
                addConstraintSet(choose, 0);


                choose.setOnClickListener(view -> {
                    Intent intent = new Intent(ChooseFolder.this, SelectFile.class);
                    intent.putExtra("FOLDER_NAME", file.getName());
                    startActivity(intent);
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

        createUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout.removeAllViews();

        createUI();
    }
}