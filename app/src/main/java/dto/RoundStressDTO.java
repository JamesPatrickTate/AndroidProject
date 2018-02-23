package dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 22/02/2018.
 */

public class RoundStressDTO {
    private List<Integer> heartRateList = new ArrayList<>();
    private List<Float> skinTemp = new ArrayList<>();
    private List<Double> gsrList = new ArrayList<>();
    private List<Long> gsrTimes = new ArrayList<>();
    private List<Long> HRtimes = new ArrayList<>();
    private List<Long> skinTimes = new ArrayList<>();
    private String day;
    private String email;

    public List<Integer> getHeartRateList() {
        return heartRateList;
    }

    public void setHeartRateList(List<Integer> heartRateList) {
        this.heartRateList = heartRateList;
    }

    public List<Float> getSkinTemp() {
        return skinTemp;
    }

    public void setSkinTemp(List<Float> skinTemp) {
        this.skinTemp = skinTemp;
    }

    public List<Double> getGsrList() {
        return gsrList;
    }

    public void setGsrList(List<Double> gsrList) {
        this.gsrList = gsrList;
    }

    public List<Long> getGsrTimes() {
        return gsrTimes;
    }

    public void setGsrTimes(List<Long> gsrTimes) {
        this.gsrTimes = gsrTimes;
    }

    public List<Long> getHRtimes() {
        return HRtimes;
    }

    public void setHRtimes(List<Long> HRtimes) {
        this.HRtimes = HRtimes;
    }

    public List<Long> getSkinTimes() {
        return skinTimes;
    }

    @Override
    public String toString() {
        return "RoundStressDTO{" +
                "heartRateList=" + heartRateList +
                ", skinTemp=" + skinTemp +
                ", gsrList=" + gsrList +
                ", gsrTimes=" + gsrTimes +
                ", HRtimes=" + HRtimes +
                ", skinTimes=" + skinTimes +
                ", day='" + day + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public void setSkinTimes(List<Long> skinTimes) {
        this.skinTimes = skinTimes;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
