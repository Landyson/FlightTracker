package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class CityData {

    private ArrayList<City> cities = new ArrayList<>();
    private int cutoffPop;

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
                        System.out.println(data[9 + offset]);

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
