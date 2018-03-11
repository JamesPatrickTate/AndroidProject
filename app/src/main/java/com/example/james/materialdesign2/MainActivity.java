package com.example.james.materialdesign2;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

//band imports///////////////////////////////////////////////////
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.ScrollFlowPanel;
import com.microsoft.band.tiles.pages.TextButton;
import com.microsoft.band.tiles.pages.TextButtonData;


import android.os.AsyncTask;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import dto.RoundStressDTO;
import dto.ShotResultsDTO;

import static com.example.james.materialdesign2.StressMeasurementService.MESSAGE;


/////////////////////////////////////////////////////////////////

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{

    protected BandClient client = null;
    private Button btnStart;
    protected static final UUID tileId = UUID.fromString("cc0D508F-70A3-47D4-BBA3-812BADB1F8Aa");
    protected static final UUID pageId1 = UUID.fromString("b1234567-89ab-cdef-0123-456789abcd00");
    protected static final UUID pageId2 = UUID.fromString("f8508b88-bce8-11e7-abc4-cec278b6b50a");
    protected static final UUID pageId3 = UUID.fromString("2195938e-bcea-11e7-abc4-cec278b6b50a");
    protected static final UUID pageId4 = UUID.fromString("40075faa-bcea-11e7-abc4-cec278b6b50a");
    private TextView txtStatus, stressAVGTextView;
    private TextView txtStatusMain;
    private ScrollView scrollView;
    private Button btnStop;
    private Snackbar snackbar;
    private FloatingActionButton newShot;
    private FloatingActionButton stats;
    private Intent myIntent;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    final WeakReference<Activity> reference  = new WeakReference<Activity>(this);
    public static List<Integer> heartRateList = new ArrayList<>();
    public static List<Float> skinTemp = new ArrayList<>();
    public static List<Double> gsrList = new ArrayList<>();
    public static List<Long> gsrTimes = new ArrayList<>();
    public static List<Long> HRtimes = new ArrayList<>();
    public static List<Long> skinTimes = new ArrayList<>();
    private FirebaseUser currentFirebaseUser;
    private String UniqueShotID;
    private StressAverageCalculator stressAverageCalculator;
    private List<Double> stressors = new ArrayList<>();
    String TAG = "STRESS";

    private  long intialCalories, finalCalories, totalCaloriesBurned = 0;
    public static long currentCals =0;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        stressAverageCalculator = new StressAverageCalculator();



        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        drawerFragment.setDrawerListener(this);



        btnStart = (Button) findViewById(R.id.startButton);
        btnStop = (Button) findViewById(R.id.done);

        scrollView = (ScrollView) findViewById(R.id.svTest);
        newShot = (FloatingActionButton) findViewById(R.id.newShot);






        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                new HeartRateConsentTask().execute(reference);
                startService(btnStart);
                stressAVGTextView = (TextView) findViewById(R.id.stressAverage);
                stressors = stressAverageCalculator.getStressAverages();
                Log.d("AVG", "onCreate: "+ stressors);
                if(stressors.size() > 0)
                    stressAVGTextView.setText("GSR: "+HelperMethods.round(stressors.get(0),2)+", Heart Rate: "+HelperMethods.round(stressors.get(1),2)+", Skin Temp: "+HelperMethods.round(stressors.get(2),2));


            }
        });



        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                disableButtons();
