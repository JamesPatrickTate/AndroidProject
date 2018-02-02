package com.example.james.materialdesign2;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dto.ShotResultsDTO;

import static org.junit.Assert.*;


/**
 * Created by james on 02/02/2018.
 */

public class CalculateAveragesTest {

    private List<ShotResultsDTO> allSHotData;
    //create shot data transfer objects for testing.
    private ShotResultsDTO shotResultsDTO1 ;
    private ShotResultsDTO shotResultsDTO2 ;
    private ShotResultsDTO shotResultsDTO3;
    private ShotResultsDTO shotResultsDTO4;
    private ShotResultsDTO shotResultsDTO5;
    private ShotResultsDTO shotResultsDTO6;

    /**
     * Create an arraylist o6 shots taken with two different clubs
     * avg iron = 3.0
     * avg iron2 = 8.66667
     * with rounding mtd should be 8.67
     */

    @Before
    public void intialize() {

        allSHotData = new ArrayList<>();
        shotResultsDTO1 = new ShotResultsDTO();
        shotResultsDTO2 = new ShotResultsDTO();
        shotResultsDTO3 = new ShotResultsDTO();
        shotResultsDTO4 = new ShotResultsDTO();
        shotResultsDTO5 = new ShotResultsDTO();
        shotResultsDTO6 = new ShotResultsDTO();

        shotResultsDTO1.setClub("iron");
        shotResultsDTO1.setShotDistance(3.0);
        shotResultsDTO1.setSwingLength("1/2");

        shotResultsDTO2.setClub("iron");
        shotResultsDTO2.setShotDistance(3.0);
        shotResultsDTO2.setSwingLength("1/2");

        shotResultsDTO3.setClub("iron");
        shotResultsDTO3.setShotDistance(3.0);
        shotResultsDTO3.setSwingLength("1/2");

        shotResultsDTO4.setClub("iron2");
        shotResultsDTO4.setShotDistance(6.0);
        shotResultsDTO4.setSwingLength("1/2");

        shotResultsDTO5.setClub("iron2");
        shotResultsDTO5.setShotDistance(9.0);
        shotResultsDTO5.setSwingLength("1/2");

        shotResultsDTO6.setClub("iron2");
        shotResultsDTO6.setShotDistance(11.0);
        shotResultsDTO6.setSwingLength("1/2");

        allSHotData.add(shotResultsDTO1);
        allSHotData.add(shotResultsDTO2);
        allSHotData.add(shotResultsDTO3);
        allSHotData.add(shotResultsDTO4);
        allSHotData.add(shotResultsDTO5);
        allSHotData.add(shotResultsDTO6);


    }


    @Test
    public void testResults() {
        CalculateAverages calculateAverages = new CalculateAverages(allSHotData);
        HashMap<String, Double> average = new HashMap<>();
        average = calculateAverages.results();
        double returnedAverage = average.get("iron|1/2");
        org.junit.Assert.assertEquals(returnedAverage,3.0, 0);
    }

    @Test
    public void testSuggestionResultswithinRange() {
        //averages are 3.0 and 8.67 so value should be returned with +2 or -2 of each
        CalculateAverages calculateAverages = new CalculateAverages(allSHotData);
        HashMap<String, Double> suggestions = new HashMap<>();
        suggestions = calculateAverages.suggestionResults(10.0);
        org.junit.Assert.assertTrue(suggestions.size() == 1 && suggestions.containsKey("iron2|1/2") && !suggestions.containsKey("iron|1/2") );
    }

    @Test
    public void testSuggestionResultsBelowRange() {
        //iron2|1/2=8.67
        CalculateAverages calculateAverages = new CalculateAverages(allSHotData);
        HashMap<String, Double> suggestions = new HashMap<>();
        suggestions = calculateAverages.suggestionResults(5.0);
        org.junit.Assert.assertTrue(suggestions.isEmpty() );
    }

    @Test
    public void testSuggestionResultsAboveRange() {
        //iron2|1/2=8.67
        CalculateAverages calculateAverages = new CalculateAverages(allSHotData);
        HashMap<String, Double> suggestions = new HashMap<>();
        suggestions = calculateAverages.suggestionResults(12);
        org.junit.Assert.assertTrue(suggestions.isEmpty() );
    }



}
