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
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            for (int j = 0; j < 10000; j++) {
                Geometry geometry = geometryCollection.getGeometryN(i);
                if (geometry.contains(point)) {
                    if (j == 9999) {
                        long endTime = System.currentTimeMillis();
                        Log.i("xxx", "time collapse： " + (endTime - startTime) + "ms");
                        return true;
                    }
                }
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
}