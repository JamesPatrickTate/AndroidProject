package com.example.james.materialdesign2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.lang.ref.WeakReference;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

import android.app.Activity;
import android.os.AsyncTask;


/**
 * This is a started service. Which records three stressors continually until stopped.
 */

public class StressMeasurementService extends Service
{
    private BandClient client = null;
    double t = 0.0;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    double skinTemptodisplay = 0.0;

    /**
     * Listener which updatates the gsr list every time there is a change in GSR.
     * The gsrList is a static list from the main activity and saved to the main activity.
     * This is then saved to the dto
     */

    private BandGsrEventListener mGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(final BandGsrEvent event) {
            if (event != null) {
                //appendToUI(String.format("Resistance = %d kOhms\n", event.getResistance()));
                NewShot.gsrPreShot = event.getResistance();
                t = event.getResistance()/100;

                MainActivity.gsrList.add(t);
                MainActivity.gsrTimes.add(event.getTimestamp());
            }
        }
    };

    /**
     * Listener which updatates the skin temp list every time there is a change in skin temp.
     * The skin temp List is a static list from the main activity and saved to the main activity.
     * This is then saved to the dto
     */

    private BandSkinTemperatureEventListener bandSkinTemperatureEventListener = new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent bandSkinTemperatureEvent) {
            if(bandSkinTemperatureEvent != null) {
                NewShot.skinTempPreShot = bandSkinTemperatureEvent.getTemperature();
                MainActivity.skinTemp.add(bandSkinTemperatureEvent.getTemperature());
                MainActivity.skinTimes.add(bandSkinTemperatureEvent.getTimestamp());
                skinTemptodisplay = bandSkinTemperatureEvent.getTemperature();
                // Log.i(TAG, "onBandSkinTemperatureChanged: " + bandSkinTemperatureEvent.getTemperature());
                //Log.d(TAG, "onBandSkinTemperatureChanged: " + bandSkinTemperatureEvent.getTemperature());

            }
        }
    };


    ///////////

    String TAG= "HEART";



    public static final String
            MESSAGE_FROM_SERVICE = StressMeasurementService.class.getName() + "HeartRateService",
            MESSAGE = "string";
    private String string = "";

    /**
     * Listener which updatates the heart rate list every time there is a change in heart rate.
     * The heart rate List is a static list from the main activity and saved to the main activity.
     * This is then saved to the dto
     */


    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
                appendToUI(String.format("Heart Rate = %d beats per minute\n"
                        + "Skin Temperature = "+ HelperMethods.round(skinTemptodisplay, 2)+" Celsius \n"
                        +"GSR =  "+ t+" kOhms", event.getHeartRate(), event.getQuality()));
                MainActivity.heartRateList.add(event.getHeartRate());
                MainActivity.HRtimes.add(event.getTimestamp());
                NewShot.heartRatePreShot = event.getHeartRate();

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
         wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
    }
    //final  = new WeakReference<Activity>(this);

    /**
     * Start the service and turn on the lsiteners
     * @param intent
     * @param flags
     * @param startId
     * @return
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        // turn on cpu wake lock
        wl.acquire();

        new HeartRateSubscriptionTask().execute();
        //Toast.makeText(this, "Service has begun", Toast.LENGTH_LONG).show();

        new SkinTemperatureSubscriptionTask().execute();

        new GsrSubscriptionTask().execute();
        return  START_STICKY;
    }

    /**
     * Stop the listeners.
     */

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
            // turn off wake lock
            wl.release();
            super.onDestroy();
        }
       // Toast.makeText(this, "Service has stopped", Toast.LENGTH_LONG).show();
    }

    //we want a started service this is only here as it must be over ridden.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }//end bind


    /**
     *  MS Band Related methods
     *
     *  These are standard methods required for using the band sensors.
     *
      */


    private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                    } else {
                        appendToUI("You have not given this application consent to access heart rate data yet."
                                + " Please press the Heart Rate Consent button.\n");
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

    private void appendToUI(final String string) {
       Log.d(TAG, "onConnected");
        Intent intent = new Intent(MESSAGE_FROM_SERVICE);
        intent.putExtra(MESSAGE, string);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //Toast.makeText(this, string, Toast.LENGTH_LONG).show();

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

    private class SkinTemperatureSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        appendToUI("Band is connected.\n");
                        client.getSensorManager().registerSkinTemperatureEventListener(bandSkinTemperatureEventListener);
                    } else {
                        appendToUI("The Skin Temp sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
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

    ///////

    /**
     * Gsr subscription
     */
    private class GsrSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        appendToUI("Band is connected.\n");
                        client.getSensorManager().registerGsrEventListener(mGsrEventListener);
                    } else {
                        appendToUI("The Gsr sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
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





}//end class
