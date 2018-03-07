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

import dto.RoundStressDTO;

/**
 * calculate aver
 * Created by james on 07/03/2018.
 */

public class StressAverageCalculator {

    private List<RoundStressDTO> allStressData = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    Double averageGSR = 0.0;
    Double averageHeartRate = 0.0;
    Double averageSkinTemp = 0.0;
    Double totalGSR = 0.0;
    Double totalHeartRate = 0.0;
    Double totalSkinTemp = 0.0;
    int countGSR, countHearRate, countSkinTemp = 0;
    private static List<Double> averageStressors = new ArrayList<>();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();

    public StressAverageCalculator() {

        calculateAverages();


    }

    /**
     *  Caculates average for each stressor
     */

    private void calculateAverages(){
       databaseReference.child("round/"+currentFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {

           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               Iterable<DataSnapshot> children = dataSnapshot.getChildren();
               for (DataSnapshot child: children) {
                   String sid = child.getKey();
                   RoundStressDTO s = child.getValue(RoundStressDTO.class);
                   //  Log.d(tag,"id"+ sid.toString());


                   // find total gsr
                   for (double gsrReading : s.getGsrList()
                           ) {
                       countGSR++;
                       totalGSR+=gsrReading;
                       Log.d("Stressors","gsr"+ gsrReading);

                   }
                   // find total heart rate
                   for (double heartRateReading : s.getHeartRateList()
                           ) {
                       countHearRate++;
                       totalHeartRate+=heartRateReading;
                       Log.d("Stressors","hr"+ heartRateReading);

                   }
                   // find total skinTemp
                   for (double skinTempReading : s.getSkinTemp()
                           ) {
                       countSkinTemp++;
                       totalSkinTemp+=skinTempReading;
                       Log.d("Stressors","sk"+ skinTempReading);

                   }



               }// end for loop

               // average GSR
               averageGSR = totalGSR / countGSR;
               // average heart rate
               averageHeartRate = totalHeartRate / countHearRate;
               // average skin temperature
               averageSkinTemp = totalSkinTemp / countSkinTemp;

               averageStressors.add(averageGSR);
               averageStressors.add(averageHeartRate);
               averageStressors.add(averageSkinTemp);

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
   }

    /**
     *
     * @return three entry list:  index 0 = gsr average, index 1 = heartrate average, index 2 = skin temperature average.
     */

   public List<Double> getStressAverages() {
       return averageStressors;
   }





}
