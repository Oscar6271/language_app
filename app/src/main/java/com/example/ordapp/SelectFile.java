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
import java.time.LocalDate;


public class SelectFile extends AppCompatActivity {
    private ConstraintLayout layout;  // Huvud-ConstraintLayout inuti ScrollView
    private int buttonCount = 0;     // För att positionera knappar vertikalt
    private String folder, fileNameWOextension;
    float density;
    boolean wasGreen;
    Intent intent;

    /**
     * Skapar knapp för en fil
     */
    private void createButtons(File file, SharedPreferences prefs) {
        String fileName = file.getName();
        fileNameWOextension = fileName.substring(0, fileName.length() - 4);

        Button button = Library.createButton(prefs, folder + "_" + fileNameWOextension, this, density, layout, 180, buttonCount, fileNameWOextension, true);
        buttonCount++;

        button.setOnClickListener(view -> {
            Intent ChooseFileModeIntent = new Intent(SelectFile.this, ChooseFileMode.class);
            ChooseFileModeIntent.putExtra("FILE_NAME", fileName);
            ChooseFileModeIntent.putExtra("FOLDER_NAME", getIntent().getStringExtra("FOLDER_NAME"));
            startActivity(ChooseFileModeIntent);
        });
    }

    /**
     * Går igenom hela mappen och lägger till knappar för varje fil genom att kalla på createButtons.
     * Räknar ut ett medelvärde för alla filers färg och bestämmer vilken färg mappen ska få
     */
    private void addFileButtons() {
        String folderName = intent.getStringExtra("FOLDER_NAME");

        File folder = new File(getFilesDir(), folderName);
        File[] files = folder.listFiles();
        SharedPreferences currentPrefs = getSharedPreferences("SelectFile", MODE_PRIVATE);
        int currentValue = 0;
        int maxValue = 0;
        // läs in vad varje fil har för färg och räkna ut medelvärde
        for (File file : files) {
            if (file.isFile() && !file.getName().equals("profileInstalled")) {
                Log.d("FILE", file.getName());
                createButtons(file, currentPrefs);
                maxValue += Library.GREEN;
                currentValue += Library.evauluatePref(currentPrefs, folderName + "_" + fileNameWOextension);
            }
        }
        // skriv det medelvärdet till mappen
        SharedPreferences prefs = getSharedPreferences("ChooseFolder", MODE_PRIVATE);
        Library.setNextColor(currentValue, maxValue, prefs, folderName);
    }

