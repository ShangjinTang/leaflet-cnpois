package com.example.locationinterestdetection;

import android.util.Log;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;

public class MapUtils {
    public static boolean isInsideAnyPolygon(Coordinate coordinate, GeometryCollection geometryCollection) {
        Point point = new GeometryFactory().createPoint(coordinate);
        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            Geometry geometry = geometryCollection.getGeometryN(i);
            if (geometry.contains(point)) {
                return true;
            }
        }
        return false;
    }


    public static Coordinate getNearestCoordinate(Coordinate coordinate, GeometryCollection geometryCollection) {
        Coordinate nearestCoordinate = null;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            Point point = (Point) geometryCollection.getGeometryN(i);
            Coordinate coordinateToCalc = point.getCoordinate();
            double distance = coordinate.distance(coordinateToCalc);

            if (distance < minDistance) {
                minDistance = distance;
                nearestCoordinate = coordinateToCalc;
            }
        }

        return nearestCoordinate;
    }

    // Calculate distance of two geo points using Haversine Formula.
    // Meter level is enough for current problem, so we do not use double here.
    public static int calcHaversineDistanceInMeters(Coordinate coord1, Coordinate coord2) {
        final int EARTH_RADIUS = 6371000;
        double lat1 = Math.toRadians(coord1.getY());
        double lat2 = Math.toRadians(coord2.getY());
        double delta_lat = Math.toRadians(coord2.getY() - coord1.getY());
        double delta_lng = Math.toRadians(coord2.getX() - coord1.getX());
        double a = Math.sin(delta_lat / 2) * Math.sin(delta_lat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(delta_lng / 2) * Math.sin(delta_lng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        return (int) distance;
    }
}