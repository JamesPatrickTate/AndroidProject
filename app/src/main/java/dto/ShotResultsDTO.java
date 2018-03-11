package dto;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

/**
 * Created by james on 28/10/2017.
 *
 * Data Transfer Object for the NewShot activity
 * see https://www.youtube.com/watch?v=ZLyJLGkcvFM
 */

public class ShotResultsDTO implements Serializable {

    private String club;
    private String swingLength;
    private  String userID;
    private  String golfCourseAddress;
    private double shotDistance;
    private double shotVelocity;
    private double StartLatitude;
    private double StartLongitude;
    private double endLatitude;
    private double endLongitude;
    private String email;
    private double gsr;
    private String day;
    private int heartRatePreShot;
    private double skinTemp;
    private double averageSkinTemp;
    private double averageHeartRate;
    private double averageGSR;
    private double averageWristSpeed;
    private double averageDistance;
    private String shotQuality;
    private double differenceSkinTemp;
    private double differenceHeartRate;
    private double differenceGSR;
    private double differenceWristSpeed;
    private double differenceDistance;

    public double getDifferenceSkinTemp() {
        return differenceSkinTemp;
    }

    public void setDifferenceSkinTemp(double differenceSkinTemp) {
        this.differenceSkinTemp = differenceSkinTemp;
    }

    public double getDifferenceHeartRate() {
        return differenceHeartRate;
    }

    public void setDifferenceHeartRate(double differenceHeartRate) {
        this.differenceHeartRate = differenceHeartRate;
    }

    public double getDifferenceGSR() {
        return differenceGSR;
    }

    public void setDifferenceGSR(double differenceGSR) {
        this.differenceGSR = differenceGSR;
    }

    public double getDifferenceWristSpeed() {
        return differenceWristSpeed;
    }

    public void setDifferenceWristSpeed(double differenceWristSpeed) {
        this.differenceWristSpeed = differenceWristSpeed;
    }

    public double getDifferenceDistance() {
        return differenceDistance;
    }

    public void setDifferenceDistance(double differenceDistance) {
        this.differenceDistance = differenceDistance;
    }

    public String getShotQuality() {
        return shotQuality;
    }

    public void setShotQuality(String shotQuality) {
        this.shotQuality = shotQuality;
    }






    public double getAverageWristSpeed() {
        return averageWristSpeed;
    }

    public void setAverageWristSpeed(double averageWristSpeed) {
        this.averageWristSpeed = averageWristSpeed;
    }

    public double getAverageDistance() {
        return averageDistance;
    }

    public void setAverageDistance(double averageDistance) {
        this.averageDistance = averageDistance;
    }

    public double getAverageSkinTemp() {
        return averageSkinTemp;
    }

    public void setAverageSkinTemp(double averageSkinTemp) {
        this.averageSkinTemp = averageSkinTemp;
    }

    public double getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(double averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public double getAverageGSR() {
        return averageGSR;
    }

    public void setAverageGSR(double averageGSR) {
        this.averageGSR = averageGSR;
    }

    public double getSkinTemp() {
        return skinTemp;
    }

    public void setSkinTemp(double skinTemp) {
        this.skinTemp = skinTemp;
    }




    public int getHeartRatePreShot() {
        return heartRatePreShot;
    }

    public void setHeartRatePreShot(int heartRatePreShot) {
        this.heartRatePreShot = heartRatePreShot;
    }


    public ShotResultsDTO(){
        //empty constructor
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day= day;
    }

    public double getGsr() {
        return gsr;
    }

    public void setGsr(double gsr) {
        this.gsr = gsr;
    }

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

    public String getgolfCourseAddress() {
        return golfCourseAddress;
    }

    public void setgolfCourseAddress (String golfCourseAddress){
        this.golfCourseAddress = golfCourseAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    @Override
    public String toString() {
        return   "Club:     "+club+"\n"
                +"Swing:    "+swingLength+"\n"
                +"Address: "+golfCourseAddress+" \n"
                +"Shot Quality: " + shotQuality+"\n"
                +"Current value follwed by difference with average: \n"
                +"Velocity: "+shotVelocity+"m/s : "+differenceWristSpeed +"\n"
                +"Distance: "+shotDistance+" : "+differenceDistance+"\n"
                +"Stress Indcators: \n" +
                "GSR: "+gsr+" : "+differenceGSR +"\n" +
                "Heart Rate: " + heartRatePreShot+" : "+differenceHeartRate +"\n" +
                "Skin Temperature: "+ skinTemp +" : "+differenceSkinTemp +"\n" ;
    }





}
