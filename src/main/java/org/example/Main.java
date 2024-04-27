package org.example;

import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        /*OpenSkyApi api = new OpenSkyApi("Landys","15NitraM06");

        OpenSkyStates os = api.getStates(0, null,
                new OpenSkyApi.BoundingBox(48.5553, 51.1172,12.2401,18.8531));
                new OpenSkyApi.BoundingBox(36.4431, 60.1878, -16.8008, 46.1727));


        for(StateVector vector : os.getStates()){
            System.out.println("letadlo " + vector.getCallsign() + " " + vector.getLatitude() + " " + vector.getLongitude() + " " + vector.getGeoAltitude() + " " + vector.getVelocity()*3.6 + " " + vector.getHeading() + " " + vector.getIcao24());
        }*/

        new Window();

    }
}