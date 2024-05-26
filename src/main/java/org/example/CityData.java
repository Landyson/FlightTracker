package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class CityData {

    private ArrayList<City> cities = new ArrayList<>();
    private int cutoffPop;

    /**
     * Constructs a CityData object with the specified file path and population cutoff.
     *
     * @param path      the path to the file containing city data
     * @param cutoffPop the minimum population required for a city to be included in the list
     */
    public CityData(String path, int cutoffPop) {
        this.cutoffPop = cutoffPop;
        load(path);
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    /**
     * Loads city data from a file and adds cities to the list if they meet the population cutoff.
     *
     * @param path the path to the file containing city data
     */
    public void load(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            br.readLine();
            String s = "";
            while ((s = br.readLine()) != null) {
                s = s.replace("\"", "");
                String[] data = s.split(",");

                int pop = 0;
                int offset = 0;
                for (int i = 0; i < 10; i++) {
                    try {
                        if (data[9 + offset] == null || data[9 + offset].equalsIgnoreCase("") || Integer.parseInt(data[9 + offset]) >= 50000000)
                            continue;
                        if (Integer.parseInt(data[9 + offset]) < cutoffPop) break;
                        //System.out.println(data[9 + offset]);
                        pop = Integer.parseInt(data[9 + offset]);
                        break;
                    } catch (Exception ignored) {
                        offset++;
                    }
                }
                if (pop == 0) continue;

                cities.add(new City(data[1], new double[]{Double.parseDouble(data[3]), Double.parseDouble(data[2])}, data[4], pop));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
