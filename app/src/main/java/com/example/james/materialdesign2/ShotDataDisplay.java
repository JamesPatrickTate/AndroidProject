package com.example.james.materialdesign2;

import android.app.ListActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dto.ShotResultsDTO;

public class ShotDataDisplay extends ListActivity  {

    private TextView shots;

    //private List<ShotResultsDTO> list;
    private ArrayAdapter<ShotResultsDTO> shotData;
    final List<ShotResultsDTO> allSHotData = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
   private final List<String > shotIds= new ArrayList<>();
    private  ArrayAdapter<String> adapter;


    private ListView list;////////////////////


    String tag = "SDD :: ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ShotResultsDTO s = new ShotResultsDTO();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot_data_display);
       // list = (ListView) findViewById(R.id.);



        FirebaseAuth mAuth;
       mAuth = FirebaseAuth.getInstance();
       FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();

        databaseReference.child("shot/"+currentFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
        //databaseReference.child("shot/").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                //ctrl alt v gives us a correct variable to store the returned value
                //get back everything from dsot value
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();




               //iterate over the snapshots and add the data to
                //the shotresults dto object we previosly used the add the data

                for (DataSnapshot child: children) {
                    String sid = child.getKey();
                    ShotResultsDTO s = child.getValue(ShotResultsDTO.class);


                    Log.d(tag,"id"+ sid.toString());
                    Log.d(tag,"Shotresults dto"+ s.toString());
                    allSHotData.add(s);
                    shotIds.add(sid);


                }
               Log.d(tag,"inside allShotDataCount::"+allSHotData.size() );
                Log.d(tag," insideallShotidCount::"+shotIds.size());

               ShotDataDisplay.this.updateUIonFirebaseRetrieval();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



         adapter = new ArrayAdapter<String>(ShotDataDisplay.this,android.R.layout.simple_list_item_1, shotIds);


    }//end on create

    /**
     * this method ensures the call to firebase is completed.
     */

    public  void updateUIonFirebaseRetrieval(){

        setListAdapter(adapter);
    }

    /**
     * Get id of shot from user clicking list. Use this id to the the shotdata object
     * to the shot display activity
     * @param l
     * @param v
     * @param position
     * @param id
     */

    protected void onListItemClick(ListView l, View v, int position, long id) {
       // super.onListItemClick(l, v, position, id);
        int ID = (int) id;
        ShotResultsDTO chosenShotObject= allSHotData.get(ID);
        Log.d("CLICK",chosenShotObject.toString());

        Intent intent = new Intent(ShotDataDisplay.this, tester.class);
        intent.putExtra("serialize_data", chosenShotObject);
        startActivity(intent);
    }

}
