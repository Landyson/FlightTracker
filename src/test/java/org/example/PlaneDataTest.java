package org.example;

import org.junit.jupiter.api.Test;
import org.opensky.api.OpenSkyApi;
import org.opensky.model.StateVector;
import static org.junit.jupiter.api.Assertions.*;



class PlaneDataTest {

    OpenSkyApi api = new OpenSkyApi("Landys", "15NitraM06");

    private StateVector mockStateVector;

    @org.junit.jupiter.api.Test
    void load() {
    }

    @org.junit.jupiter.api.Test
    void randomGeneratePlanes() {
        PlaneData planeData = new PlaneData(api, true); // Use the constructor that generates random plane data


         int planes = 2000;

        // Verify that the correct number of planes are generated
        assertEquals(planes, planeData.planes.size());

        // Verify properties of each plane
        for (Plane plane : planeData.planes) {
            assertNotNull(plane.getCallSign());
            assertEquals(2, plane.getCoordinates().length);
            assertTrue(plane.getCoordinates()[0] >= -180 && plane.getCoordinates()[0] <= 180);
            assertTrue(plane.getCoordinates()[1] >= -20 && plane.getCoordinates()[1] <= 80);
            assertTrue(plane.getAltitude() >= 500 && plane.getAltitude() <= 40000);
            assertNotNull(plane.getAltitudeATC());
            assertTrue(plane.getVelocity() >= 120 && plane.getVelocity() <= 500);
            assertTrue(plane.getHeading() >= 0 && plane.getHeading() < 360);
        }
    }

    @Test
    void generateCallsign() {
        String text = PlaneData.generateCallsign();
        assertTrue(text.matches("\\w{2}\\d{4}"));
    }
}