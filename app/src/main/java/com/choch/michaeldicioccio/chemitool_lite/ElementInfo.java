package com.choch.michaeldicioccio.chemitool_lite;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by michaeldicioccio on 3/15/17.
 */

public class ElementInfo implements Serializable {

    //-----------------------------------------------------
    // ElementInfo Attributes
    //-----------------------------------------------------
    Boolean elementRadioactive;

    Integer elementAtomicNumber;
    Integer elementPeriodicLocationNumber;

    String elementName;
    String elementSymbol;
    String elementPhase;
    String elementGroup;
    String elementPeriod;
    String elementAtomicMass;
    String elementConfig;
    String elementType;
    String elementElectronegativity;
    String elementThermalConductivity;
    String elementImage;
    String elementImageFull;
    String elementDescription;
    String elementDensity;
    String elementMeltingPointK;
    String elementMeltingPointC;
    String elementMeltingPointF;
    String elementBoilingPointK;
    String elementBoilingPointC;
    String elementBoilingPointF;
    String elementAtomicRadius;
    String elementCovRadius;
    String elementVanRadius;
    String elementDiscoveryPerson;
    String elementDiscoveryYear;
    String elementCrystalStructure;
    String elementCrystalStructure2;
    String elementYoungsModulus;
    String elementShearModulus;
    String elementBulkModulus;
    String elementMohsHardness;
    String elementVickersHardness;
    String elementBrinellHardness;

    String[] elementElectronShell;


    //-----------------------------------------------------
    // ElementInfo Accessors
    //-----------------------------------------------------
    public Boolean isElementRadioactive() {
        return elementRadioactive;
    }

    public Integer getElementAtomicNumber() {
        return elementAtomicNumber;
    }

    public Integer getElementPeriodicLocationNumber() {
        return elementPeriodicLocationNumber;
    }

    public String getElementName() {
        return elementName;
    }

    public String getElementSymbol() {
        return elementSymbol;
    }

    public String getElementPhase() {
        return elementPhase;
    }

    public String getElementGroup() {
        return elementGroup;
    }

    public String getElementPeriod() {
        return elementPeriod;
    }

    public String getElementAtomicMass() {
        return elementAtomicMass;
    }

    public String getElementConfig() {
        return elementConfig;
    }

    public String getElementType() {
        return elementType;
    }

    public String getElementElectronegativity() {
        return elementElectronegativity;
    }

    public String getElementThermalConductivity() {
        return elementThermalConductivity;
    }

    public String getElementImage() {
        return elementImage;
    }

    public String getElementImageFull() {
        return elementImageFull;
    }

    public String getElementDescription() {
        return elementDescription;
    }

    public String getElementDensity() {
        return elementDensity;
    }

    public String getElementMeltingPoint(char ch) {
        switch (ch) {
            case 'K': return elementMeltingPointK;
            case 'C': return elementMeltingPointC;
            case 'F': return elementMeltingPointF;
        }
        return null;
    }

    public String getElementBoilingPoint(char ch) {
        switch (ch) {
            case 'K': return elementBoilingPointK;
            case 'C': return elementBoilingPointC;
            case 'F': return elementBoilingPointF;
        }
        return null;
    }

    public String getElementRadius(char ch) {
        switch (ch) {
            case 'A': return elementAtomicRadius;
            case 'C': return elementCovRadius;
            case 'V': return elementVanRadius;
        }
        return null;
    }

    public String getElementDiscovery(char ch) {
        switch (ch) {
            case 'P': return elementDiscoveryPerson;
            case 'Y': return elementDiscoveryYear;
        }
        return null;
    }

    public String getElementCrystalStructure(char ch) {
        switch (ch) {
            case '1': return elementCrystalStructure;
            case '2': return elementCrystalStructure2;
        }
        return null;
    }

    public String getElementModulus(char ch) {
        switch (ch) {
            case 'Y': return elementYoungsModulus;
            case 'S': return elementShearModulus;
            case 'B': return elementBulkModulus;
        }
        return null;
    }

    public String getElementHardness(char ch) {
        switch (ch) {
            case 'M': return elementMohsHardness;
            case 'V': return elementVickersHardness;
            case 'B': return elementBrinellHardness;
        }
        return null;
    }

