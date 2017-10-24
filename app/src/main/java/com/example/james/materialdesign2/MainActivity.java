package com.example.james.materialdesign2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

//band imports///////////////////////////////////////////////////
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.microsoft.band.tiles.pages.FilledButton;
import com.microsoft.band.tiles.pages.FilledButtonData;
import com.microsoft.band.tiles.pages.FlowPanel;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.TextButton;
import com.microsoft.band.tiles.pages.TextButtonData;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import static android.R.attr.duration;
import static com.example.james.materialdesign2.R.styleable.CoordinatorLayout;

/////////////////////////////////////////////////////////////////

public class MainActivity extends AppCompatActivity {

    private BandClient client = null;
    private Button btnStart;
    private static final UUID tileId = UUID.fromString("cc0D508F-70A3-47D4-BBA3-812BADB1F8Aa");
    private static final UUID pageId1 = UUID.fromString("b1234567-89ab-cdef-0123-456789abcd00");
    private TextView txtStatus;
    private ScrollView scrollView;
    private Button btnStop;

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnStart = (Button) findViewById(R.id.startButton);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        scrollView = (ScrollView) findViewById(R.id.svTest);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons();
                new StartTask().execute();
            }
        });


        btnStop = (Button) findViewById(R.id.done);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtons();
                new StopTask().execute();
            }
        });



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

    private void processIntent(Intent intent){
        String extraString = intent.getStringExtra(getString(R.string.intent_key));

        if(extraString != null && extraString.equals(getString(R.string.intent_value))){
            if (intent.getAction() == TileEvent.ACTION_TILE_OPENED) {
                TileEvent tileOpenData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                appendToUI("Tile open event received\n" + tileOpenData.toString() + "\n\n");
            } else if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED) {
                TileButtonEvent buttonData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                appendToUI("Button event received\n" + buttonData.toString() + "\n\n");
            } else if (intent.getAction() == TileEvent.ACTION_TILE_CLOSED) {
                TileEvent tileCloseData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                 appendToUI("Tile close event received\n" + tileCloseData.toString() + "\n\n");
            }
        }
    }

    private class StartTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            //txtStatus.setText("");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI("Band is connected.\n");
                    if (addTile()) {
                        updatePages();
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
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

    private class StopTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            appendToUI("Stopping demo and removing Band Tile\n");
            try {
                if (getConnectedBandClient()) {
                    appendToUI("Removing Tile.\n");
                    removeTile();
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
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
                appendToUI("Stop completed.\n");
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
        appendToUI("Button Tile is adding ...\n");
        if (client.getTileManager().addTile(this, tile).await()) {
            appendToUI("Button Tile is added.\n");
            return true;
        } else {
            appendToUI("Unable to add button tile to the band.\n");
            return false;
        }
    }

    private PageLayout createButtonLayout() {
        return new PageLayout(
                new FlowPanel(15, 0, 260, 105, FlowPanelOrientation.VERTICAL)
                        .addElements(new FilledButton(0, 5, 210, 45).setMargins(0, 5, 0, 0).setId(12).setBackgroundColor(Color.RED))
                        .addElements(new TextButton(0, 0, 210, 45).setMargins(0, 5, 0, 0).setId(21).setPressedColor(Color.BLUE)));
    }

    private void updatePages() throws BandIOException {
        client.getTileManager().setPages(tileId,
                new PageData(pageId1, 0)
                        .update(new FilledButtonData(12, Color.YELLOW))
                        .update(new TextButtonData(21, "Text Button")));
       appendToUI("Send button page data to tile page \n\n");
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

        //appendToUI("Band is connecting...\n");//////////////////////////////////////////////////////////////////////////////////////////
        Snackbar snackbar = Snackbar
               .make(getWindow().getDecorView().getRootView(), "Band is connecting", Snackbar.LENGTH_SHORT);//TODO change all appendToUI to snackbar
        snackbar.show();










        return ConnectionState.CONNECTED == client.connect().await();
    }

    private void handleBandException(BandException e) {
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



}
