package com.example.ordapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ordapp.databinding.ActivityChooseFileModeBinding;
import com.example.ordapp.databinding.ActivitySelectFileBinding;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ChooseFileMode extends AppCompatActivity {

    Button Translation, Original, EditWordset, ExportFile, DeleteWordset;
    ConstraintLayout layout;
    String fileNameWOextension, folder, filePathWOextension;
    static {
        System.loadLibrary("ordapp");
    }

    private final ActivityResultLauncher<Intent> ExportFileLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null) {

                            Uri uri = result.getData().getData();

                            try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                                String fileContent = Library.printFile(filePathWOextension);
                                os.write(fileContent.getBytes(StandardCharsets.UTF_8));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

    private void createButtons()
    {
        SharedPreferences prefs = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);

        float density = getResources().getDisplayMetrics().density;
        int buttonCount = 0, buttonSize = 180;

        Translation = Library.createButton(prefs, folder + "_" + fileNameWOextension + "_translation", this, density, layout, buttonSize, buttonCount, "Write spanish", true);
        buttonCount++;

        Original = Library.createButton(prefs, folder + "_" + fileNameWOextension + "_original", this, density, layout, buttonSize, buttonCount, "Write swedish", true);
        buttonCount++;

        EditWordset = Library.createButton(prefs, "", this, density, layout, buttonSize, buttonCount, "Edit wordset", false);
        buttonCount++;

        ExportFile = Library.createButton(prefs, "", this, density, layout, buttonSize, buttonCount, "Export wordset", false);
        buttonCount++;

        DeleteWordset = Library.createButton(prefs, "", this, density, layout, buttonSize, buttonCount, "Delete wordset", false);
    }

    private void setPreference()
    {
        SharedPreferences currentPrefs = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);
        int translationColor = Library.evauluatePref(currentPrefs, folder + "_" + fileNameWOextension + "_translation");
        int originalColor = Library.evauluatePref(currentPrefs, folder + "_" + fileNameWOextension + "_original");

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
        Library.setNextColor(currentValue, maxValue, prefs, folder + "_" + fileNameWOextension);
    }

    private void createUI()
    {
        Intent intent = getIntent();
        folder = intent.getStringExtra("FOLDER_NAME");
        String fileName = intent.getStringExtra("FILE_NAME");
        fileNameWOextension = fileName.substring(0, fileName.length() - 4);

        String filePath = new File(getFilesDir(), folder + "/" + fileName).getAbsolutePath();
        filePathWOextension = filePath.substring(0, filePath.length() - 4);

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

        ExportFile.setOnClickListener(view -> {
            // Exportera en fil
            Intent exportIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
            exportIntent.setType("text/plain");
            exportIntent.putExtra(Intent.EXTRA_TITLE, fileName);

            ExportFileLauncher.launch(exportIntent);
        });

        DeleteWordset.setOnClickListener(view -> {
            new androidx.appcompat.app.AlertDialog.Builder(ChooseFileMode.this)
                    .setTitle("Delete wordset")
                    .setMessage("Are you sure you want to delete this wordset?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        SharedPreferences filePref = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);
                        Library.DeleteFile(new File(filePath), filePref, fileNameWOextension, folder);
                        Library.createSummaryFile(getFilesDir(), folder);
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        getSupportActionBar().setTitle("Choose action in " + fileNameWOextension);
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