package org.example;

import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlaneData {

    ArrayList<Plane> planes = new ArrayList<>();
    private Random random = new Random();

    /**
     * Creates a new PlaneData object and loads plane data from the OpenSky API.
     * @throws Exception if there was an error loading the data
     */
    public PlaneData() throws Exception {
        try {
            load();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Creates a new PlaneData object and generates random plane data.
     * @param randomGenerate whether to generate random plane data
     */
    public PlaneData(boolean randomGenerate){
        randomGeneratePlanes(2000);
    }

    /**
     * Loads plane data from the OpenSky API.
     * @throws Exception if there was an error loading the data
     */
    public void load() throws Exception {
        try {
            OpenSkyApi api = new OpenSkyApi("Landys", "15NitraM06");

            OpenSkyStates os = api.getStates(0, null,
                    new OpenSkyApi.BoundingBox(1.6438, 74.6981, -132.3900, 46.6099)
            );

            for (StateVector vector : os.getStates()) {
                String callSign = vector.getCallsign();
                double[] coordinates = new double[]{vector.getLongitude(), vector.getLatitude()};
                double altitude;
                try {
                    altitude = vector.getGeoAltitude();
                } catch (Exception e) {
                    altitude = Double.MIN_VALUE;
                }
                double velocity;
                try {
                    velocity = vector.getVelocity();
                } catch (Exception e) {
                    velocity = Double.MIN_VALUE;
                }
                double heading;
                try {
                    heading = vector.getHeading();
                } catch (Exception e) {
                    heading = Double.MIN_VALUE;
                }
                planes.add(new Plane(callSign, coordinates, altitude, velocity, heading));
            }
        } catch (Exception e) {
            throw new Exception("Couldn't update.");
        }
    }

    /**
     * Generates random plane data.
     * @param planeAmount the number of planes to generate
     */
    public void randomGeneratePlanes(int planeAmount){
        for (int i = 0; i < planeAmount; i++) {
            String callsign = generateCallsign();
            double[] coordinates = new double[]{(random.nextInt(361) - 180) * random.nextDouble(), (random.nextInt(100) - 20) * (Math.pow(random.nextDouble(), 0.5))};

            double altitude = (random.nextInt(11000) + 501);
            double velocity = (random.nextInt(380) + 120);
            double heading = random.nextInt(360);

            planes.add(new Plane(callsign, coordinates, altitude, velocity, heading));
        }
    }

    /**
     * Generates a random callsign for a plane.
     * @return a random callsign
     */
    public static String generateCallsign(){
        Random random1 = new Random();
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            sb.append(abc.charAt(random1.nextInt(abc.length())));
        }
        for (int i = 0; i < 4; i++) {
            sb.append(random1.nextInt(10));
        }
        return sb.toString();
    }

}
