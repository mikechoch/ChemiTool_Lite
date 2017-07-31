package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 4/10/17.
 */

class MyBounceInterpolator implements android.view.animation.Interpolator {
    double mAmplitude = 1;
    double mFrequency = 10;


    // variables in equation for bounce
    // amplitude is power of bounce
    // frequency is how much bounce
    MyBounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }


    // equation to create bouncing animation
    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) * Math.cos(mFrequency * time) + 1);
    }
}
