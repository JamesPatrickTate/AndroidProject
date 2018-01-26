package dto;

import android.util.Log;

/**
 *  saves vector coordinates and time of accelerometer event
 */

public class AccelerationEventCordinatesAndTime {

    private double x;
    private double y;
    private double z;
    private double time;


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
       // Log.d("XXXXX", "recorded time  in cords" + time);
        this.time = time;
    }

    public String toString() {
        return x+":"+y+":"+z+":"+time;

    }


}