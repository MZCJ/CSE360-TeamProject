package com.asu.sundevil;

public class PriceSuggestionEngine {
    private static final double MAX_LIFE = 365.0;
    public double calculate(double orig, double cond, double days) {
        double f = Math.max(0.0, 1.0 - (days / MAX_LIFE));
        return orig * cond * f;
    }
}
