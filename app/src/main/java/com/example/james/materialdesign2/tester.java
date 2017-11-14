package com.example.james.materialdesign2;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import org.w3c.dom.Text;

import dto.ShotResultsDTO;

public class tester extends AppCompatActivity implements OnMapReadyCallback
        {
    TextView t;
    GoogleMap mGoogleMap;
    MapView mapView;
    View view;
    ShotResultsDTO chosenSHot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         chosenSHot = (ShotResultsDTO) getIntent().getSerializableExtra("serialize_data");
              mapView = (MapView) findViewById(R.id.mmap);

       if (mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }





        //ShotResultsDTO chosenSHot = (ShotResultsDTO) getIntent().getSerializableExtra("serialize_data");

        //Toast.makeText(tester.this,shotID, Toast.LENGTH_LONG).show();
        t=(TextView) findViewById(R.id.object_tostring);
        t.setText(chosenSHot.toString());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.content_tester, container,false);
//        return view;
//    }

    //    @Override
//    public void onCreatedView(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        mapView = (MapView) findViewById(R.id.mmap);
//        if (mapView != null){
//            mapView.onCreate(null);
//            mapView.onResume();
//            mapView.getMapAsync(this);
//        }
//
//    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        //maps

        mGoogleMap = googleMap;
        MapsInitializer.initialize(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//        googleMap.addMarker(new MarkerOptions().position(new LatLng(chosenSHot.getStartLatitude(),chosenSHot.getStartLatitude())));
//        CameraPosition loc = CameraPosition.builder().target(new LatLng(chosenSHot.getStartLatitude(),chosenSHot.getStartLatitude())).zoom(16).bearing(0).tilt(45).build();
        LatLng start = new LatLng(chosenSHot.getStartLatitude(), chosenSHot.getStartLongitude());
        LatLng end = new LatLng(chosenSHot.getEndLatitude(), chosenSHot.getEndLongitude());
        googleMap.addMarker(new MarkerOptions().position(start)).setTitle("Start");
        googleMap.addMarker(new MarkerOptions().position(end)).setTitle("Finish");

        googleMap.addPolyline(new PolylineOptions().color(0xFFFF0000)
                .clickable(false)
                .add(start, end));


        CameraPosition loc = CameraPosition.builder().target(start).zoom(18).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(loc));


    }
}
