package com.example.ordapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    static {
        System.loadLibrary("ordapp");
    }
    ActivityChooseFolderBinding binding;
    float density;
    int buttonCount = 0;
    long daysPassed = 0;
    ConstraintLayout layout;
    String folderName;

    /**
     * Går igenom alla filerna i en mapp och sätter färg på filens translation
     * och original knappar
     *
     * @param folder Mappen för filerna man ska sätta färg på
     * @param color Den färg som filerna ska få
     */
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

                // Sätt filen, translation och original knapparna till den nya färgen
                Library.setColor(prefs, folder + "_" + fileNameWOextension, color);
                Library.setColor(FileButtonPrefs, folder + "_" + fileNameWOextension + "_translation", color);
                Library.setColor(FileButtonPrefs, folder + "_" + fileNameWOextension + "_original", color);}
        }
    }

    /**
     * Kollar om datumet då en mapp har klarats av är 7, 14, eller 21 dagar sen.
     * Sätter då mappens färg till gul (vid 14 dagar) eller röd (vid 21 dagar).
     * Använder setAllFilesColor
     *
     * @param prefs SharedPreferences mappen
     * @param folder Mappen som man eventuellt ändrar färg på
     */
    private void resetColor(SharedPreferences prefs, String folder)
    {
        String savedDate = prefs.getString(folder + "_LAST_COMPLETED_DATE",null);

        // daysPassedPref läses i SelectFile för att dubbelkolla att den här
        // funktionen används rätt
        SharedPreferences daysPassedPref = getSharedPreferences("DAYS_PASSED", MODE_PRIVATE);

        if(savedDate == null || savedDate.isEmpty())
        {
            // Om det inte finns ett sparat datum reseta preferencen
            daysPassedPref.edit().putString(folder + "_date", "").apply();
            daysPassedPref.edit().putLong(folder + "_daysPassed", 0).apply();
            return;
        }

        LocalDate completedDate = LocalDate.parse(savedDate);
        daysPassed =
                java.time.temporal.ChronoUnit.DAYS.between(
                        completedDate,
                        LocalDate.now()
                );

        daysPassedPref.edit().putLong(folder + "_daysPassed", daysPassed).apply();
        daysPassedPref.edit().putString(folder + "_date", savedDate).apply();

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
                    FileChangedprefs.edit().putBoolean(folderName + "_any_file", false).apply();

                    // Ta bort filen
                    SharedPreferences filePref = getSharedPreferences("ChooseFileMode", MODE_PRIVATE);
                    Library.DeleteFile(file, filePref, file.getName().substring(0, file.getName().length() - 4), folderName);
                }
            }
        }

        // reseta datumet för mappen
        SharedPreferences CompletedPrefs = getSharedPreferences("ChooseFolder", MODE_PRIVATE);
        Library.resetDate(CompletedPrefs, folderName + "_LAST_COMPLETED_DATE");

        return folder_file.delete();
    }

    private void ConfirmDialog(DocumentFile folder, File targetFolder) {
        new androidx.appcompat.app.AlertDialog.Builder(ChooseFolder.this)
                .setTitle("Import Folder?")
                .setMessage("Do you want to replace " + folder.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Här kan du kopiera filerna till getFilesDir()
                    // eller bara skriva ut vad som finns i mappen
                    deleteFolder(targetFolder);
                    targetFolder.mkdirs();

                    for (DocumentFile file : folder.listFiles()) {
                        if (file.isFile()) {
                            Library.importFile(file, targetFolder, this);
                        }
                    }
                    Library.createSnackBar(layout, "Replaced " + folder.getName(), 2000, 1800);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    Library.createSnackBar(layout, folder.getName() + " was not imported", 2000, 1800);

                    dialog.dismiss();
                })
                .show();
    }

    /**
     * Importerar en mapp genom att gå igenom alla filer i mappen och
     * kallar på Library.ImportFile
     *
     * @param treeUri Mappen som ska importeras
     */
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
            // Här kan du kopiera filerna till getFilesDir()
            // eller bara skriva ut vad som finns i mappen
            for (DocumentFile file : folder.listFiles()) {
                if (file.isFile()) {
                    Library.importFile(file, targetFolder, this);
                }
            }
        }
        else
        {
            ConfirmDialog(folder, targetFolder);
        }


    }

    /**
     * Startar en launcher för att användaren ska kunna välja mapp
     * från filhanteraren i mobilen
     */
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

    /**
     * Öppnar en file genom att starta SelectFile
     *
     * @param folder Den mapp som ska öppnas
     */
    private void openFolder(String folder)
    {
        Intent intent = new Intent(ChooseFolder.this, SelectFile.class);
        intent.putExtra("FOLDER_NAME", folder);
        intent.putExtra(folder + "_daysPassed", daysPassed);
        startActivity(intent);
    }

    /**
     * Lägger till knappar och OnClickListener för att kunna
     * skapa och importera en ny mapp
     */
    private void createFolder()
    {
        buttonCount++;

        // Skapa textField där man skriver in mappens namn
        EditText textField = new EditText(this);
        textField.setHint("Name of folder");
        Library.addView(textField, density, layout);
        Library.addConstraintSet(textField, 500, layout, buttonCount, density);
        buttonCount--;

        // skapa en knapp och ett textfält under knappen, knappen ska köra raderna ovanför
        Button addFolder = Library.addExtraButton("New folder", 500, density, layout, buttonCount, this);

        addFolder.setOnClickListener(view -> {
            folderName = textField.getText().toString().trim();
            File folderFile = new File(getFilesDir(), folderName);

            textField.setText("");

            // Skapar en mapp
            if (!folderFile.exists())
            {
                folderFile.mkdirs();
                openFolder(folderName);
            }
            else
            {
                Library.createSnackBar(layout, "Folder already exists", 2500, 950);
            }
        });


        // Knapp för att importera en mapp
        Button importFolder = Library.addExtraButton("Import folder", -500, density, layout, buttonCount, this);

        importFolder.setOnClickListener(view -> {
            // importera en mapp
            importFolderLauncher.launch(null);
        });

        buttonCount += 2;
    }
    /**
     * Går igenom alla mappar, skapar knappar och OnClickListeners som startar SelectFile
     */
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
                    openFolder(folder);
                });
            }
        }
    }

    /**
     * Skapar UI, används från OnCreate och OnResume
     */
    private void createUI()
    {
        buttonCount = 0;
        createFolder();
        displayFolders();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChooseFolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        layout = findViewById(R.id.main);
        density = getResources().getDisplayMetrics().density;

        createUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ta bort UI:n för att sen bygga upp den igenom
        // Behövs för att saker (färger) kan ha uppdaterats
        layout.removeAllViews();

        createUI();
    }
}