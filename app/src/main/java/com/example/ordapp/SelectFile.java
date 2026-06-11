package com.example.ordapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.documentfile.provider.DocumentFile;

import com.example.ordapp.databinding.ActivitySelectFileBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SelectFile extends AppCompatActivity {
    private ConstraintLayout layout;  // Huvud-ConstraintLayout inuti ScrollView
    private int buttonCount = 0;     // För att positionera knappar vertikalt
    private String folder;

    int dpToPx(int dp)
    {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void DeleteFile(String folder, String fileName)
    {
        // Hämta filen
        File file = new File(getFilesDir() + "/" + folder, fileName);

        // Kontrollera om filen finns och ta bort den
        if(file.exists()){
            boolean deleted = file.delete();
        }
    }

    private void addView(Button choose, int size)
    {
        choose.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(size), dpToPx(70)
        );
        choose.setLayoutParams(btnParams);
        layout.addView(choose);
    }

    public void addConstraintSet(Button choose, int startMargin)
    {
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(80 + buttonCount * 100);
        mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin);
        mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        mainSet.applyTo(layout);
    }

    private Button addExtraButton(String buttonTitle, int startMargin)
    {
        Button choose = new Button(this);
        choose.setText(buttonTitle);

        addView(choose, 150);
        addConstraintSet(choose, startMargin);

        return choose;
    }
    private void createButtons(File file) {

        // 1. Skapa huvudknapp
        Button choose = new Button(this);
        String fileName = file.getName();
        choose.setText(fileName.substring(0, fileName.length() - 4));

        addView(choose, 180);
        addConstraintSet(choose, 0);

        buttonCount++;

        choose.setOnClickListener(view -> {
            Intent ChooseFileModeIntent = new Intent(SelectFile.this, ChooseFileMode.class);
            // Log.d("DEBUG", folder + "/" + file.getName());
            ChooseFileModeIntent.putExtra("FILE_NAME", fileName);
            ChooseFileModeIntent.putExtra("FOLDER_NAME", getIntent().getStringExtra("FOLDER_NAME"));
            startActivity(ChooseFileModeIntent);
        });
    }

    private void createDropDowns() {
        Log.d("FILES", "finding files");
        Intent intent = getIntent();
        String folderName = intent.getStringExtra("FOLDER_NAME");
        // Log.d("FOLDER", folderName);

        File folder = new File(getFilesDir(), folderName);
        File[] files = folder.listFiles();

        for (File file : files) {
            if (file.isFile() && !file.getName().equals("profileInstalled")) {
                createButtons(file);
            }
        }
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
    private void importSingleFile(DocumentFile file, File targetFolder)
    {
        if (file == null || !file.isFile()) return;

        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
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
    private final ActivityResultLauncher<String[]> multipleFilesLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenMultipleDocuments(),
                    uris -> {
                        for (Uri uri : uris) {

                            DocumentFile file =
                                    DocumentFile.fromSingleUri(this, uri);

                            if (file != null) {
                                importSingleFile(file, new File(getFilesDir(), folder));
                            }
                        }
                    }
            );
    private void createUI()
    {
        Intent intent = getIntent();

        folder = intent.getStringExtra("FOLDER_NAME");

        Button addFileButton = addExtraButton("Add file", 500);
        addFileButton.setOnClickListener(view -> {
            // gå till simple_input
            Intent simple_input_intent = new Intent(SelectFile.this, SimpleInput.class);
            simple_input_intent.putExtra("FOLDER_NAME", folder);
            startActivity(simple_input_intent);
        });

        Button deleteFolderButton = addExtraButton("Delete folder", -500);
        deleteFolderButton.setOnClickListener(view -> {
            deleteAlert(folder);
        });

        buttonCount++;

        Button importFile = addExtraButton("import file", 0);
        importFile.setOnClickListener(view -> {
            // importera en fil

            multipleFilesLauncher.launch(new String[]{"text/plain"});
        });

        buttonCount++;
        createDropDowns();
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
