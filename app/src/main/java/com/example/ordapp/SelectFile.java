package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.ordapp.databinding.ActivitySelectFileBinding;

import java.io.File;

public class SelectFile extends AppCompatActivity {
    private ConstraintLayout layout;  // Huvud-ConstraintLayout inuti ScrollView
    private int buttonCount = 0;     // För att positionera knappar vertikalt

    int dpToPx(int dp)
    {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void DeleteFile(String folder, String fileName)
    {
        // Hämta filen
        File file = new File(getFilesDir() + "/" + folder, fileName);
        // Log.d("DEBUG", "tar bort " + fileName);
        // Log.d("DEBUG", file.exists() ? "finns" : "finns inte");

        // Kontrollera om filen finns och ta bort den
        if(file.exists()){
            boolean deleted = file.delete();
            /*if(deleted){
                Log.d("FILE_DELETE", "Filen togs bort!");
            } else {
                Log.d("FILE_DELETE", "Kunde inte ta bort filen.");
            }*/
        }
    }

    private void addView(Button choose)
    {
        choose.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(150), dpToPx(70)
        );
        choose.setLayoutParams(btnParams);
        layout.addView(choose);
    }

    public void addConstraintSet(Button choose)
    {
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(100 + buttonCount * 150);
        mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        mainSet.connect(choose.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

        mainSet.applyTo(layout);
    }

    private void addExtraButton(String buttonTitle)
    {
        Button choose = new Button(this);
        choose.setText(buttonTitle);

        addView(choose);
        addConstraintSet(choose);

        buttonCount++;
        Intent intent = getIntent();
        choose.setOnClickListener(view -> {
            // gå till simple_input
            Intent simple_input_intent = new Intent(SelectFile.this, SimpleInput.class);
            simple_input_intent.putExtra("FOLDER_NAME", intent.getStringExtra("FOLDER_NAME"));
            startActivity(simple_input_intent);
        });
    }
    private void createButtons(File file) {

        // 1. Skapa huvudknapp
        Button choose = new Button(this);
        String fileName = file.getName();
        choose.setText(fileName.substring(0, fileName.length() - 4));

        addView(choose);
        addConstraintSet(choose);

        buttonCount++;

        choose.setOnClickListener(view -> {
            Intent ChooseFileModeIntent = new Intent(SelectFile.this, ChooseFileMode.class);
            // Log.d("DEBUG", folder + "/" + file.getName());
            ChooseFileModeIntent.putExtra("FILE_NAME", fileName);
            ChooseFileModeIntent.putExtra("FOLDER_NAME", getIntent().getStringExtra("FOLDER_NAME"));
            startActivity(ChooseFileModeIntent);
        });
    }

    private void createDropDowns() {
        Log.d("FILES", "finding files");
        Intent intent = getIntent();
        String folderName = intent.getStringExtra("FOLDER_NAME");
        // Log.d("FOLDER", folderName);

        File folder = new File(getFilesDir(), folderName);
        File[] files = folder.listFiles();

        for (File file : files) {
            if (file.isFile() && !file.getName().equals("profileInstalled")) {
                createButtons(file);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectFileBinding binding = ActivitySelectFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ScrollView finns i XML, ConstraintLayout som child
        layout = findViewById(R.id.main);

        addExtraButton("Add file");

        createDropDowns();
    }
}
