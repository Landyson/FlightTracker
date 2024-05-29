package org.example;

import java.util.Arrays;

public class Plane {

    private String callSign;
    private double[] coordinates;
    private double altitude;
    private String altitudeATC;
    private double velocity;
    private double heading;

    /**
     * Conversion factor from meters per second to knots.
     */
    public static final double M_TO_KNOTS = 1.944;

    /**
     * Conversion factor from meters to feet.
     */
    public static final double M_TO_FT = 3.2808399;

    /**
     * The radius of the Earth in meters.
     */
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

    /**
     * Sets the plane's call sign.
     * @param callSign the plane's call sign
     */
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

    /**
     * Sets the plane's altitude to 0ft if plane is lower than 10ft.
     * @param altitude is same
     */
    public void setAltitude(double altitude) {
        if (altitude <= 10) this.altitude = 0;
        else this.altitude = altitude * M_TO_FT;
    }

    public String getAltitudeATC() {
        return altitudeATC;
    }

    /**
     * Sets the plane's altitude in ATC format.
     * @param altitude the plane's altitude in meters
     */
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

    /**
     * Sets the plane's velocity to 1000 if it's bigger than that.
     * @param velocity the plane's velocity in meters per second
     */
    public void setVelocity(double velocity) {
        if (velocity >= 1000) this.velocity = 1000;
        else this.velocity = velocity;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    /**
     * Animates the plane's movement based on its velocity and heading.
     * @param millis the number of milliseconds to animate
     */
    public void animate(int millis){
        double currentVelocity = this.velocity * (millis / 1000.0) / (EARTH_RADIUS_M / 400);
        double ang = heading + 90;
        this.coordinates[0] -= Math.cos(Math.toRadians(ang)) * currentVelocity;
        this.coordinates[1] += Math.sin(Math.toRadians(ang)) * currentVelocity;
    }

    @Override
    public String toString() {
        return "Plane{" + "callSign='" + callSign + '\'' + ", coordinates=" + Arrays.toString(coordinates) + ", altitude=" + altitude + ", velocity=" + velocity + ", heading=" + heading + '}';
    }
}
