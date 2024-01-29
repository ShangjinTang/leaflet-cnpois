package com.example.locationinterestdetection;

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

public class SqliteGeometryLoader {
    public static GeometryCollection loadPolygonsFromSqlite(Context context, String sqliteFileName, String tableName) {
        return loadGeometryCollectionFromSqlite(context, sqliteFileName, tableName, "polygon");
    }

    public static GeometryCollection loadCoordinatesFromSqlite(Context context, String sqliteFileName, String tableName) {
        return loadGeometryCollectionFromSqlite(context, sqliteFileName, tableName, "coordinate");
    }

    private static GeometryCollection loadGeometryCollectionFromSqlite(Context context, String sqliteFileName, String tableName, String columnName) {
        try {
            copyDatabaseFromAssets(context, sqliteFileName);
            SQLiteOpenHelper dbHelper = new AoiSQLiteOpenHelper(context, sqliteFileName);
            SqliteGeometryConverter SqliteGeometryConverter = new SqliteGeometryConverter(dbHelper);
            return SqliteGeometryConverter.parseGeometryCollectionFromDatabase(tableName, columnName);
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