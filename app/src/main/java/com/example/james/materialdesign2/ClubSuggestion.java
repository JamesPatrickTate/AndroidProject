package com.example.james.materialdesign2;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.example.james.materialdesign2.FirebaseDB.allSHotData;

public class ClubSuggestion extends  ListActivity {

    private TextView displaySuggestion;
    private  EditText distanceRequiredByUser;
    private Button btn;
    private String  userInput;
    private static final String TAG = "userInput";

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
    private static int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_suggestion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();

        distanceRequiredByUser = (EditText) findViewById(R.id.inputSuggestion);

        btn = (Button) findViewById(R.id.getSuggestion);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            // refresh screen if user changes value
                counter++;
                if(counter > 1) {

                    Toast msg = Toast.makeText(getBaseContext(),"Please re-enter value. Screen refresh required",Toast.LENGTH_LONG);
                    msg.show();

                    counter = 0;
                    finish();
                    startActivity(getIntent());
                }


                 final String str = distanceRequiredByUser.getText().toString();


                databaseReference.child("shot/"+currentFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                         double userInputValue = Double.parseDouble(str);
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

                        //ensure the results array is empty before we add our suggestions
                        Log.d("XXX","Is results empty ::"+results.size() );
                        results.clear();

                        cav = new CalculateAverages(allSHotData);
                        results = cav.suggestionResults(userInputValue);



                        //if no values within acceptable range
                        // print message to screen

                        if(results.isEmpty()){

                            Toast msg = Toast.makeText(getBaseContext(),"Sorry you have no shots within 4M for a club suggestion." +
                                    " Remember to log all of your shots to build up more data. Good luck! ",Toast.LENGTH_LONG);
                            msg.show();
                        }


                        for (String s: results.keySet()) {
                            System.out.println(s+":"+results.get(s));
                            averagesDisplayStringArray.add(s+" :: "+results.get(s)+" m");
                            Log.d("XXX",s );

                        }
                        ClubSuggestion.this.updateUIonFirebaseRetrieval();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                adapter = new ArrayAdapter<String>(ClubSuggestion.this,android.R.layout.simple_list_item_1, averagesDisplayStringArray);




            }
        });




    }

    public  void updateUIonFirebaseRetrieval(){

        setListAdapter(adapter);


    }
}