//                new StopTask().execute();

                Toast.makeText(MainActivity.this, "Heart rate count " + heartRateList.size(), Toast.LENGTH_LONG).show();
                stopService(btnStop);
                onSaveClicked();

                Log.d(TAG, "heart rate length: " + heartRateList +
                                "\n GSR list length: " + gsrList +
                                "\n skin temp list: " + skinTemp);
            }
        });

        newShot = (FloatingActionButton) findViewById(R.id.newShot);
        newShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openNewShot(newShot);
            }
        });


        stats = (FloatingActionButton) findViewById(R.id.stats);
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSuggestions(stats);
            }
        });


        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtStatus.setText("");


        //add messages from heartrate service
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String message = intent.getStringExtra(MESSAGE);

                        txtStatus.setText("From Service: " + message);
                    }
                }, new IntentFilter(StressMeasurementService.MESSAGE_FROM_SERVICE)
        );





    }//end of onCreate

    //save stress data for round
    /**
     * creates dto and saves to data base
     */
    public  void onSaveClicked() {

        // create a DTO to hold our stressData information.
        RoundStressDTO roundStressDTO = new RoundStressDTO();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        Bundle bundle = getIntent().getExtras();
        String userEmail = bundle.getString("Email");
        Date dNow = new Date( );
        Format formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        UniqueShotID = formatter.format(dNow);
        Format day = new SimpleDateFormat("yyyy-MM-dd");
        String dayOfShot = day.format(dNow);
        roundStressDTO.setDay(dayOfShot);
        roundStressDTO.setEmail(userEmail);
        roundStressDTO.setSkinTimes(skinTimes);
        roundStressDTO.setSkinTemp(skinTemp);
        roundStressDTO.setHRtimes(HRtimes);
        roundStressDTO.setHeartRateList(heartRateList);
        roundStressDTO.setGsrList(gsrList);
        roundStressDTO.setGsrTimes(gsrTimes);

        try {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference();
            databaseReference.child("round/"+currentFirebaseUser.getUid()+
                    "/"+UniqueShotID).setValue(roundStressDTO);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.couldNotSaveShotData, Toast.LENGTH_LONG).show();
        }
        String wetherData =  " ";

    }//end save on clicked


    /**
     * start heart rate service
     */
    public void startService( View view) {
        //heart rate works
        Intent heartRateIntent = new Intent(this, StressMeasurementService.class);
        startService(heartRateIntent);
//        //calorie doesnt work
//        Intent calorieIntent = new Intent(this, CaloriesService.class);
//        startService(calorieIntent);
        //skin temp


        //intialCalories = currentCals;
        //Toast.makeText(MainActivity.this, "intialCalories " + intialCalories, Toast.LENGTH_LONG).show();

    }



    /**
     * stop heart rate service
     */
    public void stopService(View view) {
        Intent heartRateIntent = new Intent(this, StressMeasurementService.class);
        stopService(heartRateIntent);



        //calories
//        finalCalories = currentCals;
//        totalCaloriesBurned = finalCalories - intialCalories;
//        Toast.makeText(MainActivity.this, " finalCalories " +  finalCalories+ " totalCaloriesBurned "+totalCaloriesBurned, Toast.LENGTH_LONG).show();
//        Intent calorieIntent = new Intent(this, CaloriesService.class);
//        stopService(calorieIntent);
    }


    //heart rate consent
    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient()) {

                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
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


    private void openSuggestions(View view) {

        Intent intent = new Intent(this, ClubSuggestion.class);
        startActivity(intent);
    }

    private void openNewShot(View view) {


        Bundle bundle = getIntent().getExtras();
        String userEmail = bundle.getString("Email");

        Intent intent = new Intent(this, NewShot.class);
        intent.putExtra("Email", userEmail);

        startActivity(intent);
    }

    private void openShotData(View view) {

        Intent intent = new Intent(this, ShotDataDisplay.class);

        startActivity(intent);
    }

    @Override
        protected void onNewIntent(Intent intent) {
            processIntent(intent);
            super.onNewIntent(intent);
        }

        @Override
        protected void onResume() {
            super.onResume();

            if(getIntent() != null && getIntent().getExtras() != null){
                processIntent(getIntent());
            }
        }


        @Override
        protected void onDestroy() {
            if (client != null) {
                try {
                    client.disconnect().await();
                } catch (InterruptedException e) {
                    // Do nothing as this is happening during destroy
                } catch (BandException e) {
                    // Do nothing as this is happening during destroy
                }
            }
            super.onDestroy();
        }

    protected void processIntent(Intent intent){
        String extraString = intent.getStringExtra(getString(R.string.intent_key));

        if(extraString != null && extraString.equals(getString(R.string.intent_value))){
            if (intent.getAction() == TileEvent.ACTION_TILE_OPENED) {
                TileEvent tileOpenData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                //appendToUI("Tile open event received\n" + tileOpenData.toString() + "\n\n");
                snackbar = Snackbar
                        .make(getWindow().getDecorView().getRootView(), "Tile open event received\n" + tileOpenData.toString() + "\n\n",
                                Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED) {
                TileButtonEvent buttonData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                //appendToUI("Button event received\n" + buttonData.toString() + "\n\n");
                snackbar = Snackbar
                        .make(getWindow().getDecorView().getRootView(), "Button event received\n" + buttonData.toString() + "\n\n",
                                Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else if (intent.getAction() == TileEvent.ACTION_TILE_CLOSED) {
                TileEvent tileCloseData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                 //appendToUI("Tile close event received\n" + tileCloseData.toString() + "\n\n");
                snackbar = Snackbar
                        .make(getWindow().getDecorView().getRootView(), "Tile close event received\n" + tileCloseData.toString() + "\n\n",
                                Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }

    /*
        TaskMesages is a helper class allows use to use the snack bar message in classes which use extends AsyncTask. Because
         these use worker thread and we need access to the root view

     */

     class TaskMesages{

        public void removeTileMessage(){
            snackbar = Snackbar
                    .make(getWindow().getDecorView().getRootView(), "Stopping app and removing Band Tile", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

        public void removeMesage(){
            snackbar = Snackbar
                    .make(getWindow().getDecorView().getRootView(), "Removing Tile", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }



        public void btOff(){
            snackbar = Snackbar
                    .make(getWindow().getDecorView().getRootView(), "Band isn't connected. Please make sure bluetooth is on and the band is in range", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        public void connected(){
            snackbar = Snackbar
                    .make(getWindow().getDecorView().getRootView(), "Band is connected", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }//end of Stop task message


    protected class StartTask extends AsyncTask<Void, Void, Boolean> {

        TaskMesages messages = new TaskMesages();
        @Override
        protected void onPreExecute() {
            //txtStatus.setText("");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                   // appendToUI("Band is connected.\n");
                    messages.connected();
                    if (addTile()) {
                        updatePages();
                    }
                } else {
                    //appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                    messages.btOff();
                    return false;
                }
            } catch (BandException e) {
                handleBandException(e);
                return false;
            } catch (Exception e) {
                appendToUI(e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                btnStop.setEnabled(true);
            } else {
                btnStart.setEnabled(true);
            }
        }
    }


    protected class StopTask extends AsyncTask<Void, Void, Boolean> {
        TaskMesages messages = new TaskMesages();
        @Override
        protected Boolean doInBackground(Void... params) {
            //appendToUI("Stopping demo and removing Band Tile\n");

            messages.removeTileMessage();
            try {
                if (getConnectedBandClient()) {
                    //appendToUI("Removing Tile.\n");
                    messages.removeMesage();
                    removeTile();
                } else {
                    //appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                    messages.btOff();
                }
            } catch (BandException e) {
                handleBandException(e);
                return false;
            } catch (Exception e) {
                appendToUI(e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
               // appendToUI("Stop completed.\n");
                snackbar = Snackbar
                        .make(getWindow().getDecorView().getRootView(), "Stop completed.", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            btnStart.setEnabled(true);
        }
    }

    private void disableButtons() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnStart.setEnabled(false);
                btnStop.setEnabled(false);
            }
        });
    }

    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.append(string);
                scrollView.post(new Runnable(){
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, txtStatus.getBottom());
                    }

                });
            }
        });
    }

    private void removeTile() throws BandIOException, InterruptedException, BandException {
        if (doesTileExist()) {
            client.getTileManager().removeTile(tileId).await();
        }
    }


    private boolean doesTileExist() throws BandIOException, InterruptedException, BandException {
        List<BandTile> tiles = client.getTileManager().getTiles().await();
        for (BandTile tile : tiles) {
            if (tile.getTileId().equals(tileId)) {
                return true;
            }
        }
        return false;
    }

    private boolean addTile() throws Exception {
        if (doesTileExist()) {
            return true;
        }

		/* Set the options */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.raw.b_icon, options);

        BandTile tile = new BandTile.Builder(tileId, "Button Tile", tileIcon)
                .setPageLayouts(createButtonLayout())
                .build();
        //appendToUI("Button Tile is adding ...\n");
        snackbar = Snackbar
                .make(getWindow().getDecorView().getRootView(), "Button Tile is adding ...", Snackbar.LENGTH_SHORT);
        snackbar.show();
        if (client.getTileManager().addTile(this, tile).await()) {
            //appendToUI("Button Tile is added.\n");
             snackbar = Snackbar
                    .make(getWindow().getDecorView().getRootView(), "Button Tile is added", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return true;
        } else {
            //appendToUI("Unable to add button tile to the band.\n");
             snackbar = Snackbar
                    .make(getWindow().getDecorView().getRootView(), "Unable to add button tile to the band", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
    }

//    protected PageLayout createButtonLayout() {
//        return new PageLayout(
//        new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.HORIZONTAL)
//                .addElements(new FilledButton(0, 5, 210, 45).setMargins(0, 5, 0, 0).setId(12).setBackgroundColor(Color.RED))
//                .addElements(new TextButton(0, 0, 210, 45).setMargins(0, 5, 0, 0).setId(21).setPressedColor(Color.BLUE)));
//
//
//
//    }
//
//
//
//
//    protected void updatePages() throws BandIOException {
//        client.getTileManager().setPages(tileId,
//                new PageData(pageId1,  0)
//                        .update(new FilledButtonData(12, Color.YELLOW))
//                        .update(new TextButtonData(12, "Start Swing")));
//
//         snackbar = Snackbar
//                .make(getWindow().getDecorView().getRootView(), "Send button page data to tile page", Snackbar.LENGTH_SHORT);
//        snackbar.show();
//    }
    //////////
    private PageLayout createButtonLayout() {
        return new PageLayout(
                new ScrollFlowPanel(15, 0, 260, 105, FlowPanelOrientation.HORIZONTAL

                )
                        .addElements(new TextButton(0, 0, 50, 90).setMargins(5, 5, 0, 0).setId(12).setPressedColor(Color.RED))
                        .addElements(new TextButton(0, 0, 50, 90).setMargins(5, 5, 0, 0).setId(21).setPressedColor(Color.RED))
                        .addElements(new TextButton(0, 0, 50, 90).setMargins(5, 5, 0, 0).setId(22).setPressedColor(Color.RED))
                        .addElements(new TextButton(0, 0, 50, 90).setMargins(5, 5, 0, 0).setId(23).setPressedColor(Color.RED))
        );


    }

    private void updatePages() throws BandIOException {
        client.getTileManager().setPages(tileId,
                new PageData(pageId1, 0)
                       // .update(new FilledButtonData(12, Color.YELLOW))
                        .update(new TextButtonData(12, "Start Swing"))
                        .update(new TextButtonData(21, "End Swing"))
                        .update(new TextButtonData(22, "Start Dist"))
                        .update(new TextButtonData(23, "End Dist"))
        );

        snackbar = Snackbar
               .make(getWindow().getDecorView().getRootView(), "Send button page data to tile page", Snackbar.LENGTH_SHORT);
       snackbar.show();
    }

    //////////

    protected boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                //appendToUI("Band isn't paired with your phone.\n");
                 snackbar = Snackbar
                        .make(getWindow().getDecorView().getRootView(), "Band isn't paired with your phone.", Snackbar.LENGTH_LONG);
                snackbar.show();
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        //appendToUI("Band is connecting...\n");//////////////////////////////////////////////////////////////////////////////////////////
         snackbar = Snackbar
               .make(getWindow().getDecorView().getRootView(), "Band is connecting", Snackbar.LENGTH_SHORT);
        snackbar.show();










        return ConnectionState.CONNECTED == client.connect().await();
    }

    protected void handleBandException(BandException e) {
        String exceptionMessage = "";
        switch (e.getErrorType()) {
            case DEVICE_ERROR:
                exceptionMessage = "Please make sure bluetooth is on and the band is in range.\n";
                break;
            case UNSUPPORTED_SDK_VERSION_ERROR:
                exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                break;
            case SERVICE_ERROR:
                exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                break;
            case BAND_FULL_ERROR:
                exceptionMessage = "Band is full. Please use Microsoft Health to remove a tile.\n";
                break;
            default:
                exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                break;
        }
        appendToUI(exceptionMessage);
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        Intent intent;
        switch (position) {
            case 0:
             intent = new Intent(this, ShotDataDisplay.class);
                startActivity(intent);

                break;
            case 1:
                 intent = new Intent(this, ShotAverages.class);
                startActivity(intent);
            case 2:
//                fragment = new MessagesFragment();
//                title = getString(R.string.title_messages);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }



}
