package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CityDataTest {

    @Test
    void load() {
        String testFilePath = "lib/worldcities.csv";

        // Create a CityData instance
        CityData cityData = new CityData(testFilePath, 100000);

        // Call the load method
        cityData.load(testFilePath);

        // Get the list of cities
        ArrayList<City> cities = cityData.getCities();

        // Verify that the cities list is not empty
        assertFalse(cities.isEmpty());

        // Verify that cities are added based on the population cutoff
        for (City city : cities) {
            assertTrue(city.getPopulation() >= 100000); // Assuming the population cutoff is 100,000
        }
    }
}