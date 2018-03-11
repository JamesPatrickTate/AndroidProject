package com.example.james.materialdesign2;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A class with static methods for repetitive tasks
 * Created by james on 09/03/2018.
 */

public class HelperMethods {

    /**
     * reduce the length of the distance variable to 3 decimal places
     *
     * @param value the double to rounded
     * @param precision amount of digits after the decimal point
     * @return double with reduced precision
     */
    public static double round(double value, int precision) {
        if (precision < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(precision, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
