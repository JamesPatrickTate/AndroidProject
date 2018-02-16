package com.example.james.materialdesign2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import dto.AccelerationEventCordinatesAndTime;
import dto.ShotResultsDTO;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import dto.UserDTO;


/**
 * https://developers.google.com/android/guides/api-client workng on this page for maps
 *
 *
 * http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&APPID=acd069c689dcab6dd5f89d4425a2045c
 */

public class NewShot extends AppCompatActivity implements OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener


{

    //private static final String TAG = "NewShot";

    private Spinner clubSelector;
    private Spinner shotSelector;
    private String club;
    private String swingLength;
    private String UniqueShotID;//todo make unique
    private ImageButton saveData;
    private Snackbar snackbar;
    float xVal, yVal, zVal;
    private TextView txtStatus;
    private Button accStart, accEnd, distStart, distEnd;
    private ArrayList<AccelerationEventCordinatesAndTime> swingDataPoints = new ArrayList<>();
    private TextView maxVelView;
    private TextView sDist;
    private static  String golfCourseAddress = "Sorry Location Unavaiable";
    private double maxVel;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private double shotDistance;
    static Location currentLocation, startLocation, endLocation;
    private  FirebaseUser currentFirebaseUser;
    static final int REQUEST_LOCATION = 1;
    public LocationManager locationManager;
    LocationListener locationListener, listener;
    private BandClient client = null;
    private static final UUID tileId = UUID.fromString("cc0D508F-70A3-47D4-BBA3-812BADB1F8Aa");
    private static final UUID pageId1 = UUID.fromString("b1234567-89ab-cdef-0123-456789abcd00");
    private TextView st;
    private TextView en;
    static Location lastLocation = null;
    static double distanceInMetres = 0;
    final String TAG = "NEWSHOT";
    private long UPDATE_INTERVAL = 1000;  /* 1 sec */
    private long FASTEST_INTERVAL = 500; /* 1/2 sec */
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    GoogleApiClient gac;
    LocationRequest locationRequest;
    TextView tvLatitude, tvLongitude, tvTime;
    Geocoder geocoder;



    boolean startRecordingDataPoints = false;
    double gsrValue = 0.0;
    ////////////////////////

