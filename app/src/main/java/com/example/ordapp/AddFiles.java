package com.example.ordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ordapp.databinding.AddFilesBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class AddFiles extends AppCompatActivity {
    private AddFilesBinding binding;
    private ConstraintLayout layout;
    private int currentIndex = 5, wordViewId;
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private void addWordInput()
    {
        TextInputLayout inputOrdLayout = new TextInputLayout(new ContextThemeWrapper(this, com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox));
        wordViewId = View.generateViewId();
        inputOrdLayout.setId(wordViewId);

        ConstraintLayout.LayoutParams params =
                new ConstraintLayout.LayoutParams(dpToPx(150), ViewGroup.LayoutParams.WRAP_CONTENT);

        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(dpToPx(28), dpToPx(456 + (currentIndex - 4) * 62), 0, 0);

        currentIndex++;
        inputOrdLayout.setLayoutParams(params);

        TextInputEditText inputOrd = new TextInputEditText(inputOrdLayout.getContext());
        inputOrd.setHint("ord");
        inputOrdLayout.addView(inputOrd);

        layout.addView(inputOrdLayout);

        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        set.connect(inputOrdLayout.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dpToPx(28));
        set.connect(binding.addWordsButton.getId(), ConstraintSet.TOP, inputOrdLayout.getId(), ConstraintSet.BOTTOM, dpToPx(32));
        set.applyTo(layout);
    }

    private void addTranslationInput()
    {
        TextInputLayout translationLayout = new TextInputLayout(this, null,
                com.google.android.material.R.attr.textInputStyle);
        translationLayout.setId(View.generateViewId());

        ConstraintLayout.LayoutParams params =
                new ConstraintLayout.LayoutParams(dpToPx(150), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPx(100), 0, 0, 0);

        translationLayout.setLayoutParams(params);

        TextInputEditText inputOrd = new TextInputEditText(translationLayout.getContext());
        inputOrd.setHint("översättning");
        translationLayout.addView(inputOrd);

        layout.addView(translationLayout);

        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        set.connect(translationLayout.getId(), ConstraintSet.START, wordViewId, ConstraintSet.END, dpToPx(32)); // 16dp margin
        set.connect(translationLayout.getId(), ConstraintSet.TOP, wordViewId, ConstraintSet.TOP);

        set.applyTo(layout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AddFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        layout = findViewById(R.id.main);

        binding.addWordsButton.setOnClickListener(v -> {
            addWordInput();
            addTranslationInput();
        });

        binding.GoToSimpleInput.setOnClickListener(v -> {
            Intent intent = new Intent(AddFiles.this, SimpleInput.class);
            startActivity(intent);
        });
    }
}
