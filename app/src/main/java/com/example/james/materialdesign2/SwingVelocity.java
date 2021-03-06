package com.example.james.materialdesign2;
import android.util.Log;

import java.util.ArrayList;

import dto.AccelerationEventCordinatesAndTime;

/**
 * Calculate the maximum swing speed using accelerometer readings
 * @param  - ArrayList of accelerometer values taken during the last swing
 * @return double maxSwingSpeed
 * Created by james on 29/10/2017.
 */


public class SwingVelocity {





        static double getMaxAccelerometerValues(ArrayList<AccelerationEventCordinatesAndTime> accelerometerValues) {

        /*
        v = final velocity
        u = initial velocity
        a = change in acceleration
        t = time

        Formula being used:
                v = u + at
        Initial velocity taken to be zero on first calculation
        */

            double maxVelocity = 0.0;

            double currentVelocity;
            double initialXVelocity = 0;
            double initialYVelocity = 0;
            double initialZVelocity = 0;
            double xVelocity, yVelocity, zVelocity;
            double deltaXAcceleration, deltaYAcceleration, deltaZAcceleration;
            double timeTaken;

            for (int i = 1; i < accelerometerValues.size(); i++) {

               // Log.v("Acc", "values in array=" + accelerometerValues.get(i));
                AccelerationEventCordinatesAndTime currentDataPoint = new AccelerationEventCordinatesAndTime();

                AccelerationEventCordinatesAndTime futureDataPoint = new AccelerationEventCordinatesAndTime();

                currentDataPoint = accelerometerValues.get(i-1);
                futureDataPoint = accelerometerValues.get(i);
               // Log.v("Acc", "current data point" + currentDataPoint);
               // Log.v("Acc", "future" + futureDataPoint);

                        double temp = futureDataPoint.getTime() - currentDataPoint.getTime();
                       Log.v("Acc", "TEMPVAL= " + temp);

                        timeTaken = temp / 1000;
                        Log.v("Acc", "TIMETAKEN = " + timeTaken);



                deltaXAcceleration = futureDataPoint.getX() - currentDataPoint.getX();
                deltaYAcceleration = futureDataPoint.getY() - currentDataPoint.getY();
                deltaZAcceleration = futureDataPoint.getZ() - futureDataPoint.getZ();

                xVelocity = initialXVelocity + deltaXAcceleration*timeTaken;
                yVelocity = initialYVelocity + deltaYAcceleration*timeTaken;
                zVelocity = initialZVelocity + deltaZAcceleration*timeTaken;

                currentVelocity = Math.sqrt(Math.pow(xVelocity,2) + Math.pow(yVelocity,2) + Math.pow(zVelocity,2));

               // Log.v("Acc", "current velocity" + currentVelocity );




                if (currentVelocity > maxVelocity) {
                    maxVelocity = currentVelocity;
                    Log.v("Acc", "max velocity" + maxVelocity );
                }
            }

            Log.v("Acc", " final max velocity" + maxVelocity );

            return maxVelocity;
        }
    }


//        import java.util.ArrayList;
//
//class SwingSpeedCalculator {
//
//    /**
//     * Calculate the maximum swing speed using accelerometer readings
//     * @param accelerometerValues - ArrayList of accelerometer values taken during the last swing
//     * @return double maxSwingSpeed
//     */
//    static double getMaxAccelerometerValues(ArrayList<vectorCoOrdinates> accelerometerValues) {
//
//        /*
//        v = final velocity
//        u = initial velocity
//        a = change in acceleration
//        t = time
//
//        Formula being used:
//                v = u + at
//        Initial velocity taken to be zero on first calculation
//        */
//
//        double maxVelocity = 0.0;
//
//        double currentVelocity;
//        double initialXVelocity = 0;
//        double initialYVelocity = 0;
//        double initialZVelocity = 0;
//        double xVelocity, yVelocity, zVelocity;
//        double deltaXAcceleration, deltaYAcceleration, deltaZAcceleration;
//        double timeTaken;
//
//        for (int i = 1; i < accelerometerValues.size(); i++) {
//            double [] currentReadings = accelerometerValues.get(i).getCoOrdinates();
//            double [] previousReadings = accelerometerValues.get(i-1).getCoOrdinates();
//
//            timeTaken = (currentReadings[3] - previousReadings[3]) / 1000;
//
//            deltaXAcceleration = currentReadings[0] - previousReadings[0];
//            deltaYAcceleration = currentReadings[1] - previousReadings[1];
//            deltaZAcceleration = currentReadings[2] - previousReadings[2];
//
//            xVelocity = initialXVelocity + deltaXAcceleration*timeTaken;
//            yVelocity = initialYVelocity + deltaYAcceleration*timeTaken;
//            zVelocity = initialZVelocity + deltaZAcceleration*timeTaken;
//
//            currentVelocity = Math.sqrt(Math.pow(xVelocity,2) + Math.pow(yVelocity,2) + Math.pow(zVelocity,2));
//
////            initialXVelocity = xVelocity;
////            initialYVelocity = yVelocity;
////            initialZVelocity = zVelocity;
//
//            if (currentVelocity > maxVelocity) {
//                maxVelocity = currentVelocity;
//            }
//        }
//
//        return maxVelocity;
//    }
//}
////}
