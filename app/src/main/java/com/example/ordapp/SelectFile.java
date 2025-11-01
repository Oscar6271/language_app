package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ordapp.databinding.ActivitySelectFileBinding;

import java.io.File;
import java.util.ArrayList;

class DropDownMenu {
    Button expandButton;
    String fileName;
    ConstraintLayout layout;
    Button PracticeTranslation;
    Button PracticeOriginal;
    Button EditWordSet;
    Button DeleteWordSet;
}

public class SelectFile extends AppCompatActivity {
    private ArrayList<DropDownMenu> dropDownMenus;
    public native String printFile(String fileName);
    public native void DeleteFile(String fileName);

    private void createDropDowns() {
        File[] files = getFilesDir().listFiles();

        if (files.length != 0) {
            for (File file : files) {
                if (file.isFile() && !file.getName().equals("profileInstalled")) {
                    Log.d("FILES", file.getName());
                    // skapa en DropDownMenu instans och lägg till i arrayList:en
                    DropDownMenu temp = new DropDownMenu();
                    // sätt värden för knappar och layout
                    temp.fileName = file.getName();
                    dropDownMenus.add(temp);
                }
            }
        }
    }

    private void dropDownInteraction(DropDownMenu dropDown)
    {
        dropDown.expandButton.setOnClickListener(view -> {
            if (dropDown.layout.getVisibility() == View.GONE) {
                dropDown.layout.setVisibility(View.VISIBLE);
            } else {
                dropDown.layout.setVisibility(View.GONE);
            }
        });

        dropDown.PracticeTranslation.setOnClickListener(view ->{
            Intent intent = new Intent(SelectFile.this, Practice.class);
            intent.putExtra("FILE_NAME", dropDown.fileName);
            intent.putExtra("LANGUAGE", "translation");
            startActivity(intent);
        });

        dropDown.PracticeOriginal.setOnClickListener(view -> {
            Intent intent = new Intent(SelectFile.this, Practice.class);
            intent.putExtra("FILE_NAME", dropDown.fileName);
            intent.putExtra("LANGUAGE", "original");
            startActivity(intent);
        });

        dropDown.EditWordSet.setOnClickListener(view -> {
            Intent intent = new Intent(SelectFile.this, SimpleInput.class);

            intent.putExtra("FILE_NAME", "words.txt");
            intent.putExtra("CONTENT", printFile(getFilesDir().getAbsolutePath() + "/" + dropDown.fileName);
            intent.putExtra("APPEND", false);

            startActivity(intent);
        });

        dropDown.DeleteWordSet.setOnClickListener(view -> {
            DeleteFile(getFilesDir().getAbsolutePath() + "/" + dropDown.fileName);
            finish();
            startActivity(getIntent());
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectFileBinding binding = ActivitySelectFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createDropDowns();

        ConstraintLayout dropdownMenu = (ConstraintLayout)findViewById(R.id.dropdownMenu);

        for(DropDownMenu dropDown : dropDownMenus) {
            dropDownInteraction(dropDown);
        }

        binding.WordsFileButton.setOnClickListener(view -> {
            if (dropdownMenu.getVisibility() == View.GONE) {
                dropdownMenu.setVisibility(View.VISIBLE);
            } else {
                dropdownMenu.setVisibility(View.GONE);
            }
        });

        binding.WriteTranslationButton.setOnClickListener(view ->{
            Intent intent = new Intent(SelectFile.this, Practice.class);
            intent.putExtra("FILE_NAME", "words.txt");
            intent.putExtra("LANGUAGE", "translation");
            startActivity(intent);
        });

        binding.WriteOriginalButton.setOnClickListener(view -> {
            Intent intent = new Intent(SelectFile.this, Practice.class);
            intent.putExtra("FILE_NAME", "words.txt");
            intent.putExtra("LANGUAGE", "original");
            startActivity(intent);
        });

        binding.EditSetButton.setOnClickListener(view -> {
            Intent intent = new Intent(SelectFile.this, SimpleInput.class);

            intent.putExtra("FILE_NAME", "words.txt");
            intent.putExtra("CONTENT", printFile(getFilesDir().getAbsolutePath() + "/words.txt"));
            intent.putExtra("APPEND", false);

            startActivity(intent);
        });

        binding.DeleteSetButton.setOnClickListener(view -> {
            DeleteFile(getFilesDir().getAbsolutePath() + "/words.txt");
            finish();
            startActivity(getIntent());
        });
    }
}