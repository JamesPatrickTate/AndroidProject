package com.example.james.materialdesign2;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dto.ShotResultsDTO;

public class ShotAverages extends ListActivity {

    String tag = "shotAverages";

    private ArrayAdapter<ShotResultsDTO> shotData;
    final List<ShotResultsDTO> allSHotData = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private final List<String > shotIds= new ArrayList<>();
    private  ArrayAdapter<String> adapter;
    HashMap<String, Double> results= new HashMap<>();
    List<String>  averagesDisplayStringArray = new ArrayList<>();
    private CalculateAverages cav ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_shot_averages);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();

        databaseReference.child("shot/"+currentFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {

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
                    //  Log.d(tag,"id"+ sid.toString());
                     Log.d(tag,"Shotresults dto"+ s.toString());
                    allSHotData.add(s);
                    shotIds.add(sid);


                }
                Log.d("XXX","inside allShotavDataCount::"+allSHotData.size() );
                Log.d("MSG"," insideallShotavidCount::"+shotIds.size());

                cav = new CalculateAverages(allSHotData);
                results = cav.results();


                for (String s: results.keySet()) {
                    System.out.println(s+":"+results.get(s));
                    averagesDisplayStringArray.add(s+" :: "+results.get(s)+" m");
                    Log.d("XXX",s );

                }
                ShotAverages.this.updateUIonFirebaseRetrieval();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        List<String>  averagesDisplayStringArray = new ArrayList<>();
//        for (String s: results.keySet()) {
//            System.out.println(s+":"+results.get(s));
//            averagesDisplayStringArray.add(s+" :: "+results.get(s));
//            Log.d("XXX",s );
//
//        }



        adapter = new ArrayAdapter<String>(ShotAverages.this,android.R.layout.simple_list_item_1, averagesDisplayStringArray);

    }

    public  void updateUIonFirebaseRetrieval(){

        setListAdapter(adapter);
    }


}
