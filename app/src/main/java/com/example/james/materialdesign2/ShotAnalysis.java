package com.example.james.materialdesign2;

import android.util.Log;

/**
 * Created by james on 09/03/2018.
 *  This class will take in shot data a analyse the quality of the shot. It will also try to diagnose
 *  issues with the shot from recorded data
 */

public class ShotAnalysis {
    private double currentShotDistance, averageDistanceforSuchShot, differenceBetweenCurrentAndAverage;
    private String shotQuality;
    String TAG = "ShotAnalysis";


    public  ShotAnalysis(double currentShotDistance,
                         double averageDistanceforSuchShot) {

        this.currentShotDistance = currentShotDistance;
        this.averageDistanceforSuchShot = averageDistanceforSuchShot;
       // Log.d(TAG, "params: "+currentShotDistance +" : " + averageDistanceforSuchShot );


    } // end constructor

    /**
     *  Assign a quality to a shot based on the average for the club and swing
     *  - Great is longer 5 meters
     *  - Poor is short between 5-9 meters
     *  - Awful is short over 9 meters (over a club length)
     * @return the quaity rating
     */
    public String getShotQuality() {
        differenceBetweenCurrentAndAverage = currentShotDistance - averageDistanceforSuchShot;
     //   Log.d(TAG, "Difference: "+ differenceBetweenCurrentAndAverage);
        if(differenceBetweenCurrentAndAverage > 5) {
            shotQuality = "ABOVE AVERAGE";
        }else if(differenceBetweenCurrentAndAverage < -5){
            shotQuality = "BELOW AVERAGE";
        }else {
            shotQuality = "AVERAGE";
        }
        //Log.d(TAG, "Shot Quality: "+ shotQuality);
        return shotQuality;
    }

    /**
     *  return difference
     * @param currentWristSpeed
     * @param averageWristSpeed
     * @return
     */

    public double differenceInWristSpeed(double currentWristSpeed, double averageWristSpeed ) {
        return currentWristSpeed - averageWristSpeed;
    }
    public double differenceInSkinTemp(double currentSkinTemp, double averageSkinTemp ) {
        return currentSkinTemp - averageSkinTemp;
    }
    public double  differenceHeartrate(int heartrate , double averageHr) {
        return ((double)heartrate) - averageHr;
    }
    public double  differenceGSR(double GSR , double averageGSR) {
        return GSR - averageGSR ;
    }
    public double differenceInDistance() {
        return HelperMethods.round(currentShotDistance - averageDistanceforSuchShot, 2);
    }

} // end class
