package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.ordapp.databinding.ActivityChooseFolderBinding;

import java.io.File;

public class ChooseFolder extends AppCompatActivity {

    int dpToPx(int dp)
    {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    int buttonCount = 0;
    ConstraintLayout layout;

    private void addView(Button choose)
    {
        choose.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(150), dpToPx(70)
        );
        choose.setLayoutParams(btnParams);
        layout.addView(choose);
    }

    private void addConstraintSet(Button choose)
    {
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(80 + buttonCount * 100);
        mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        mainSet.applyTo(layout);
        buttonCount++;

    }

    private void addView(EditText choose)
    {
        choose.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(150), dpToPx(70)
        );
        choose.setLayoutParams(btnParams);
        layout.addView(choose);
    }

    private void addConstraintSet(EditText choose)
    {
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(buttonCount * 150);
        mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        mainSet.applyTo(layout);
        buttonCount++;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChooseFolderBinding binding = ActivityChooseFolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        layout = findViewById(R.id.main);


        // skapa en knapp och ett textfält under knappen, knappen ska köra raderna ovanför
        Button addFolder = new Button(this);
        addFolder.setText("New folder");
        addView(addFolder);
        addConstraintSet(addFolder);

        EditText textField = new EditText(this);
        textField.setHint("Name of folder");
        addView(textField);
        addConstraintSet(textField);

        addFolder.setOnClickListener(view -> {
            String folderName = textField.getText().toString().trim();
            File folderFile = new File(getFilesDir(), folderName);

            // Skapar en folder
            if (!folderFile.exists())
            {
                folderFile.mkdirs();
                textField.setText("");
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            else
            {
                textField.setText("Folder already exists");
            }

        });

        File[] files = getFilesDir().listFiles();

        for(File file : files) {
            Log.d("DEBUG", "File: " + file.getName());
            if(file.isDirectory()) {
                Button choose = new Button(this);
                choose.setText(file.getName());

                addView(choose);
                addConstraintSet(choose);


                choose.setOnClickListener(view -> {
                    Intent intent = new Intent(ChooseFolder.this, SelectFile.class);
                    intent.putExtra("FOLDER_NAME", file.getName());
                    startActivity(intent);
                });
            }
        }
    }
}