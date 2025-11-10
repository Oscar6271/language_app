package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.ordapp.databinding.ActivityChooseFolderBinding;

import java.io.File;

public class ChooseFolder extends AppCompatActivity {

    int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    int buttonCount = 0;
    ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChooseFolderBinding binding = ActivityChooseFolderBinding.inflate(getLayoutInflater());
        layout = findViewById(R.id.main);

        File[] files = getFilesDir().listFiles();

        for(File file : files) {
            if(file.isDirectory()) {
                Button choose = new Button(this);
                choose.setText(file.getName());

                choose.setId(View.generateViewId());
                ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                        dpToPx(150), dpToPx(70)
                );
                choose.setLayoutParams(btnParams);
                layout.addView(choose);

                ConstraintSet mainSet = new ConstraintSet();
                mainSet.clone(layout);

                int topMargin = dpToPx(buttonCount * 150);
                mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
                mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                mainSet.connect(choose.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

                mainSet.applyTo(layout);
                buttonCount++;

                choose.setOnClickListener(view -> {
                    Intent intent = new Intent(ChooseFolder.this, SelectFile.class);
                    intent.putExtra("FOLDER_NAME", file.getName());
                    startActivity(intent);
                });
            }
        }
    }
}