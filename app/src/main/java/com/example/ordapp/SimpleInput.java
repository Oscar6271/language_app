package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ordapp.databinding.ActivitySimpleInputBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

public class SimpleInput extends AppCompatActivity {
    static {
        System.loadLibrary("ordapp");
    }
    private ActivitySimpleInputBinding binding;
    private TextView extraMessageText;
    private TextInputEditText fileEdit, contentEdit, folderText;
    private String fileName, folderName, content;
    private boolean append;

    /**
     * Tar fram det som har lagrats i Intent
     */
    private void GetIntent()
    {
        Intent intent = getIntent();
        fileName = intent.getStringExtra("FILE_NAME");
        content = intent.getStringExtra("CONTENT");
        append = intent.getBooleanExtra("APPEND", true);
        folderName = intent.getStringExtra("FOLDER_NAME");
    }

    /**
     * Tar fram textView och sätter dit text (mappens namn, filens innehåll och namn)
     */
    private void setText()
    {
        extraMessageText = findViewById(R.id.extraMessage);
        folderText  = findViewById(R.id.folderNameInput);
        fileEdit    = findViewById(R.id.fileNameInput);
        contentEdit = findViewById(R.id.SimpleInputText);

        if(fileName.equalsIgnoreCase("summary"))
        {
            extraMessageText.setText("EDITING NOT POSSIBLE in summary file");
        }

        folderText.setText(folderName);
        contentEdit.setText(content);
        fileEdit.setText(fileName);

        getSupportActionBar().setTitle("Edit file: " + fileName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySimpleInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ConstraintLayout layout = binding.main;
        GetIntent();

        setText();

        binding.createSimpleFileButton.setOnClickListener(v -> {
            folderName = folderText.getText().toString().trim();
            fileName   = fileEdit.getText().toString().trim();

            File folderFile = new File(getFilesDir(), folderName);

            // Skapar en folder
            if (!folderFile.exists())
            {
                folderFile.mkdirs();
            }

            File file = new File(folderFile, fileName);

            if(!fileName.isEmpty()) {
                String contentText = contentEdit.getText().toString();

                Library.writeToFile(file.getAbsolutePath(), contentText, append);
                Library.createSummaryFile(getFilesDir(), folderName);

                finish();

            } else {
                Library.createSnackBar(layout, "Input a filename to save wordset", 2000, 1800);
            }
        });
    }
}