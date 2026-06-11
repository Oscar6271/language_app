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
    private TextView errormessage, folderText;
    private TextInputEditText fileEdit, contentEdit;

    public native void writeToFile(String fileName, String contentToWrite, boolean append);
    public native String printFile(String fileName);
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

    public void createSummaryFile()
    {
        File folder = new File(getFilesDir(), folderName);
        File[] files = folder.listFiles();

        String summaryFile = new File(getFilesDir(), folderName + "/" + folderName + "_summary.txt").getAbsolutePath();
        String fileWOextension = summaryFile.substring(0, summaryFile.length() - 4);

        for(File file : files)
        {
            if(file.isFile() && !file.getName().equals("profileInstalled"))
            {
                // läs in varje fil med printFile till en String
                String filePath = new File(getFilesDir(), folderName + "/" + file.getName()).getAbsolutePath();
                String filePathWOextension = filePath.substring(0, filePath.length() - 4);
                writeToFile(fileWOextension, printFile(filePathWOextension), true);
            }
        }
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
                writeToFile(file.getAbsolutePath(), contentText, append);
                createSummaryFile();
                // Log.d("WRITE", "written: " + getFilesDir().getAbsolutePath() + "/" + folderName + "/" + fileName);
                finish();

            } else {
                errormessage.setText("Input a filename to save wordset");
            }

            Log.d("DEBUG", "Added file to " + folderName + "/" + fileName);
        });
    }
}