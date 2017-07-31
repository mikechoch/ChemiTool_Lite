package com.choch.michaeldicioccio.chemitool_lite;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by michaeldicioccio on 6/21/17.
 */

public class ElementStudyReport implements Serializable {
    //-----------------------------------------------------
    // ElementStudyReport Attributes
    //-----------------------------------------------------
    boolean correct;

    Integer attempts;
    Integer elementAtomicNumber;

    String elementSymbol;
    String elementName;


    //-----------------------------------------------------
    // ElementStudyReport Constructor
    //-----------------------------------------------------
    public ElementStudyReport(Integer atomicNumber, String symbol, String name) {
        elementAtomicNumber = atomicNumber;
        elementSymbol = symbol;
        elementName = name;
        attempts = 0;
        correct = false;
    }


    //-----------------------------------------------------
    // ElementStudyReport Accessors
    //-----------------------------------------------------
    public boolean isCorrect() {
        return correct;
    }

    public int getAttempts() {
        return attempts;
    }

    //-----------------------------------------------------
    // ElementStudyReport Mutators
    //-----------------------------------------------------
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public void addAttempt() {
        if (attempts < 3) {
            this.attempts++;
        }
    }

    public Integer getElementAtomicNumber() {
        return elementAtomicNumber;
    }

    public String getElementSymbol() {
        return elementSymbol;
    }

    public String getElementName() {
        return elementName;
    }
}
