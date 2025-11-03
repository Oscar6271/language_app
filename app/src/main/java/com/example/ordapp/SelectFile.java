package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.ordapp.databinding.ActivitySelectFileBinding;

import java.io.File;

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
    public native String printFile(String fileName);

    private ConstraintLayout layout;  // Huvud-ConstraintLayout inuti ScrollView
    private int buttonCount = 0;     // För att positionera knappar vertikalt

    int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void DeleteFile(String fileName)
    {
        // Hämta filen
        File file = new File(getFilesDir(), fileName);

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

    private void buttonSetup(DropDownMenu dropDown) {
        dropDown.expandButton.setOnClickListener(view -> {
            if (dropDown.layout.getVisibility() == View.GONE) {
                dropDown.layout.setVisibility(View.VISIBLE);
            } else {
                dropDown.layout.setVisibility(View.GONE);
            }
        });

        dropDown.PracticeTranslation.setOnClickListener(view -> {
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
            intent.putExtra("CONTENT", printFile(getFilesDir().getAbsolutePath() + "/" + dropDown.fileName));
            intent.putExtra("APPEND", false);
            startActivity(intent);
        });

        dropDown.DeleteWordSet.setOnClickListener(view -> {
            DeleteFile(dropDown.fileName);
            finish();
            startActivity(getIntent());
        });
    }

    private DropDownMenu createButtons(File file) {
        DropDownMenu temp = new DropDownMenu();

        // 1. Skapa huvudknapp
        temp.expandButton = new Button(this);
        temp.expandButton.setText(file.getName());
        temp.expandButton.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(150), dpToPx(70)
        );
        temp.expandButton.setLayoutParams(btnParams);
        layout.addView(temp.expandButton);

        // 2. Skapa dropdown-layout
        temp.layout = new ConstraintLayout(this);
        temp.layout.setId(View.generateViewId());
        temp.layout.setVisibility(View.GONE);
        int padding = dpToPx(8);
        temp.layout.setPadding(padding, padding, padding, padding);
        layout.addView(temp.layout);

        // 3. Skapa knappar i dropdown
        temp.PracticeTranslation = new Button(this);
        temp.PracticeTranslation.setId(View.generateViewId());
        temp.PracticeTranslation.setText("Write translation");
        temp.layout.addView(temp.PracticeTranslation);

        temp.PracticeOriginal = new Button(this);
        temp.PracticeOriginal.setId(View.generateViewId());
        temp.PracticeOriginal.setText("Write original");
        temp.layout.addView(temp.PracticeOriginal);

        temp.EditWordSet = new Button(this);
        temp.EditWordSet.setId(View.generateViewId());
        temp.EditWordSet.setText("Edit wordset");
        temp.layout.addView(temp.EditWordSet);

        temp.DeleteWordSet = new Button(this);
        temp.DeleteWordSet.setId(View.generateViewId());
        temp.DeleteWordSet.setText("Delete wordset");
        temp.layout.addView(temp.DeleteWordSet);

        // 4. Constraints för dropdown-knappar
        ConstraintSet dropdownSet = new ConstraintSet();
        dropdownSet.clone(temp.layout);

        dropdownSet.connect(temp.PracticeTranslation.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        dropdownSet.connect(temp.PracticeTranslation.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);

        dropdownSet.connect(temp.PracticeOriginal.getId(), ConstraintSet.TOP, temp.PracticeTranslation.getId(), ConstraintSet.BOTTOM, dpToPx(8));
        dropdownSet.connect(temp.PracticeOriginal.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);

        dropdownSet.connect(temp.EditWordSet.getId(), ConstraintSet.TOP, temp.PracticeOriginal.getId(), ConstraintSet.BOTTOM, dpToPx(8));
        dropdownSet.connect(temp.EditWordSet.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);

        dropdownSet.connect(temp.DeleteWordSet.getId(), ConstraintSet.TOP, temp.EditWordSet.getId(), ConstraintSet.BOTTOM, dpToPx(8));
        dropdownSet.connect(temp.DeleteWordSet.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);

        dropdownSet.applyTo(temp.layout);

        // 5. Constraints för huvudknapp och dropdown i huvudlayout
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(100 + buttonCount * 120);
        mainSet.connect(temp.expandButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(temp.expandButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        mainSet.connect(temp.expandButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        mainSet.connect(temp.layout.getId(), ConstraintSet.TOP, temp.expandButton.getId(), ConstraintSet.BOTTOM, 0);
        mainSet.connect(temp.layout.getId(), ConstraintSet.START, temp.expandButton.getId(), ConstraintSet.START, 0);
        mainSet.connect(temp.layout.getId(), ConstraintSet.END, temp.expandButton.getId(), ConstraintSet.END, 0);

        mainSet.applyTo(layout);
        buttonCount++;

        temp.fileName = file.getName();
        return temp;
    }

    private void dropDownSetup(File file) {
        DropDownMenu dropDown = createButtons(file);
        buttonSetup(dropDown);
    }

    private void createDropDowns() {
        File[] files = getFilesDir().listFiles();
        for (File file : files) {
            if (file.isFile() && !file.getName().equals("profileInstalled")) {
                Log.d("FILES", file.getName());
                dropDownSetup(file);
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
