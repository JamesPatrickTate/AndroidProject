package com.example.james.materialdesign2;
import java.util.ArrayList;

import dto.AccelerationEventCordinatesAndTime;

/**
 * Calculate the maximum swing speed using accelerometer readings
 * @param accelerometerValues - ArrayList of accelerometer values taken during the last swing
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
                AccelerationEventCordinatesAndTime currentDataPoint = new AccelerationEventCordinatesAndTime();
                AccelerationEventCordinatesAndTime futureDataPoint = new AccelerationEventCordinatesAndTime();
                currentDataPoint = accelerometerValues.get(i-1);
                futureDataPoint = accelerometerValues.get(i);

                        timeTaken = (futureDataPoint.getTime() - currentDataPoint.getTime()) / 1000;

                deltaXAcceleration = futureDataPoint.getX() - currentDataPoint.getX();
                deltaYAcceleration = futureDataPoint.getY() - currentDataPoint.getY();
                deltaZAcceleration = futureDataPoint.getZ() - futureDataPoint.getZ();

                xVelocity = initialXVelocity + deltaXAcceleration*timeTaken;
                yVelocity = initialYVelocity + deltaYAcceleration*timeTaken;
                zVelocity = initialZVelocity + deltaZAcceleration*timeTaken;

                currentVelocity = Math.sqrt(Math.pow(xVelocity,2) + Math.pow(yVelocity,2) + Math.pow(zVelocity,2));



                if (currentVelocity > maxVelocity) {
                    maxVelocity = currentVelocity;
                }
            }

            return maxVelocity;
        }
    }


//}
