package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ordapp.databinding.ActivitySimpleInputBinding;
import com.google.android.material.textfield.TextInputEditText;

public class SimpleInput extends AppCompatActivity {
    static {
        System.loadLibrary("ordapp"); // namnet på din .so-fil (utan 'lib' och '.so')
    }
    private ActivitySimpleInputBinding binding;
    private TextView errormessage;

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
        fileNameInput.setHorizontallyScrolling(true);
        fileNameInput.setMovementMethod(new ScrollingMovementMethod());

        TextInputEditText contentInput = findViewById(R.id.SimpleInputText);
        errormessage = findViewById(R.id.errorMessageText);

        fileNameInput.setText(fileName);
        contentInput.setText(content);

        binding.createSimpleFileButton.setOnClickListener(v -> {
            fileName = binding.fileName.getEditText().getText().toString();

            if(!fileName.isEmpty()) {
                errormessage.setText("");
                writeToFile(getFilesDir().getAbsolutePath() + "/" + fileName, binding.simpleInput.getEditText().getText().toString(), append);
                Log.d("WRITE", "written: " + getFilesDir().getAbsolutePath() + "/" + fileName);
                finish();
            } else {
                errormessage.setText("Input a filename to save wordset");
            }
        });
    }
}