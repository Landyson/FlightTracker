package org.example;

import java.util.ArrayList;

public class CountryVector {

    private ArrayList<double[][]> coordinates;
    private double[] geoPoint;
    private String name;
    private String continent;

    public CountryVector(ArrayList<double[][]> coordinates, double[] geoPoint, String name, String continent){
        this.coordinates = coordinates;
        this.geoPoint = geoPoint;
        this.name = name;
        this.continent = continent;
    }

    public ArrayList<double[][]> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<double[][]> coordinates) {
        this.coordinates = coordinates;
    }

    public double[] getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(double[] geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }
}
