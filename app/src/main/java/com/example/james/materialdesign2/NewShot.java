package com.example.james.materialdesign2;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dto.AccelerationEventCordinatesAndTime;
import dto.ShotResultsDTO;

public class NewShot extends AppCompatActivity implements OnItemSelectedListener  {


    private Spinner clubSelector;
    private Spinner shotSelector;
    private String  club;
    private String  swingLength;
    private int guid;//todo make unique
    private ImageButton saveData;
    private Snackbar snackbar;
    float xVal, yVal, zVal;
    private TextView txtStatus;
    private Button accStart, accEnd, distStart, distEnd;
    private ArrayList <AccelerationEventCordinatesAndTime>  swingDataPoints = new ArrayList<>();

    private BandClient client = null;
    private static final UUID tileId = UUID.fromString("cc0D508F-70A3-47D4-BBA3-812BADB1F8Aa");
    private static final UUID pageId1 = UUID.fromString("b1234567-89ab-cdef-0123-456789abcd00");



    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {

            //create data transfer object for event
            AccelerationEventCordinatesAndTime swingDataPoint = new AccelerationEventCordinatesAndTime();

            if (event != null) {
                xVal=event.getAccelerationX();
                yVal=event.getAccelerationY();
                zVal=event.getAccelerationZ();

                swingDataPoint.setX(xVal);
                swingDataPoint.setY(yVal);
                swingDataPoint.setZ(zVal);
                swingDataPoint.setTime(System.currentTimeMillis());
                swingDataPoints.add(swingDataPoint);


                appendToUI(String.format(" Acceleration Values:: X = %.0f hz  Y = %.0f hz   Z = %.1f hz  " , xVal, yVal,zVal, swingDataPoint));
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        accStart = (Button) findViewById(R.id.accStart);
        accEnd = (Button) findViewById(R.id.accEnd);
        distStart = (Button) findViewById(R.id.distStart);
        distEnd = (Button) findViewById(R.id.distEnd);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        /*
        spinners
         */
        clubSelector = (Spinner) findViewById(R.id.clubSelector);
        // Spinner click listener
        clubSelector.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);//TODO allow user to add new clubs to strings array

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
        shotSelector.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);//TODO allow user to add new clubs to strings array

        // Spinner Drop down elements
        List<String> shots = new ArrayList<String>();
        shots.add("Full");
        shots.add("3/4");
        shots.add("2/3");
        shots.add("1/2");
        shots.add("1/3");
        shots.add("1/4");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shots);


        // Drop down layout style
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        shotSelector.setAdapter(dataAdapter2);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /// acceleration for swing on and off

        accStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                try{
                client.disconnect().await();
                }
                catch(Exception exception){

                    System.out.println("Error disconnecting acceleration listener");
                }

                txtStatus.setText("");
                snackbar = Snackbar
                        .make(getWindow().getDecorView().getRootView(), "Swing Record OFF. arraysize"+swingDataPoints.size(), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        /////////////////////



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

                onSaveClicked();
            }
        });





    }//on create end

    /**
     * creates dto and saves to data base
     */
    public void onSaveClicked() {
        club = clubSelector.getSelectedItem().toString();
        swingLength = shotSelector.getSelectedItem().toString();
        guid = 2;
        // create a DTO to hold our shotData information.
        ShotResultsDTO shotData = new ShotResultsDTO();

        // populate the shotData with values from the screen.
        shotData.setClub(club);
        shotData.setSwingLength(swingLength);
        shotData.setGuid(guid);


        // save the shotData.
        try {
            // specimenDAO.save(shotData);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference();
            databaseReference.child("foo").push().setValue(shotData);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.couldNotSaveShotData, Toast.LENGTH_LONG).show();
        }

    }//end save on clicked

    /**
     * Spinner related methods.
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




}//end of new shot activity








