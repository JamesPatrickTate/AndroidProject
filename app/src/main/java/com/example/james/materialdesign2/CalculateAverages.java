package com.example.james.materialdesign2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dto.ShotResultsDTO;

/**
 * Created by james on 17/11/2017.
 */

public class CalculateAverages {

    private List<ShotResultsDTO> allSHotData = new ArrayList<>();
    private final double acceptableRange = 2.0;

    /**
     *
     * @param allSHotData arraylist of all the shot data for the player
     */
    public CalculateAverages(List<ShotResultsDTO> allSHotData){
        this.allSHotData = allSHotData;
    }



    public HashMap<String, Double> results(){
        HashMap<String, Double> result = new HashMap<>();
        HashMap<String, Double> total = new HashMap<>();
        HashMap<String , Integer> counter = new HashMap<>();


        for(ShotResultsDTO d : allSHotData) {

            //club concatenated with swing length
            String key = d.getClub() + "|" + d.getSwingLength();
            Double distance = d.getShotDistance();
            //distance = round(distance, 2);

            if (distance != 0.0) {

                if (!total.containsKey(key)) {
                    total.put(key, distance);
                    counter.put(key, 1);
                } else {

                    total.put(key, total.get(key) + distance);
                    counter.put(key, counter.get(key) + 1);
                }
        }else{
                System.out.println("Distance of zero will not contribute to average");
            }



        }

        for (String key:  total.keySet()) {
               Double avg = total.get(key)/counter.get(key);
               avg = round(avg, 2);
               result.put(key,avg);
            
        }

        for (String s: result.keySet()) {
            System.out.println(s+":"+result.get(s));

        }

        return  result;
    }


    /**
     * returns array with values for club suggestion
     */

    public HashMap<String, Double> suggestionResults(double distanceUserWantsToCover){
       HashMap<String, Double> result = new HashMap<>();
       HashMap<String, Double> averages = results();
       Double minAcceptableSuggestion = distanceUserWantsToCover - acceptableRange;
       Double maxAcceptableSuggestion =  distanceUserWantsToCover + acceptableRange;

        for (Map.Entry<String, Double> entry : averages.entrySet()) {
            String key = entry.getKey();
            System.out.println("key: "+ key);
            Double value = entry.getValue();
            System.out.println("val: "+ value);

            if(value > minAcceptableSuggestion && value < maxAcceptableSuggestion) {
                result.put(key,value);
            }//end if
        }//end for

        return  result;


//        HashMap<String, Double> total = new HashMap<>();
//        HashMap<String , Integer> counter = new HashMap<>();
//
//
//        for(ShotResultsDTO d : allSHotData) {
//
//            //club concatenated with swing length
//            String key = d.getClub() + "|" + d.getSwingLength();
//            Double distance = d.getShotDistance();
//
//            // The max and min values sets the acceptable range for club suggestion
//            // we add and subtract the acceptable range variable from the given distance
//            // for a club suggestion based on the range
//            Double minAcceptableSuggestion = distanceUserWantsToCover - acceptableRange;
//            Double maxAcceptableSuggestion =  distanceUserWantsToCover + acceptableRange;
//
//            if (distance > minAcceptableSuggestion && distance < maxAcceptableSuggestion) {
//
//                if (!total.containsKey(key)) {
//                    total.put(key, distance);
//                    counter.put(key, 1);
//                } else {
//
//                    total.put(key, total.get(key) + distance);
//                    counter.put(key, counter.get(key) + 1);
//                }
//            }else{
//                System.out.println("Distance of zero will not contribute to average");
//            }
//
//
//
//        }
//
//        for (String key:  total.keySet()) {
//            Double avg = total.get(key)/counter.get(key);
//            avg = round(avg, 2);
//            result.put(key,avg);
//
//        }
//
//        for (String s: result.keySet()) {
//            System.out.println(s+":"+result.get(s));
//
//        }


    }

    /**
     * reduce the length of the distance variabole to 3 decimal places
     *
     * @param value
     * @param precision
     * @return
     */
    private static double round(double value, int precision) {
        if (precision < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(precision, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }



}



