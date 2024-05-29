package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CountryDataTest {


    @Test
    void stringToCoordinates() {
        CountryData countryData = new CountryData();

        String data3 = "\"{\"\"coordinates\"\": [[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.a], [102.0]]], \"\"type\"\": \"\"Polygon\"\"}\"";
        assertThrows(NumberFormatException.class, () -> {
            countryData.stringToCoordinates(data3);
        });
    }

    @Test
    void nameToCountryVector() {
        CountryVector ca = new CountryVector(null,new double[0],"Uganda","Africa");
        ArrayList<CountryVector> ar = new ArrayList<>();
        ar.add(ca);

        CountryData cd = new CountryData();
        cd.setCountryVectors(ar);

        assertEquals(cd.nameToCountryVector("Uganda"), ca);
    }
}