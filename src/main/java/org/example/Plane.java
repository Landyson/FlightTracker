package org.example;

import java.util.Arrays;

public class Plane {

    private String callSign;
    private double[] coordinates;
    private double altitude;
    private String altitudeATC;
    private double velocity;
    private double heading;

    public static final double M_TO_KNOTS = 1.944;
    public static final double M_TO_FT = 3.2808399;
    public static final double EARTH_RADIUS_M = 6366707.0195;

    public Plane(String callSign, double[] coordinates, double altitude, double velocity, double heading) {
        setCallSign(callSign);
        this.coordinates = coordinates;
        setAltitude(altitude);
        setAltitudeATC(altitude);
        setVelocity(velocity);
        this.heading = heading;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        if (callSign.equalsIgnoreCase("")) this.callSign = "N/A";

        else this.callSign = callSign;
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
        if (altitude <= 10) this.altitude = 0;
        else this.altitude = altitude * M_TO_FT;
    }

    public String getAltitudeATC() {
        return altitudeATC;
    }

    public void setAltitudeATC(double altitude) {
        if (altitude <= 0) this.altitudeATC = "LANDED";
        else {
            int t10k = (int) Math.floor((altitude * M_TO_FT) / 10000.0);
            int t1k = (int) Math.floor((altitude * M_TO_FT) / 1000.0) - t10k * 10;
            int t100 = (int) Math.floor((altitude * M_TO_FT) / 100.0) - t10k * 100 - t1k * 10;
            this.altitudeATC = t10k + String.valueOf(t1k) + t100;
        }
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
