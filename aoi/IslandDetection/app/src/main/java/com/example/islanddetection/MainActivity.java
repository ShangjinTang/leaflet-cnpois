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
    private String geoJsonFileName = "example_aois.geojson";
    private String geoSqliteDbFileName = "example.sqlite";
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

                EditText editLatitudeText = findViewById(R.id.editLatitude);
                EditText editLongitudeText = findViewById(R.id.editLongitude);
                double latitude = Double.parseDouble(editLatitudeText.getText().toString());
                double longitude = Double.parseDouble(editLongitudeText.getText().toString());
                Coordinate coordinate = new Coordinate(longitude, latitude);
                TextView resultText = findViewById(R.id.resultTextView);

                GeometryCollection geometryCollectionFromGeojson = GeoJsonUtils.loadGeometryCollectionFromGeoJson(context, geoJsonFileName);
                if (geometryCollectionFromGeojson != null) {
                    Log.d(TAG, "geometryCollectionFromGeojson: " + geometryCollectionFromGeojson.toString());
                    boolean isInsideAnyGeojsonPolygon = MapUtils.isInsideAnyPolygon(coordinate, geometryCollectionFromGeojson);
                    Log.v(TAG, "isInsideAnyPolygon: " + isInsideAnyGeojsonPolygon);
                }

                GeometryCollection geometryCollectionFromSqlite = GeoSqliteDbUtils.loadGeometryCollectionFromSqlite(context, geoSqliteDbFileName, geoSqliteDbTableName);
                if (geometryCollectionFromSqlite != null) {
                    Log.d(TAG, "geometryCollectionFromSqlite: " + geometryCollectionFromSqlite.toString());
                    boolean isInsideAnySqlitePolygon = MapUtils.isInsideAnyPolygon(coordinate, geometryCollectionFromSqlite);
                    Log.v(TAG, "isInsideAnyPolygon: " + isInsideAnySqlitePolygon);
                }

//                if (isInsideAnyPolygon) {
//                    resultText.setText("Inside any polygons: true");
//                } else {
//                    resultText.setText("Inside any polygons: false");
//                }
            }
        });

    }
}