    /**
     * Skapar en alert innan man tar bort en mapp
     */
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
    /**
     * Tar bort mappen och alla filer i den
     */
    private boolean deleteFolder(File folder_file) {
        if (folder_file == null || !folder_file.exists())
        {
            return false;
        }

        File[] files = folder_file.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    // Sätt att ingen fil har uppdaterats
                    SharedPreferences FileChangedprefs = getSharedPreferences("file_updated", MODE_PRIVATE);
                    FileChangedprefs.edit().putBoolean(folder + "_any_file", false).apply();

                    // Ta bort filen
                    SharedPreferences filePref = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);
                    Library.DeleteFile(file, filePref, file.getName().substring(0, file.getName().length() - 4), folder);
                }
            }
        }

        // reseta datumet för mappen
        SharedPreferences CompletedPrefs = getSharedPreferences("ChooseFolder", MODE_PRIVATE);
        Library.resetDate(CompletedPrefs, folder + "_LAST_COMPLETED_DATE");

        return folder_file.delete();
    }

    /**
     * Launcher för att importera flera filer samtidigt
     */
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
                    }
            );
    /**
     * Skriver ut det datum som mappan har klarats av på och antal dagar sen avklarad
     */
    private void addDateText() {
        // använd den här SharedPreferences för att kunna uppdatera texten utan att gå till ChooseFolder först
        SharedPreferences CompletedPrefs = getSharedPreferences("ChooseFolder", MODE_PRIVATE);
        String dateString = CompletedPrefs.getString(folder + "_LAST_COMPLETED_DATE", "");

        SharedPreferences daysPassedPref = getSharedPreferences("DAYS_PASSED", MODE_PRIVATE);
        String days = String.valueOf(daysPassedPref.getLong(folder + "_daysPassed", 0));

        String text = "";

        Log.d("DATE", dateString);

        if(!dateString.isEmpty())
        {
            LocalDate date = LocalDate.parse(dateString);
            int day = date.getDayOfMonth();
            String month = date.getMonth().toString().toLowerCase();
            int year = date.getYear();
            text = folder + " was completed on:\n" + day + " " +
                    month + " " + year + " (" + days + " days ago)";
        }
        else
        {
            text = folder + " has not been completed yet";
        }
        Library.addTextView(layout, this, text);
    }

    /**
     * Lägger till knapparna Delete folder, Add file och Import file
     */
    private void addExtraButtons()
    {
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
    }

    /**
     * Lägger till knappar och OnClickListener för att filer i mappen.
     * Lägger även till knapparna Delete folder, Add file och Import file
     */
    private void createUI()
    {
        Intent intent = getIntent();
        density = getResources().getDisplayMetrics().density;
        folder = intent.getStringExtra("FOLDER_NAME");

        addExtraButtons();

        addFileButtons();

        getSupportActionBar().setTitle("Choose file from " + folder);

        addDateText();
    }

    /**
     * Kollar om alla filer i mappen är gröna
     *
     * @return true om alla filer i mappen är gröna, annars false.
     */
    private boolean isAllFilesGreen()
    {
        String folderName = intent.getStringExtra("FOLDER_NAME");

        if(folderName == null)
        {
            return false;
        }

        File folder = new File(getFilesDir(), folderName);
        File[] files = folder.listFiles();

        if(files == null)
        {
            return false;
        }

        SharedPreferences currentPrefs = getSharedPreferences("SelectFile", MODE_PRIVATE);
        for (File file : files) {
            if (file.isFile() && !file.getName().equals("profileInstalled")) {
                String fileName = file.getName();
                fileNameWOextension = fileName.substring(0, fileName.length() - 4);
                int color = Library.evauluatePref(currentPrefs, folderName + "_" + fileNameWOextension);

                if(color != Library.GREEN) {
                     return false;
                }
            }
        }
        return true;
    }

    /**
     * Sparar dagens datum för en mapp
     */
    private void saveDate()
    {
        SharedPreferences CompletedPrefs = getSharedPreferences("ChooseFolder", MODE_PRIVATE);
        LocalDate today = LocalDate.now();

        CompletedPrefs.edit()
                .putString(folder + "_LAST_COMPLETED_DATE", today.toString())
                .apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectFileBinding binding = ActivitySelectFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();

        // ScrollView finns i XML, ConstraintLayout som child
        layout = findViewById(R.id.main);
        createUI();
        wasGreen = isAllFilesGreen();
    }

    /**
     * Återskapar UI:n.
     * Kollar om alla filer i mappen precis har blivit gröna och sparar då datumet.
     * Datumet resetas om man har gjort om en fil i mappen.
     */
    @Override
    protected void onResume() {
        super.onResume();
        layout.removeAllViews();
        buttonCount = 0;

        // om alla filer precis har blivit gröna sparar man datumet
        // annars kollar man om en fil har uppdaterats och nollställer då datumet
        boolean isGreen = isAllFilesGreen();

        if(!wasGreen && isGreen) {
            saveDate();
        }
        else if(!(wasGreen && isGreen))
        {
            SharedPreferences FileChangedprefs = getSharedPreferences("file_updated", MODE_PRIVATE);
            boolean fileUpdated = FileChangedprefs.getBoolean(folder + "_any_file", false);

            // om någon fil i mappen har uppdaterats resetas datumet
            if(fileUpdated) {
                FileChangedprefs.edit().putBoolean(folder + "_any_file", false).apply();
                SharedPreferences CompletedPrefs = getSharedPreferences("ChooseFolder", MODE_PRIVATE);
                Library.resetDate(CompletedPrefs, folder + "_LAST_COMPLETED_DATE");
            }
        }

        createUI();
        wasGreen = isGreen;
    }
}
