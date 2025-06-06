package com.github.damianjester.nclient.legacy.async.database.export;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.github.damianjester.nclient.legacy.SettingsActivity;
import com.github.damianjester.nclient.legacy.utility.LogUtility;

import java.io.IOException;

public class Manager extends Thread {
    @NonNull
    private final Uri file;
    @NonNull
    private final SettingsActivity context;
    private final boolean export;
    private final Runnable end;

    public Manager(@NonNull Uri file, @NonNull SettingsActivity context, boolean export, Runnable end) {
        this.file = file;
        this.context = context;
        this.export = export;
        this.end = end;
    }


    @Override
    public void run() {
        try {
            if (export) Exporter.exportData(context, file);
            else Importer.importData(context, file);
            context.runOnUiThread(end);
        } catch (IOException e) {
            LogUtility.e(e, e);
        }
    }
}
