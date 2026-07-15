package com.example.ordapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordapp.databinding.ActivitySimpleInputBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

public class SimpleInput extends AppCompatActivity {
    static {
        System.loadLibrary("ordapp");
    }
    private ActivitySimpleInputBinding binding;
    private TextView errormessage, folderText;
    private TextInputEditText fileEdit, contentEdit;

    private String fileName, folderName, content;
    private boolean append;

    private void GetIntent()
    {
        Intent intent = getIntent();
        fileName = intent.getStringExtra("FILE_NAME");
        content = intent.getStringExtra("CONTENT");
        append = intent.getBooleanExtra("APPEND", true);
        folderName = intent.getStringExtra("FOLDER_NAME");
    }

    private void setText()
    {
        folderText  = findViewById(R.id.folderInput);
        fileEdit    = findViewById(R.id.fileNameInput);
        contentEdit = findViewById(R.id.SimpleInputText);

        errormessage = findViewById(R.id.errorMessageText);

        folderText.setText("Folder: " + folderName);
        contentEdit.setText(content);
        fileEdit.setText(fileName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySimpleInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GetIntent();

        setText();

        binding.createSimpleFileButton.setOnClickListener(v -> {
            folderName = folderText.getText().toString().substring(8).trim();
            fileName   = fileEdit.getText().toString().trim();

            File folderFile = new File(getFilesDir(), folderName);

            // Skapar en folder
            if (!folderFile.exists())
            {
                folderFile.mkdirs();
            }

            File file = new File(folderFile, fileName);

            if(!fileName.isEmpty()) {
                errormessage.setText("");
                String contentText = contentEdit.getText().toString();

                Library.writeToFile(file.getAbsolutePath(), contentText, append);
                Library.createSummaryFile(getFilesDir(), folderName);

                finish();

            } else {
                errormessage.setText("Input a filename to save wordset");
            }
        });
    }
}