    private BandGsrEventListener mGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(final BandGsrEvent event) {
            if (event != null) {
                gsrValue = event.getResistance();
//                tvLatitude.setText("gsr:" + event.getResistance());

            }
        }
    };

    ////////////////////////

    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {

            //create data transfer object for event
            AccelerationEventCordinatesAndTime swingDataPoint = new AccelerationEventCordinatesAndTime();

            if (event != null) {
                xVal = event.getAccelerationX();
                yVal = event.getAccelerationY();
                zVal = event.getAccelerationZ();

                swingDataPoint.setX(xVal);
                swingDataPoint.setY(yVal);
                swingDataPoint.setZ(zVal);
                swingDataPoint.setTime(System.currentTimeMillis());
                Log.d("XXXXX", "recorded time " + System.currentTimeMillis());
                swingDataPoints.add(swingDataPoint);


                appendToUI(String.format(" Acceleration Values:: X = %.0f hz  Y = %.0f hz   Z = %.1f hz  ", xVal, yVal, zVal, swingDataPoint));
            }
        }
    };



    ///////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // record gsr value then close gsr listener
        new GsrSubscriptionTask().execute();

        if (gsrValue != 0.0) {
            try {
                client.getSensorManager().unregisterGsrEventListener(mGsrEventListener);
            } catch (BandIOException e) {
                System.out.println("GSR PROBLEM");
            }
        }

        UniqueShotID = "testValue";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        accStart = (Button) findViewById(R.id.accStart);
        accEnd = (Button) findViewById(R.id.accEnd);
        distStart = (Button) findViewById(R.id.distStart);
        distEnd = (Button) findViewById(R.id.distEnd);








        /*
        spinners
         */
        clubSelector = (Spinner) findViewById(R.id.clubSelector);
        // Spinner click listener
        clubSelector.setOnItemSelectedListener((OnItemSelectedListener) this);//TODO allow user to add new clubs to strings array

        // Spinner Drop down elements
        List<String> clubs = new ArrayList<String>();
        clubs.add("D");
        clubs.add("3W");
        clubs.add("5I");
        clubs.add("6I");
        clubs.add("7I");
        clubs.add("8I");
        clubs.add("9I");
        clubs.add("SW");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, clubs);


        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        clubSelector.setAdapter(dataAdapter);

        ////////////////////////////////////
        shotSelector = (Spinner) findViewById(R.id.shotSelector);
        // Spinner click listener
        shotSelector.setOnItemSelectedListener((OnItemSelectedListener) this);//TODO allow user to add new clubs to strings array

        // Spinner Drop down elements
        List<String> shots = new ArrayList<String>();
        shots.add("Full");
        shots.add("3/4");
        //shots.add("2/3");
        shots.add("1/2");
        //shots.add("1/3");
        shots.add("1/4");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shots);


        // Drop down layout style
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        shotSelector.setAdapter(dataAdapter2);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //todo add acceleration and distance  to a service
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        /// acceleration for swing on and off
        accStart.setOnClickListener(new View.OnClickListener() {





            @Override
            public void onClick(View v) {





                startRecordingDataPoints = true;
                txtStatus.setText("");


                new AccelerometerSubscriptionTask().execute();
                snackbar = Snackbar
                        .make(getWindow().getDecorView().getRootView(), "Swing Record ON.", Snackbar.LENGTH_SHORT);
                snackbar.show();



            }
        });

        accEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    client.disconnect().await();
                } catch (Exception exception) {

                    System.out.println("Error disconnecting acceleration listener");
                }



                maxVelView = (TextView) findViewById(R.id.mavValue);

                //display max velocity
                maxVel = 0;
                SwingVelocity sv = new SwingVelocity();
                maxVel = sv.getMaxAccelerometerValues(swingDataPoints);
                maxVel = round(maxVel, 3);
                maxVelView.setText(maxVel + "m/s");

                txtStatus.setText("");
                snackbar = Snackbar
                        .make(getWindow().getDecorView().getRootView(), "Swing Record OFF. Arraysize" + swingDataPoints.size(), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        /*
            Distance , location

         */


        ///////////////////////

        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        // tvTime = (TextView) findViewById(R.id.tvTime);

        isGooglePlayServicesAvailable();

        if (!isLocationEnabled())
            showAlert();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();




        geocoder = new Geocoder(this, Locale.getDefault());

        distStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                startLocation = currentLocation;

                if(startLocation != null) {

                    startLatitude = startLocation.getLatitude();
                    startLongitude = startLocation.getLongitude();

                    Handler h = new Handler();

                   getAddressFromLocation(endLatitude, endLongitude,NewShot.this, h );

                }else {
                    Toast.makeText(NewShot.this, "Location local failed please wait!", Toast.LENGTH_LONG).show();
                }

            }
        });

       

        //Address golfCourseName = getAddress(currentLocation.getLatitude(), currentLocation.getLongitude());


        sDist = (TextView) findViewById(R.id.distval);

        distEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endLocation = currentLocation;
                endLatitude = endLocation.getLatitude();
                endLongitude = endLocation.getLongitude();
                tvLongitude.setText("ela:" + endLatitude + ", elo:" + endLongitude);



                float[] t = new float[5];
                Location.distanceBetween(startLatitude,startLongitude,endLatitude,endLongitude,t);
                shotDistance =t[0];
                sDist.setText(shotDistance + "m\n");

                tvLongitude.setText("accuracy:"+endLocation.getAccuracy());


                //tvLatitude.setText("bet:"+t[0]);

            }
        });


        /////////////////////////
        /*
            save button
         */

        saveData = (ImageButton) findViewById(R.id.saveData);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar = Snackbar
                        .make(getWindow().getDecorView().getRootView(), R.string.try_to_save,
                                Snackbar.LENGTH_SHORT);
                snackbar.show();

                Handler h = new Handler();

                   getAddressFromLocation(endLatitude, endLongitude,NewShot.this, h );

                onSaveClicked();

                //Intent intent = new Intent(NewShot.this, ShotResults.class);

               // startActivity(intent);
            }
        });


    }//on create end

    /**
     * reduce the length of the distance variable to 3 decimal places
     *
     * @param value
     * @param places
     * @return
     */
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Distance and location methods
     */

    @Override
    protected void onStart() {
        gac.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        gac.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // updateUI(location);
            currentLocation = location;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewShot.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
        Log.d(TAG, "onConnected");

        Location ll = LocationServices.FusedLocationApi.getLastLocation(gac);
        Log.d(TAG, "LastLocation: " + (ll == null ? "NO LastLocation" : ll.toString()));

        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
        //locationManager.requestLocationUpdates(gac,1,1,this);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(NewShot.this, "Permission was granted!", Toast.LENGTH_LONG).show();

                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                gac, locationRequest, this);
                    } catch (SecurityException e) {
                        Toast.makeText(NewShot.this, "SecurityException:\n" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NewShot.this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(NewShot.this, "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
        Log.d("DDD", connectionResult.toString());
    }

//    private void updateUI(Location loc) {
//        Log.d(TAG, "updateUI");
////            tvLatitude.setText(Double.toString(loc.getLatitude()));
////            tvLongitude.setText(Double.toString(loc.getLongitude()));
//        //tvTime.setText(DateFormat.getTimeInstance().format(loc.getTime()));
//    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGooglePlayServicesAvailable() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.d(TAG, "This device is supported.");
        return true;
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }
//////////////////////////////////////////////////////////////


    /**
     * creates dto and saves to data base
     */
    public  void onSaveClicked() {


//

        Toast.makeText(this, "Address : "+ golfCourseAddress , Toast.LENGTH_LONG).show();

        club = clubSelector.getSelectedItem().toString();
        swingLength = shotSelector.getSelectedItem().toString();

        // create a DTO to hold our shotData information.
        ShotResultsDTO shotData = new ShotResultsDTO();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        Bundle bundle = getIntent().getExtras();
        String userEmail = bundle.getString("Email");
        //String s = currentFirebaseUser;
        // populate the shotData with values from the screen.
        shotData.setClub(club);
        shotData.setSwingLength(swingLength);
        shotData.setUserID(currentFirebaseUser.getUid());
        shotData.setShotDistance(shotDistance);
        shotData.setShotVelocity(maxVel);
        shotData.setStartLatitude(startLatitude);
        shotData.setStartLongitude(startLongitude);
        shotData.setEndLatitude(endLatitude);
        shotData.setEndLongitude(endLongitude);
        shotData.setGsr(gsrValue);
        shotData.setgolfCourseAddress(golfCourseAddress);
        shotData.setEmail(userEmail);








        //unique shot id
        Date dNow = new Date( );
        Format formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        UniqueShotID = formatter.format(dNow);


        // save the shotData.
        try {
            // specimenDAO.save(shotData);#
//            Bundle bundle = getIntent().getExtras();
//            String userEmail = bundle.getString("Email");
            //Toast.makeText(this, "email: "+userEmail, Toast.LENGTH_LONG).show();


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference();
            //databaseReference.child("shot/"+UniqueShotID).setValue(shotData);

           //expirement
            databaseReference.child("shot/"+currentFirebaseUser.getUid()+"/"+UniqueShotID).setValue(shotData);
//            databaseReference.child("shot/"+UniqueShotID).push().setValue(shotData);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.couldNotSaveShotData, Toast.LENGTH_LONG).show();
        }
            String wetherData =  " ";

//        try {
//
//            String openWeatherAPi= "http://api.openweathermap.org/data/2.5/weather?lat="+startLatitude+"&lon="+startLongitude+"&APPID=acd069c689dcab6dd5f89d4425a2045c";
//            URL url = new URL(openWeatherAPi);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            try {
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                StringBuilder stringBuilder = new StringBuilder();
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    stringBuilder.append(line).append("\n");
//                }
//                bufferedReader.close();
//                wetherData =  stringBuilder.toString();
//                Log.i(TAG, "api return  "+ wetherData);
//            }
//            finally{
//                urlConnection.disconnect();
//            }
//        }
//        catch(Exception e) {
//            Log.e("ERROR", e.getMessage(), e);
//
//        }
//
//
//        Toast.makeText(this, wetherData, Toast.LENGTH_LONG).show();









    }//end save on clicked

    /**
     * Spinner related methods.
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    // band related TODO refactor reusued band methods to a band class

    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(string);
            }
        });
    }


    private class AccelerometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI("Band is connected.\n");
                    client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage;
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        return ConnectionState.CONNECTED == client.connect().await();
    }


    /**
     * Gsr subscription
     */
    private class GsrSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        appendToUI("Band is connected.\n");
                        client.getSensorManager().registerGsrEventListener(mGsrEventListener);
                    } else {
                        appendToUI("The Gsr sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }




    public static void setAddress(String s)
    {
        golfCourseAddress = s;

    }



    public static void getAddressFromLocation(final double latitude, final double longitude, final Context context, final Handler handler) {
        Thread thread = new Thread() {


            @Override
            public void run() {

                Log.i("NEWSHOT", "lat and long "  + latitude +", "+ longitude ) ;
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = "No Address Available";
                try {
                    List<Address> addresses;

                    List < Address > addressList = geocoder.getFromLocation(latitude, longitude, 1);




                    if (addressList != null && addressList.size() > 0) {
                       Address address = addressList.get(0);
                       StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)); //.append("\n");
                       }
                        sb.append(address.getAddressLine(0)).append("\n");
//                        sb.append(address.getFeatureName()).append("\n");
//                       sb.append(address.getLocality()).append("\n");
//                        sb.append(address.getCountryName());
                       result = sb.toString();


                       NewShot.setAddress(result);



                        ArrayList<String> addressFragments = new ArrayList<String>();


                    }
                } catch (IOException e) {
                    Log.e("Location Address Loader", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = " Unable to get address for this location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }



}


//////////////////////////////////////////////////////
//end of new shot activity








