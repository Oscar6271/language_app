package com.example.ordapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Library {
    public static native void writeToFile(String fileName, String contentToWrite, boolean append);
    public static native String printFile(String fileName);
    public static native int readFile(String fileName, String language_to_write_in);
    public static native String pickWord();
    public static native String compare(String userInput);
    public static native int checkEmpty();

    public static final int GREEN = 3;
    public static final int YELLOW = 2;
    public static final int RED = 1;

    public static void createSummaryFile(File FilesDir, String folderName)
    {
        File summaryFile = new File(FilesDir, folderName + "/" + folderName + "_summary.txt");
        if(summaryFile.exists())
        {
            summaryFile.delete();
        }
        String summaryFileName = summaryFile.getAbsolutePath();
        String fileWOextension = summaryFileName.substring(0, summaryFileName.length() - 4);

        File folder = new File(FilesDir, folderName);
        File[] files = folder.listFiles();

        for(File file : files)
        {
            if(file.isFile() && !file.getName().equals("profileInstalled") && !file.getName().equals(fileWOextension))
            {
                // läs in varje fil med printFile till en String
                String filePath = new File(FilesDir, folderName + "/" + file.getName()).getAbsolutePath();
                String filePathWOextension = filePath.substring(0, filePath.length() - 4);
                writeToFile(fileWOextension, printFile(filePathWOextension), true);
            }
        }
    }

    public static int dpToPx(int dp, float density)
    {
        return (int) (dp * density);
    }

    public static void addView(Button button, float density, ConstraintLayout layout, int size)
    {
        button.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(size, density), dpToPx(70, density)
        );
        button.setLayoutParams(btnParams);
        layout.addView(button);
    }

    public static void addConstraintSet(Button button, int startMargin, ConstraintLayout layout, int buttonCount, float density)
    {
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(80 + buttonCount * 80, density);
        mainSet.connect(button.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(button.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin);
        mainSet.connect(button.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        mainSet.applyTo(layout);
    }

    public static void addView(EditText text, float density, ConstraintLayout layout)
    {
        text.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(150, density), dpToPx(70, density)
        );
        text.setLayoutParams(btnParams);
        layout.addView(text);
    }

    public static void addConstraintSet(EditText choose, int startMarging, ConstraintLayout layout, int buttonCount, float density)
    {
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(buttonCount * 140, density);
        mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMarging);
        mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        mainSet.applyTo(layout);
    }

    public static void importFile(DocumentFile file, File targetFolder, Context context)
    {
        if (file == null || !file.isFile()) return;

        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        File targetFile = new File(targetFolder, file.getName());

        try (
                InputStream in = context.getContentResolver().openInputStream(file.getUri());
                FileOutputStream out = new FileOutputStream(targetFile)
        ) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void DeleteFile(File file)
    {
        // Kontrollera om filen finns och ta bort den
        if(file.exists()){
            boolean deleted = file.delete();
        }
    }

    public static Button addExtraButton(String buttonTitle, int startMargin, float density, ConstraintLayout layout, int buttonCount, Context context)
    {
        Button button = new Button(context);
        button.setText(buttonTitle);

        Library.addView(button, density, layout, 150);
        Library.addConstraintSet(button, startMargin, layout, buttonCount, density);

        return button;
    }

    public static Button createButton(SharedPreferences prefs, String prefKey, Context context, float density, ConstraintLayout layout, int size, int buttonCount, String buttonText)
    {
        Button button = new Button(context);
        addView(button, density, layout, size);
        addConstraintSet(button, 0, layout, buttonCount, density);
        button.setText(buttonText);
        button.setTextColor(Color.BLACK);

        readPref(prefs, prefKey, button);

        return button;
    }

    public static int evauluatePref(SharedPreferences prefs, String prefKey)
    {
        String color = prefs.getString(prefKey, "");

        if(color.equals("yellow"))
        {
            return YELLOW;
        }
        else if(color.equals("green"))
        {
            return GREEN;
        }
        else if(color.equals("red"))
        {
            return RED;
        }

        return 0;
    }

    public static void setNextColor(int currentValue, int maxValue, SharedPreferences nextPref, String nextPrefKey)
    {
        double ratio = (double) currentValue / maxValue;

        String color = "";
        if(ratio >= 0.5 && ratio < 1)
        {
            color = "yellow";
        }
        else if(ratio == 1)
        {
            color = "green";
        }
        else if(ratio < 0.5 && ratio >= 0.0)
        {
            color = "red";
        }

        nextPref.edit().putString(nextPrefKey, color).apply();
    }

    public static void setColor(SharedPreferences prefs, String prefsKey, String color)
    {
        prefs.edit().putString(prefsKey, color).apply();
    }

    public static void readPref(SharedPreferences prefs, String prefKey, Button button)
    {
        String color = prefs.getString(prefKey, "");

        if(color.equals("yellow"))
        {
            button.setBackgroundTintList(
                    ColorStateList.valueOf(Color.YELLOW));
        }
        else if(color.equals("green"))
        {
            button.setBackgroundTintList(
                    ColorStateList.valueOf(Color.GREEN));
        }
        else if(color.equals("red"))
        {
            button.setBackgroundTintList(
                    ColorStateList.valueOf(Color.RED));
        }
    }

    public static String getColor(SharedPreferences prefs, String prefKey)
    {
        return prefs.getString(prefKey, "");
    }

    public static void gotoNextColor(SharedPreferences prefs, String prefKey)
    {
        // om knappen inte har en färg, default till röd och sätt den sen till gul
        String color = prefs.getString(prefKey, "red");

        if(color.equals("green"))
        {
            return;
        }

        if(color.equals("yellow"))
        {
            setColor(prefs, prefKey, "green");
        }
        else if(color.equals("red"))
        {
            setColor(prefs, prefKey, "yellow");
        }
    }

    public static void setPracticeColor(int totalCorrect, int totalWords, SharedPreferences prefs, String prefKey)
    {
        double ratio = (double) totalCorrect / totalWords;
        if(ratio == 1)
        {
            Library.gotoNextColor(prefs, prefKey);
        }
        else if(ratio > 0.5 && ratio < 1 && Library.getColor(prefs, prefKey).equals("green"))
        {
            Library.setColor(prefs, prefKey, "yellow");
        }
        else if(ratio <= 0.5)
        {
            Library.setColor(prefs, prefKey, "red");
        }
    }
}
