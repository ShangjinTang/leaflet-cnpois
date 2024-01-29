package com.example.locationinterestdetection;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GeojsonGeometryLoader {
    public static GeometryCollection loadGeometryCollectionFromGeojson(Context context, String geoJsonFileName) {
        try {
            String geoJson = readGeoJsonFromAssets(context, geoJsonFileName);

            JSONObject geoJsonObject = new JSONObject(geoJson);
            JSONArray featuresArray = geoJsonObject.getJSONArray("features");

            GeometryFactory geometryFactory = new GeometryFactory();

            List<Geometry> geometryList = new ArrayList<>();
            for (int i = 0; i < featuresArray.length(); i++) {
                JSONObject featureObject = featuresArray.getJSONObject(i);
                JSONObject geometryObject = featureObject.getJSONObject("geometry");
                Geometry geometry = GeojsonLocationParser.parseGeometry(geometryObject, geometryFactory);
                geometryList.add(geometry);
            }
            GeometryCollection geometryCollection = new GeometryCollection(geometryList.toArray(new Geometry[0]), geometryFactory);
            return geometryCollection;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readGeoJsonFromAssets(Context context, String fileName) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(fileName);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }


}