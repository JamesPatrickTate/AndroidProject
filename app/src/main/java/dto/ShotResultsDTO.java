package dto;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by james on 28/10/2017.
 *
 * Data Transfer Object for the NewShot activity
 * see https://www.youtube.com/watch?v=ZLyJLGkcvFM
 */

public class ShotResultsDTO {

    private String club;
    private String swingLength;
    private  String userID;
    private double shotDistance;
    private double shotVelocity;
    private double StartLatitude;
    private double StartLongitude;
    private double endLatitude;
    private double endLongitude;

    public double getShotDistance() {
        return shotDistance;
    }

    public void setShotDistance(double shotDistance) {
        this.shotDistance = shotDistance;
    }

    public double getShotVelocity() {
        return shotVelocity;
    }

    public void setShotVelocity(double shotVelocity) {
        this.shotVelocity = shotVelocity;
    }

    public double getStartLatitude() {
        return StartLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        StartLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return StartLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        StartLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getSwingLength() {
        return swingLength;
    }

    public void setSwingLength(String swingLength) {
        this.swingLength = swingLength;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID (String userID){
        this.userID = userID;
    }



    public String toString() {
        return club+":"+swingLength+":"+userID;

    }





}
