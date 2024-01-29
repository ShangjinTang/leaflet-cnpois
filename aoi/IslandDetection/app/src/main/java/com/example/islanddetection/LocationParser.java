package com.example.islanddetection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

public class LocationParser {
    public static List<Coordinate> parseCoordinates(JSONArray coordinateArray) throws JSONException {
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
    public static Polygon parsePolygon(JSONArray coordinatesArray, GeometryFactory geometryFactory) throws JSONException {
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
    public static GeometryCollection parseMultiPolygon(JSONArray coordinatesArray, GeometryFactory geometryFactory) throws JSONException {
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

    public static Geometry parseGeometry(JSONObject geometryObject, GeometryFactory geometryFactory) throws JSONException {
        String type = geometryObject.getString("type");
        JSONArray coordinatesArray = geometryObject.getJSONArray("coordinates").getJSONArray(0);

        if (type.equalsIgnoreCase("Polygon")) {
            return LocationParser.parsePolygon(coordinatesArray, geometryFactory);
        } else if (type.equalsIgnoreCase("MultiPolygon")) {
            return LocationParser.parseMultiPolygon(coordinatesArray, geometryFactory);
        }

        throw new JSONException("Unsupported geometry type: " + type);
    }
}
