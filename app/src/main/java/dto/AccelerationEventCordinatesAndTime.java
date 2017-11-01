package dto;

/**
 *  saves vector coordinates and time of accelerometer event
 */

public class AccelerationEventCordinatesAndTime {

    private double x;
    private double y;
    private double z;
    private long time;


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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String toString() {
        return x+":"+y+":"+z+":"+time;

    }


}