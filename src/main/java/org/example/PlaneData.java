package org.example;

import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import java.awt.*;
import java.util.ArrayList;

public class PlaneData {

    ArrayList<Plane> planes = new ArrayList<>();

    public PlaneData(double[] box) throws Exception{
        try {
            load(box);
        }
        catch (Exception e){
            throw e;
        }

    }

    public void load(double[] box) throws Exception{
        try {
            OpenSkyApi api = new OpenSkyApi();

            OpenSkyStates os = api.getStates(0, null,
                    new OpenSkyApi.BoundingBox(36.4431, 60.1878, -16.8008, 46.1727));
                    //new OpenSkyApi.BoundingBox(box[3], box[1], box[0], box[2]));

            for(StateVector vector : os.getStates()){
                String callSign = vector.getCallsign();
                double[] coordinates = new double[]{vector.getLongitude(), vector.getLatitude()};
                double altitude;
                try {
                    altitude = vector.getGeoAltitude();
                }
                catch (Exception e){
                    altitude = Double.MIN_VALUE;
                }
                double velocity;
                try {
                    velocity = vector.getVelocity();
                }
                catch (Exception e){
                    velocity = Double.MIN_VALUE;
                }
                double heading;
                try {
                    heading = vector.getHeading();
                }
                catch (Exception e){
                    heading = Double.MIN_VALUE;
                }
                planes.add(new Plane(callSign, coordinates, altitude, velocity, heading));
            }
        }
        catch (Exception e){
            throw new Exception("Couldn't update.");
        }
    }

}
