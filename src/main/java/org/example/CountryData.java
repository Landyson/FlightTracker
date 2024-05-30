package org.example;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Objects;

public class CountryData {

    private ArrayList<CountryVector> countryVectors = new ArrayList<>();

    public CountryData(String path) {
        load(path);
    }

    public ArrayList<CountryVector> getCountryVectors() {
        return countryVectors;
    }

    public CountryData() {
    }

    public void setCountryVectors(ArrayList<CountryVector> countryVectors) {
        this.countryVectors = countryVectors;
    }

    /**
     * Loads country data from a file and adds country vectors to the list.
     *
     * @param path the path to the file containing country data
     */
    public void load(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            br.readLine();
            String s = "";
            while ((s = br.readLine()) != null) {
                String[] data = s.split(";");

                ArrayList<double[][]> cooridnates = stringToCoordinates(data[1]);

                String[] gSplit = data[0].split(",");
                double[] geoPoint = new double[]{Double.parseDouble(gSplit[1]), Double.parseDouble(gSplit[0])};

                String name = data[5];

                String continent = data[6];

                countryVectors.add(new CountryVector(cooridnates, geoPoint, name, continent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a string of coordinates to an ArrayList of double[][] arrays.
     *
     * @param data the string of coordinates
     * @return an ArrayList of double[][] arrays representing the coordinates
     */
    public ArrayList<double[][]> stringToCoordinates(String data) {
        data = data.replace("\"{\"\"coordinates\"\": ", "");
        data = data.replace(", \"\"type\"\": \"\"Polygon\"\"}\"", "");
        data = data.replace(", \"\"type\"\": \"\"MultiPolygon\"\"}\"", "");

        char[] chars = data.toCharArray();

        ArrayList<StringBuilder> polyStrings = new ArrayList<>();

        int polyCount = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '[' && chars[i + 1] == '[' && chars[i + 2] == '[') {
                polyStrings.add(new StringBuilder());
                for (int j = i + 1; j < chars.length; j++) {
                    if (chars[j] == ']' && chars[j + 1] == ']' && chars[j + 2] == ']') {
                        i = j + 3;
                        break;
                    }
                    polyStrings.get(polyCount).append(chars[j]);
                }
                polyCount++;
            }
        }

        ArrayList<double[][]> result = new ArrayList<>();
        for (int i = 0; i < polyStrings.size(); i++) {
            String temp = polyStrings.get(i).toString();
            temp = temp.replace("[", "");
            temp = temp.replace("]", "");
            String[] tempSplit = temp.split(",");

            double[][] tempGPS;
            tempGPS = new double[tempSplit.length / 2][2];
            for (int x = 0; x < tempSplit.length / 2; x++) {
                for (int y = 0; y < 2; y++) {
                    double num = Double.parseDouble(tempSplit[x * 2 + y]);
                    if (num != 0.0) {
                        if (y == 0) tempGPS[x][y] = wga84ToMercatorX(num);
                        else tempGPS[x][y] = wga84ToMercatorY(num);
                    }
                }
            }
            result.add(tempGPS);
        }
        return result;
    }

    /**
     * Returns the CountryVector object with the specified name.
     *
     * @param name the name of the country
     * @return the CountryVector object with the specified name
     * @throws InputMismatchException if no CountryVector object with the specified name is found
     */
    public CountryVector nameToCountryVector(String name) {
        for (CountryVector cv : countryVectors) {
            if (cv.getName().equalsIgnoreCase(name)) {
                return cv;
            }
        }
        throw new InputMismatchException();
    }

    /**
     * Returns an array of doubles representing the bounding box for the country with the specified name.
     *
     * @param name the name of the country
     * @return an array of doubles representing the bounding box for the country with the specified name, where the first element is the maximum longitude, the second element is the maximum latitude, the third element is the minimum longitude, and the fourth element is the minimum latitude
     */

    public double[] boxForCountry(String name) {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        CountryVector cv = nameToCountryVector(name);
        for (double[][] c : cv.getCoordinates()) {
            for (double[] gps : c) {
                if (gps[0] < minX) minX = gps[0];
                if (gps[0] > maxX) maxX = gps[0];
                if (gps[1] < minY) minY = gps[1];
                if (gps[1] > maxY) maxY = gps[1];
            }
        }
        //x, y, width, height
        return new double[]{maxX, maxY, minX, minY};
    }

    /**
     * Converts a longitude value from WGA84 to Mercator X.
     *
     * @param x the longitude value in WGA84
     * @return the longitude value in Mercator X
     */
    public static double wga84ToMercatorX(double x) {
        return x;
    }

    /**
     * Converts a latitude value from WGA84 to Mercator Y.
     *
     * @param y the latitude value in WGA84
     * @return the latitude value in Mercator Y
     */
    public static double wga84ToMercatorY(double y) {
        return Math.log(Math.tan((90.0 + y) * Math.PI / 360.0)) / (Math.PI / 180.0);
    }

}
