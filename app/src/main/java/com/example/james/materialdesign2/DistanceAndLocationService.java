package com.example.james.materialdesign2;




        import android.Manifest;
        import android.app.Service;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Binder;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.support.v4.app.ActivityCompat;

public class DistanceAndLocationService extends Service {

    DistanceTravelBinder mDistanceTravelBinder = new DistanceTravelBinder();
    static double distanceInMetres = 0;
    static Location lastLocation = null;

    public DistanceAndLocationService() {
    }

    @Override
    public void onCreate() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null) {
                    lastLocation = location;
                }
                distanceInMetres += location.distanceTo(lastLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mDistanceTravelBinder;
    }


    public class DistanceTravelBinder extends Binder{
        DistanceAndLocationService getBinder(){
            return DistanceAndLocationService.this;
        }
    }

    public double getDistanceTraveled(){
        return distanceInMetres;
    }
}