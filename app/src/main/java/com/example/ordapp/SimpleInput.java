package com.example.ordapp;

import android.content.Intent;
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
    private TextView errormessage;

    public native void writeToFile(String fileName, String contentToWrite, boolean append);
    private String fileName;
    private String folderName;
    private boolean append;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySimpleInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        fileName = intent.getStringExtra("FILE_NAME");
        String content = intent.getStringExtra("CONTENT");
        append = intent.getBooleanExtra("APPEND", true);
        folderName = intent.getStringExtra("FOLDER_NAME");

        TextInputEditText folderEdit  = findViewById(R.id.folderNameInput);
        TextInputEditText fileEdit    = findViewById(R.id.fileNameInput);
        TextInputEditText contentEdit = findViewById(R.id.SimpleInputText);

        errormessage = findViewById(R.id.errorMessageText);

        folderEdit.setText(folderName);
        contentEdit.setText(content);
        fileEdit.setText(fileName);

        binding.createSimpleFileButton.setOnClickListener(v -> {
            folderName = folderEdit.getText().toString().trim();
            fileName   = fileEdit.getText().toString().trim();

            File folderFile = new File(getFilesDir(), folderName);
            if (!folderFile.exists()) folderFile.mkdirs();

            File file = new File(folderFile, fileName);

            if(!fileName.isEmpty()) {
                errormessage.setText("");
                String contentText = contentEdit.getText().toString();
                writeToFile(file.getAbsolutePath(), contentText, append);                Log.d("WRITE", "written: " + getFilesDir().getAbsolutePath() + "/" + folderName + "/" + fileName);
                finish();
            } else {
                errormessage.setText("Input a filename to save wordset");
            }

            Log.d("DEBUG", "Added file to " + folderName + "/" + fileName);
        });
    }
}