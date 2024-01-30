package com.example.locationinterestdetection;

import androidx.appcompat.app.AppCompatActivity;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public Context context;
    private static final String TAG = "MainActivity";
    private String aoisGeojson = "example_aois.geojson";
    private String poisGeojson = "example_pois.geojson";
    private String geoSqliteDbFileName = "example.sqlite";
    private String poisTable = "pois_example";
    private String aoisTable = "aois_example";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editLatitudeText = findViewById(R.id.editLatitude);
                EditText editLongitudeText = findViewById(R.id.editLongitude);
                double latitude = Double.parseDouble(editLatitudeText.getText().toString());
                double longitude = Double.parseDouble(editLongitudeText.getText().toString());
                Coordinate inputCoordinate = new Coordinate(longitude, latitude);
                TextView resultText = findViewById(R.id.resultTextView);

//                GeometryCollection aoiGeometryCollectionFromGeojson = GeojsonGeometryLoader.loadGeometryCollectionFromGeojson(context, aoisGeojson);
//                if (aoiGeometryCollectionFromGeojson != null) {
//                    Log.d(TAG, "aoiGeometryCollectionFromGeojson: " + aoiGeometryCollectionFromGeojson.toString());
//                    boolean isInsideAnyGeojsonPolygon = MapUtils.isInsideAnyPolygon(coordinate, aoiGeometryCollectionFromGeojson);
//                    Log.v(TAG, "isInsideAnyPolygon: " + isInsideAnyGeojsonPolygon);
//                }


//                GeometryCollection poiGeoCollectionsFromGeojson = GeojsonGeometryLoader.loadGeometryCollectionFromGeojson(context, poisGeojson);
//                if (poiGeoCollectionsFromGeojson != null) {
//                    Log.d(TAG, "poiGeoCollectionsFromGeojson: " + poiGeoCollectionsFromGeojson.toString());
//                }

                GeometryCollection aoiGeoCollectionFromSqlite = SqliteGeometryLoader.loadPolygonsFromSqlite(context, geoSqliteDbFileName, aoisTable);
                if (aoiGeoCollectionFromSqlite != null) {
                    Log.d(TAG, "aoiGeoCollectionFromSqlite: " + aoiGeoCollectionFromSqlite.toString());
                    boolean isInsideAnySqlitePolygon = MapUtils.isInsideAnyPolygon(inputCoordinate, aoiGeoCollectionFromSqlite);
                    Log.v(TAG, "isInsideAnyPolygon: " + isInsideAnySqlitePolygon);
                    if (isInsideAnySqlitePolygon) {
                        resultText.setText("Inside any polygons: TRUE");
                    } else {
                        resultText.setText("Inside any polygons: FALSE");
                    }
                }

                // POI search nearest coordinate
                GeometryCollection poiGeoCollectionFromSqlite = SqliteGeometryLoader.loadCoordinatesFromSqlite(context, geoSqliteDbFileName, poisTable);
                if (poiGeoCollectionFromSqlite != null) {
                    Log.d(TAG, "poiGeoCollectionFromSqlite: " + poiGeoCollectionFromSqlite.toString());
                    Coordinate nearestCoordinate = MapUtils.getNearestCoordinate(inputCoordinate, poiGeoCollectionFromSqlite);
                    Log.d(TAG, "nearestCoordinate: " + nearestCoordinate.toString());
                    int distanceInMeters = MapUtils.calcHaversineDistanceInMeters(inputCoordinate, nearestCoordinate);
                    Log.d(TAG, "distanceInKilometers to nearest poi: " + distanceInMeters + "m");
                }
            }
        });

    }
}