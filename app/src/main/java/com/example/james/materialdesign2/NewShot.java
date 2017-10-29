package com.example.james.materialdesign2;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import dto.ShotResultsDTO;

public class NewShot extends AppCompatActivity implements OnItemSelectedListener  {


    private Spinner clubSelector;
    private Spinner shotSelector;
    private String  club;
    private String  swingLength;
    private int guid;
    private ImageButton saveData;
    private Snackbar snackbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        spinners
         */
        clubSelector = (Spinner) findViewById(R.id.clubSelector);
        // Spinner click listener
        clubSelector.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);//TODO allow user to add new clubs to strings array

        // Spinner Drop down elements
        List<String> clubs = new ArrayList<String>();
        clubs.add("Driver");
        clubs.add("3 wood");
        clubs.add("5 Iron");
        clubs.add("6 Iron");
        clubs.add("7 Iron");
        clubs.add("8 Iron");
        clubs.add("9 Iron");
        clubs.add("Sand Wedge");

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
        shots.add("full");
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

        //save chosen screen results to strings
        club = clubSelector.getSelectedItem().toString();
        swingLength = shotSelector.getSelectedItem().toString();
        guid = 1;

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

    }

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
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


}








