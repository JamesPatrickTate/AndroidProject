package com.example.james.materialdesign2;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by james on 09/03/2018.
 */

public class ShotAnalysisTest {
    public ShotAnalysis shotAnalysisLong;
    public ShotAnalysis shotAnalysisShort;
    public ShotAnalysis shotAnalysisVeryShort;
    @Before
    public void intialize(){

        shotAnalysisLong = new ShotAnalysis(20, 14);
        shotAnalysisShort = new ShotAnalysis(20, 24);
        shotAnalysisVeryShort = new ShotAnalysis(20, 35);

    }

    @Test
    public void testShotQualityGreat() {
        org.junit.Assert.assertEquals(shotAnalysisLong.getShotQuality(), "Great");
    }
    @Test
    public void testShotQualityAverage() {
        org.junit.Assert.assertEquals(shotAnalysisShort.getShotQuality(), "Average");
    }

    @Test
    public void testShotQualityAwful() {
        org.junit.Assert.assertEquals(shotAnalysisVeryShort.getShotQuality(), "Awful");
    }


    @Test
    public void testdifferenceInWristSpeed(){
        org.junit.Assert.assertEquals(shotAnalysisVeryShort.
                differenceInWristSpeed(.233,4), -3.767, 0);
    }
    @Test
    public void differenceHeartrate(){
        org.junit.Assert.assertEquals(shotAnalysisVeryShort.
                differenceHeartrate(5,5.5), -0.5, 2);
    }
    @Test
    public void testdifferenceInSkinTemp(){
        org.junit.Assert.assertEquals(shotAnalysisVeryShort.
                differenceInSkinTemp(4.233,4), .233, 2);
    }
    @Test
    public void testdifferenceGSR(){
        org.junit.Assert.assertEquals(shotAnalysisVeryShort.
                differenceGSR(4.233,6), -2.233, 2);
    }


}
