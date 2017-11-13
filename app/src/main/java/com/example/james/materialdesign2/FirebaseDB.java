package com.example.james.materialdesign2;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dto.ShotResultsDTO;
import dto.UserDTO;

/**
 * Created by james on 09/11/2017.
 */

public class FirebaseDB {

    private ShotResultsDTO  shotResultsDTO;
    private UserDTO userDTO;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference  databaseReference = database.getReference();
    public static List<ShotResultsDTO> allSHotData = new ArrayList<>();



    public FirebaseDB() {




    }

    public void uploadShotDataToDB(String tableName, ShotResultsDTO  shotResultsDTO ) {
        //todo move shot results to here
        //databaseReference.child("shot/" + UniqueShotID).setValue(shotData);
    }


    public void uploaduserDataToDB(String id, UserDTO userDTO ) {

        databaseReference.child("Users/" + id).setValue(userDTO);
    }

    /**
     * https://www.youtube.com/watch?v=aPLh31MWewc
     * retrieve all shot data for every user
     */
    public List<ShotResultsDTO> getShotData() {//todo make this only for current id



            databaseReference.child("shot/").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //ctrl alt v gives us a correct variable to store the returned value
                    //get back everything from dsot value
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    //iterate over the snapshots and add the data to
                    //the shotresults dto object we previosly used the add the data
                    for (DataSnapshot child: children) {

                        ShotResultsDTO singleShotData = child.getValue(ShotResultsDTO.class);
                        allSHotData.add(singleShotData);
                    }



                    //setAllSHotData(allSHotData);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        return  allSHotData;
    }








}
