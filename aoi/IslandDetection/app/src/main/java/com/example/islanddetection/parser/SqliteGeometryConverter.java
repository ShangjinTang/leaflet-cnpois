package com.example.islanddetection.parser;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;

import com.example.islanddetection.parser.GeojsonLocationParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;


public class SqliteGeometryConverter {
    private SQLiteOpenHelper dbHelper;

    public SqliteGeometryConverter(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public GeometryCollection parseGeometryCollectionFromDatabase(String tableName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(tableName, new String[]{"polygon"}, null, null, null, null, null);

            List<Geometry> geometries = new ArrayList<>();

            try {
                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex("polygon");
                    if (columnIndex != -1) {
                        String polygonString = cursor.getString(columnIndex);
                        JSONArray coordinatesArray = new JSONArray(polygonString);
                        GeometryFactory geometryFactory = new GeometryFactory();
                        Geometry geometry = GeojsonLocationParser.parsePolygon(coordinatesArray, geometryFactory);
                        geometries.add(geometry);
                    } else {
                        geometries.add(null);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Geometry[] geometryArray = geometries.toArray(new Geometry[0]);
            return new GeometryCollection(geometryArray, new GeometryFactory());
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

}