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

import com.example.locationinterestdetection.parser.GeojsonGeometryLoader;
import com.example.locationinterestdetection.parser.SqliteGeometryLoader;

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

                GeometryCollection aoiGeometryCollectionFromGeojson = GeojsonGeometryLoader.loadGeometryCollectionFromGeoJson(context, geoJsonFileName);
                if (aoiGeometryCollectionFromGeojson != null) {
                    Log.d(TAG, "geometryCollectionFromGeojson: " + aoiGeometryCollectionFromGeojson.toString());
                    boolean isInsideAnyGeojsonPolygon = MapUtils.isInsideAnyPolygon(coordinate, aoiGeometryCollectionFromGeojson);
                    Log.v(TAG, "isInsideAnyPolygon: " + isInsideAnyGeojsonPolygon);
                }

                GeometryCollection aoiGeoCollectionFromSqlite = SqliteGeometryLoader.loadGeometryCollectionFromSqlite(context, geoSqliteDbFileName, geoSqliteDbTableName);
                if (aoiGeoCollectionFromSqlite != null) {
                    Log.d(TAG, "geometryCollectionFromSqlite: " + aoiGeoCollectionFromSqlite.toString());
                    boolean isInsideAnySqlitePolygon = MapUtils.isInsideAnyPolygon(coordinate, aoiGeoCollectionFromSqlite);
                    Log.v(TAG, "isInsideAnyPolygon: " + isInsideAnySqlitePolygon);
                }

                // For POI
                GeometryCollection poiGeoCollections = GeojsonGeometryLoader.loadGeometryCollectionFromGeoJson(context, "example_pois.geojson");
                if (poiGeoCollections != null) {
                    Log.d(TAG, "poiGeoCollections: " + poiGeoCollections.toString());
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