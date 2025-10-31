package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordapp.databinding.ActivitySimpleInputBinding;
import com.google.android.material.textfield.TextInputEditText;

public class SimpleInput extends AppCompatActivity {
    private ActivitySimpleInputBinding binding;

    public native void writeToFile(String fileName, String contentToWrite, boolean append);
    private String fileName;
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

        TextInputEditText fileNameInput = findViewById(R.id.fileNameInput);
        TextInputEditText contentInput = findViewById(R.id.SimpleInputText);

        fileNameInput.setText(fileName);
        contentInput.setText(content);

        binding.createSimpleFileButton.setOnClickListener(v -> {
            fileName = binding.fileName.getEditText().getText().toString();

            writeToFile(getFilesDir().getAbsolutePath() + "/" + fileName, binding.simpleInput.getEditText().getText().toString(), append);
            finish();
        });
    }
}