package org.example;

import java.util.Arrays;

public class Plane {

    private String callSign;
    private double[] coordinates;
    private double altitude;
    private double velocity;
    private double heading;

    public Plane(String callSign, double[] coordinates, double altitude, double velocity, double heading) {
        this.callSign = callSign;
        this.coordinates = coordinates;
        this.altitude = altitude;
        this.velocity = velocity;
        this.heading = heading;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    @Override
    public String toString() {
        return "Plane{" + "callSign='" + callSign + '\'' + ", coordinates=" + Arrays.toString(coordinates) + ", altitude=" + altitude + ", velocity=" + velocity + ", heading=" + heading + '}';
    }
}