    public String printElementElectronShell() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String shell : elementElectronShell) {
            stringBuilder.append(", " + shell);
        }
        stringBuilder.delete(0, 1);
        return stringBuilder.toString();
    }

    public String[] getElementElectronShell() {
        return elementElectronShell;
    }


    //-----------------------------------------------------
    // ElementInfo Mutators
    //-----------------------------------------------------
    public void setElementRadioactive(Boolean radioactive) {
        elementRadioactive = radioactive;
    }

    public void setElementAtomicNumber(Integer atomicNumber) {
        elementAtomicNumber = atomicNumber;
    }

    public void setElementPeriodicLocationNumber(Integer periodicLocationNumber) {
        elementPeriodicLocationNumber = periodicLocationNumber;
    }

    public void setElementName(String name) {
        elementName = name;
    }

    public void setElementSymbol(String symbol) {
        elementSymbol = symbol;
    }

    public void setElementPhase(String phase) {
        elementPhase = phase;
    }

    public void setElementGroup(String group) {
        elementGroup = group;
    }

    public void setElementPeriod(String period) {
        elementPeriod = period;
    }

    public void setElementAtomicMass(String atomicMass) {
        elementAtomicMass = atomicMass;
    }

    public void setElementConfig(String config) {
        elementConfig = config;
    }

    public void setElementType(String type) {
        elementType = type;
    }

    public void setElementElectronegativity(String electronegativity) {
        elementElectronegativity = electronegativity;
    }

    public void setElementThermalConductivity(String thermalConductivity) {
        elementThermalConductivity = thermalConductivity;
    }

    public void setElementImage(String image) {
        elementImage = image;
    }

    public void setElementImageFull(String image) {
        elementImageFull = image;
    }

    public void setElementDescription(String description) {
        elementDescription = description;
    }

    public void setElementDensity(String density) {
        elementDensity = density;
    }

    public void setElementMeltingPointK(String meltingPointK) {
        elementMeltingPointK = meltingPointK;
    }

    public void setElementMeltingPointC(String meltingPointC) {
        elementMeltingPointC = meltingPointC;
    }

    public void setElementMeltingPointF(String meltingPointF) {
        elementMeltingPointF = meltingPointF;
    }

    public void setElementBoilingPointK(String boilingPointK) {
        elementBoilingPointK = boilingPointK;
    }

    public void setElementBoilingPointC(String boilingPointC) {
        elementBoilingPointC = boilingPointC;
    }

    public void setElementBoilingPointF(String boilingPointF) {
        elementBoilingPointF = boilingPointF;
    }

    public void setElementAtomicRadius(String atomicRadius) {
        elementAtomicRadius = atomicRadius;
    }

    public void setElementCovRadius(String covRadius) {
        elementCovRadius = covRadius;
    }

    public void setElementVanRadius(String vanRadius) {
        elementVanRadius = vanRadius;
    }

    public void setElementDiscoveryPerson(String person) {
        elementDiscoveryPerson = person;
    }

    public void setElementDiscoveryYear(String year) {
        elementDiscoveryYear = year;
    }

    public void setElementCrystalStructure(String crystalStructure) {
        elementCrystalStructure = crystalStructure;
    }

    public void setElementCrystalStructure2(String crystalStructure) {
        elementCrystalStructure2 = crystalStructure;
    }

    public void setElementYoungsModulus(String youngsModulus) {
        elementYoungsModulus = youngsModulus;
    }

    public void setElementShearModulus(String shearModulus) {
        elementShearModulus = shearModulus;
    }

    public void setElementBulkModulus(String bulkModulus) {
        elementBulkModulus = bulkModulus;
    }

    public void setElementMohsHardness(String mohsHardness) {
        elementMohsHardness = mohsHardness;
    }

    public void setElementVickersHardness(String vickersHardness) {
        elementVickersHardness = vickersHardness;
    }

    public void setElementBrinellHardness(String brinellHardness) {
        elementBrinellHardness = brinellHardness;
    }

    public void setElementElectronShell(String[] electronShell) {
        elementElectronShell = electronShell;
    }

}
