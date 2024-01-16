package com.example.islanddetection;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public Context context;
    private static final String TAG = "MainActivity";
    private String geoJsonFileName = "example_island_polygons.geojson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GeometryCollection geometryCollection = GeoJsonUtils.loadGeometryCollectionFromGeoJson(context, geoJsonFileName);

                    EditText editLatitudeText = findViewById(R.id.editLatitude);
                    EditText editLongitudeText = findViewById(R.id.editLongitude);
                    double latitude = Double.parseDouble(editLatitudeText.getText().toString());
                    double longitude = Double.parseDouble(editLongitudeText.getText().toString());
                    Log.v(TAG, "latitude: " + latitude );
                    Log.v(TAG, "longitude: " + longitude );
                    Coordinate coordinate = new Coordinate(longitude, latitude);
                    TextView resultText = findViewById(R.id.resultTextView);

                    boolean isInsideAnyPolygon = MapUtils.isInsideAnyPolygon(geometryCollection, coordinate);
                    Log.v(TAG, "isInsideAnyPolygon: " + isInsideAnyPolygon);

                    if (isInsideAnyPolygon) {
                        resultText.setText("Is Island Area: true");
                    } else {
                        resultText.setText("Is Island Area: false");
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}