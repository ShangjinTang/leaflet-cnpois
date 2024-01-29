package com.example.islanddetection;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.locationtech.jts.geom.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class GeoSqliteDbUtils {

    public static GeometryCollection loadGeometryCollectionFromSqlite(Context context, String sqliteFileName, String tableName) {
        try {
            copyDatabaseFromAssets(context, sqliteFileName);
            SQLiteOpenHelper dbHelper = new AoiSQLiteOpenHelper(context, sqliteFileName);
            SQLiteGeometryParser SQLiteGeometryParser = new SQLiteGeometryParser(dbHelper);
            return SQLiteGeometryParser.parseGeometryCollectionFromDatabase(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void copyDatabaseFromAssets(Context context, String sqliteFileName) throws IOException {
        AssetManager assetManager = context.getAssets();
        String[] assetFiles = assetManager.list("");

        if (!Arrays.asList(assetFiles).contains(sqliteFileName)) {
            throw new IOException("Database file not found in assets: " + sqliteFileName);
        }

        File sqliteFile = context.getDatabasePath(sqliteFileName);

        if (sqliteFile.exists()) {
            return;
        }

        InputStream inputStream = assetManager.open(sqliteFileName);
        OutputStream outputStream = new FileOutputStream(sqliteFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    private static class AoiSQLiteOpenHelper extends SQLiteOpenHelper {
        AoiSQLiteOpenHelper(Context context, String sqliteDbName) {
            super(context, sqliteDbName, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}