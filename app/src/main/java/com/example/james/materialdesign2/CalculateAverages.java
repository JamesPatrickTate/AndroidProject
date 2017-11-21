package com.example.james.materialdesign2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dto.ShotResultsDTO;

/**
 * Created by james on 17/11/2017.
 */

public class CalculateAverages {

    private List<ShotResultsDTO> allSHotData = new ArrayList<>();

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
     * reduce the length of the distnce variabole to 3 decimal places
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


//                clubs.add("D");
//                clubs.add("3W");
//                clubs.add("5I");
//                clubs.add("6I");
//                clubs.add("7I");
//                clubs.add("8I");
//                clubs.add("9I");
//                clubs.add("SW");
//
//                shots.add("Full");
//                shots.add("2/3");
//                shots.add("1/2");
//                shots.add("1/4");

