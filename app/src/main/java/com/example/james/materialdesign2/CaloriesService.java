package com.example.james.materialdesign2;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;

import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;

/**
 * Created by james on 18/02/2018.
 * This class takes stress readings. it records both heartrate  and sking temp
 */

public class CaloriesService extends  Service{


    private BandClient client = null;


    public static final String
            MESSAGE_FROM_SERVICE = StressMeasurementService.class.getName() + "HeartRateService",
            MESSAGE = "string";
    private String string = "";



    private BandCaloriesEventListener bandCaloriesEventListener = new BandCaloriesEventListener() {
        @Override
        public void onBandCaloriesChanged(BandCaloriesEvent bandCaloriesEvent) {
           MainActivity.currentCals =  bandCaloriesEvent.getCalories();

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }
    //final  = new WeakReference<Activity>(this);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new CaloriesSubscriptionTask().execute();
        Toast.makeText(this, "Calorie service has begun", Toast.LENGTH_LONG).show();
        return  START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
            super.onDestroy();
        }
        Toast.makeText(this, "Calorie Service has stopped", Toast.LENGTH_LONG).show();
    }

    //we want a started service this is only here as it must be over ridden.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }//end bind

    // MS Band Related methods



    private class CaloriesSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        appendToUI("Band is connected.\n");
                        client.getSensorManager().registerCaloriesEventListener(bandCaloriesEventListener);
                    } else {
                        appendToUI("The calories sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
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

    private void appendToUI(final String string) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                txtStatus.setText(string);
//            }
//        });
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

        appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

}