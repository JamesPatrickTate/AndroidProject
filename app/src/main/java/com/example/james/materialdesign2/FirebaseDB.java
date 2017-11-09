package com.example.james.materialdesign2;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dto.ShotResultsDTO;
import dto.UserDTO;

/**
 * Created by james on 09/11/2017.
 */

public class FirebaseDB {

    private ShotResultsDTO  shotResultsDTO;
    private UserDTO userDTO;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();


    public FirebaseDB() {



    }

    public void uploadShotDataToDB(String tableName, ShotResultsDTO  shotResultsDTO ) {
        //todo move shot results to here
        //databaseReference.child("shot/" + UniqueShotID).setValue(shotData);
    }
    public void uploaduserDataToDB(String id, UserDTO userDTO ) {

        databaseReference.child("Users/" + id).setValue(userDTO);
    }


}
