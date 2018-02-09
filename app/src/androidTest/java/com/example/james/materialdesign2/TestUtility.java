package com.example.james.materialdesign2;

/**
 * Created by james on 08/02/2018.
 * class to hold any helper methods which
 * may be shared among tests
 */

public class TestUtility {

    /**
     * Add a wait for page loads ect
     * @param waitingTimeInMiiliSeconds 1000ms = 1s
     */
    public void waiter(long waitingTimeInMiiliSeconds) {
        try {
            Thread.sleep(waitingTimeInMiiliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
