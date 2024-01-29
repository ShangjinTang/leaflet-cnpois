package com.example.islanddetection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;

import org.json.JSONArray;
import org.json.JSONException;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;


public class GeometryParser {
    private SQLiteOpenHelper dbHelper;

    public GeometryParser(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public GeometryCollection parseGeometryCollectionFromDatabase(String table_name) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(table_name, new String[]{"polygon"}, null, null, null, null, null);

            List<Geometry> geometries = new ArrayList<>();

            try {
                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex("polygon");
                    if (columnIndex != -1) {
                        String polygonString = cursor.getString(columnIndex);
                        JSONArray coordinatesArray = new JSONArray(polygonString);
                        GeometryFactory geometryFactory = new GeometryFactory();
                        Geometry geometry = parsePolygon(coordinatesArray, geometryFactory);
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

    private static Polygon parsePolygon(JSONArray coordinatesArray, GeometryFactory geometryFactory) throws JSONException {
        List<Coordinate> coordinateList = new ArrayList<>();

        for (int i = 0; i < coordinatesArray.length(); i++) {
            JSONArray coordinate = coordinatesArray.getJSONArray(i);
            double x = coordinate.getDouble(0);
            double y = coordinate.getDouble(1);
            coordinateList.add(new Coordinate(x, y));
        }

        Coordinate[] coordinates = coordinateList.toArray(new Coordinate[0]);

        LinearRing linearRing = geometryFactory.createLinearRing(coordinates);
        return new Polygon(linearRing, null, geometryFactory);
    }

    private static List<Coordinate> parseCoordinates(JSONArray coordinateArray) throws JSONException {
        List<Coordinate> coordinateList = new ArrayList<>();

        for (int i = 0; i < coordinateArray.length(); i++) {
            JSONArray coordinate = coordinateArray.getJSONArray(i);
            double x = coordinate.getDouble(0);
            double y = coordinate.getDouble(1);
            Coordinate c = new Coordinate(x, y);
            coordinateList.add(c);
        }

        return coordinateList;
    }
}