package com.example.ordapp;


import android.content.Context;
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

    public static void addView(Button choose, float density, ConstraintLayout layout, int size)
    {
        choose.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(size, density), dpToPx(70, density)
        );
        choose.setLayoutParams(btnParams);
        layout.addView(choose);
    }

    public static void addConstraintSet(Button choose, int startMargin, ConstraintLayout layout, int buttonCount, float density)
    {
        ConstraintSet mainSet = new ConstraintSet();
        mainSet.clone(layout);

        int topMargin = dpToPx(80 + buttonCount * 80, density);
        mainSet.connect(choose.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin);
        mainSet.connect(choose.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin);
        mainSet.connect(choose.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        mainSet.applyTo(layout);
    }

    public static void addView(EditText choose, float density, ConstraintLayout layout)
    {
        choose.setId(View.generateViewId());
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                dpToPx(150, density), dpToPx(70, density)
        );
        choose.setLayoutParams(btnParams);
        layout.addView(choose);
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
        Button choose = new Button(context);
        choose.setText(buttonTitle);

        Library.addView(choose, density, layout, 150);
        Library.addConstraintSet(choose, startMargin, layout, buttonCount, density);

        return choose;
    }
}
