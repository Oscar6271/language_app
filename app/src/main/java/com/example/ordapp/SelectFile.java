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

    int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void DeleteFile(String fileName)
    {
        // Hämta filen
        File file = new File(getFilesDir(), fileName + ".txt");

        // Kontrollera om filen finns och ta bort den
        if(file.exists()){
            boolean deleted = file.delete();
            if(deleted){
                Log.d("FILE_DELETE", "Filen togs bort!");
            } else {
                Log.d("FILE_DELETE", "Kunde inte ta bort filen.");
            }
        }
    }
    private void createButtons(File file) {

        // 1. Skapa huvudknapp
        Button choose = new Button(this);
        String fileName = file.getName();
        choose.setText(fileName.substring(0, fileName.length() - 4));

        choose.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(150), dpToPx(70)
        );
        choose.setLayoutParams(btnParams);
        layout.addView(choose);

        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(100 + buttonCount * 150);
        mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        mainSet.connect(choose.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

        mainSet.applyTo(layout);
        buttonCount++;

        choose.setOnClickListener(view -> {
            Intent intent = new Intent(SelectFile.this, ChooseFileMode.class);
            intent.putExtra("FILE_NAME", file.getName());
            startActivity(intent);
        });
    }

    private void createDropDowns() {
        File[] files = getFilesDir().listFiles();
        Log.d("FILES", "finding files");
        for (File file : files) {
            if (file.isFile() && !file.getName().equals("profileInstalled")) {
                Log.d("FILES", file.getName() + " Deleted");
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

        createDropDowns();
    }
}
