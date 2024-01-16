package com.example.islanddetection;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GeoJsonUtils {
    public static GeometryCollection loadGeometryCollectionFromGeoJson(Context context, String geoJsonFileName) throws IOException, JSONException {
        String geoJson = readGeoJsonFromAssets(context, geoJsonFileName);

        JSONObject geoJsonObject = new JSONObject(geoJson);
        JSONArray featuresArray = geoJsonObject.getJSONArray("features");

        GeometryFactory geometryFactory = new GeometryFactory();

        List<Geometry> geometryList = new ArrayList<>();
        for (int i = 0; i < featuresArray.length(); i++) {
            JSONObject featureObject = featuresArray.getJSONObject(i);
            JSONObject geometryObject = featureObject.getJSONObject("geometry");
            Geometry geometry = parseGeometry(geometryObject, geometryFactory);
            geometryList.add(geometry);
        }

        GeometryCollection geometryCollection = new GeometryCollection(geometryList.toArray(new Geometry[0]), geometryFactory);

        return geometryCollection;
    }

    private static String readGeoJsonFromAssets(Context context, String fileName) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(fileName);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }

    private static Geometry parseGeometry(JSONObject geometryObject, GeometryFactory geometryFactory) throws JSONException {
        String type = geometryObject.getString("type");
        JSONArray coordinatesArray = geometryObject.getJSONArray("coordinates");

        if (type.equalsIgnoreCase("Polygon")) {
            return parsePolygon(coordinatesArray, geometryFactory);
        } else if (type.equalsIgnoreCase("MultiPolygon")) {
            return parseMultiPolygon(coordinatesArray, geometryFactory);
        }

        throw new JSONException("Unsupported geometry type: " + type);
    }

    private static Polygon parsePolygon(JSONArray coordinatesArray, GeometryFactory geometryFactory) throws JSONException {
        List<Coordinate> coordinateList = new ArrayList<>();

        for (int i = 0; i < coordinatesArray.length(); i++) {
            JSONArray polygonCoordinates = coordinatesArray.getJSONArray(i);
            List<Coordinate> subCoordinateList = parseCoordinates(polygonCoordinates);
            coordinateList.addAll(subCoordinateList);
        }

        Coordinate[] coordinates = coordinateList.toArray(new Coordinate[0]);

        LinearRing linearRing = geometryFactory.createLinearRing(coordinates);
        return new Polygon(linearRing, null, geometryFactory);
    }

    private static GeometryCollection parseMultiPolygon(JSONArray coordinatesArray, GeometryFactory geometryFactory) throws JSONException {
        List<Polygon> polygonList = new ArrayList<>();

        for (int i = 0; i < coordinatesArray.length(); i++) {
            JSONArray polygonCoordinates = coordinatesArray.getJSONArray(i);
            List<Coordinate> coordinateList = parseCoordinates(polygonCoordinates);
            Coordinate[] coordinates = coordinateList.toArray(new Coordinate[0]);

            LinearRing linearRing = geometryFactory.createLinearRing(coordinates);
            Polygon polygon = new Polygon(linearRing, null, geometryFactory);
            polygonList.add(polygon);
        }

        return new GeometryCollection(polygonList.toArray(new Polygon[0]), geometryFactory);
    }

    private static List<Coordinate> parseCoordinates(JSONArray coordinatesArray) throws JSONException {
        List<Coordinate> coordinateList = new ArrayList<>();

        for (int i = 0; i < coordinatesArray.length(); i++) {
            JSONArray coordinateArrayI = coordinatesArray.getJSONArray(i);
            double x = coordinateArrayI.getDouble(0);
            double y = coordinateArrayI.getDouble(1);
            Coordinate coordinate = new Coordinate(x, y);
            coordinateList.add(coordinate);
        }

        return coordinateList;
    }
}