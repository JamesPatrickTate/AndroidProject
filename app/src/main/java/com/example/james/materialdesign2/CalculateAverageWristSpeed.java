package com.example.james.materialdesign2;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import dto.ShotResultsDTO;

/**
 * calculate aver
 * Created by james on 07/03/2018.
 */

public class CalculateAverageWristSpeed {


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
    static double counterWrist,counterDistance , averageWristSpeed, totalWristSpeed, averageDistance, totalDistance = 0;
    private String clubName, swinlength = " ";
    String TAG ="CalculateAverageWristSpeed";

    public CalculateAverageWristSpeed(String clubName, String swingLength) {
        this.clubName = clubName;
        this.swinlength = swingLength;
        calculateAverages();
    }

    /**
     *  Caculates average for each stressor
     */

    private void calculateAverages(){
        counterDistance = 0;
        counterWrist = 0;
        databaseReference.child("shot/"+currentFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child: children) {
                    String sid = child.getKey();
                  // ShotResultsDTO s = child.getValue(ShotResultsDTO.class);
                    ShotResultsDTO s = child.getValue(ShotResultsDTO.class);
                    if(clubName.equals(s.getClub())  && swinlength.equals(s.getSwingLength()) && s.getShotDistance() > 0) {

                        totalDistance += s.getShotDistance();
                        counterDistance++;
                        Log.d(TAG, "disance: "+ totalDistance);

                    }
                    if(clubName.equals(s.getClub())  && swinlength.equals(s.getSwingLength()) && s.getShotVelocity() > 0) {
                        totalWristSpeed += s.getShotVelocity();
                        Log.d(TAG, "speeed: "+ totalWristSpeed);
                        counterWrist++;
                    }
                   // Log.d(TAG, "counter: "+ counter);

                }// end for loop

              // calculate averages
                averageWristSpeed = totalWristSpeed / counterWrist;
                averageDistance = totalDistance / counterDistance;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     *
     * @return average stress
     */

    public double getWristSpeedAverages() {
        return averageWristSpeed;
    }
 /**
     *
     * @return average hit distance
     */

    public double getDisatanceAverages() {
        return averageDistance;
    }





}
