package com.example.islanddetection;

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
    private String geoJsonFileName = "example_island_polygons.geojson";
    private String geoSqliteDbFileName = "example_island_polygons.sqlite";
    private String geoSqliteDbTableName = "geodata_aois";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeometryCollection geometryCollectionFromGeojson = GeoJsonUtils.loadGeometryCollectionFromGeoJson(context, geoJsonFileName);
                GeometryCollection geometryCollectionFromSqlite = GeoSqliteDbUtils.loadGeometryCollectionFromSqlite(context, geoSqliteDbFileName, geoSqliteDbTableName);

                EditText editLatitudeText = findViewById(R.id.editLatitude);
                EditText editLongitudeText = findViewById(R.id.editLongitude);
                double latitude = Double.parseDouble(editLatitudeText.getText().toString());
                double longitude = Double.parseDouble(editLongitudeText.getText().toString());
                Log.v(TAG, "latitude: " + latitude);
                Log.v(TAG, "longitude: " + longitude);
                Coordinate coordinate = new Coordinate(longitude, latitude);
                TextView resultText = findViewById(R.id.resultTextView);

                if (geometryCollectionFromGeojson != null) {
                    Log.d(TAG, "geometryCollectionFromGeojson: " + geometryCollectionFromGeojson.toString());
                    boolean isInsideAnyGeojsonPolygon = MapUtils.isInsideAnyPolygon(coordinate, geometryCollectionFromGeojson);
                    Log.v(TAG, "isInsideAnyPolygon: " + isInsideAnyGeojsonPolygon);
                }

                if (geometryCollectionFromSqlite != null) {
                    Log.d(TAG, "geometryCollectionFromSqlite: " + geometryCollectionFromSqlite.toString());
                    boolean isInsideAnySqlitePolygon = MapUtils.isInsideAnyPolygon(coordinate, geometryCollectionFromSqlite);
                    Log.v(TAG, "isInsideAnyPolygon: " + isInsideAnySqlitePolygon);
                }

//                if (isInsideAnyPolygon) {
//                    resultText.setText("Is Island Area: true");
//                } else {
//                    resultText.setText("Is Island Area: false");
//                }

            }
        });

    }
}