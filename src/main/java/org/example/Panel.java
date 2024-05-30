package org.example;

import org.opensky.api.OpenSkyApi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;

public class Panel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {

    OpenSkyApi api = new OpenSkyApi("Landys", "flighttracker");

    private CountryData countryData;
    private double[] startPoint;

    private PlaneData planeData;

    private CityData cityData;

    private double zoom = 1;
    private double moveX;
    private double moveY;
    private double pressedX;
    private double pressedY;
    private double relesedX;
    private double relesedY;

    private double currentMouseX;
    private double currentMouseY;
    private double deltaTime;
    private long lastTime = System.currentTimeMillis();

    private boolean viewTable = false;
    private boolean viewAll = false;
    private boolean viewCities = true;
    private int tablePage = 0;

    private boolean failedToUpdate = false;

    /**
     * Initializes a new Panel instance. This constructor sets up the initial
     * state of the panel, including loading country and city data, setting start points,
     * adding mouse listeners, and configuring timers for animation and periodic updates.
     */
    public Panel(){
        this.countryData = new CountryData("world-administrative-boundaries.csv");
        this.cityData = new CityData("worldcities.csv", 250000);

        this.startPoint = new double[]{20,0};
        this.moveX = startPoint[0];
        this.moveY = startPoint[1];
        this.pressedX = startPoint[0];
        this.pressedY = startPoint[1];
        this.relesedX = startPoint[0];
        this.relesedY = startPoint[1];

        updatePlaneData();

        this.addMouseWheelListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);

        int fpsCount = 165;
        int millis = (int) ((1.0/fpsCount) * 1000.0);

        Timer fps = new Timer(millis, e -> {
            deltaTime = System.currentTimeMillis() - lastTime + millis;
            //System.out.println(deltaTime);
            for (Plane p : planeData.planes){
                p.animate((int) deltaTime);
            }
            lastTime = System.currentTimeMillis();
            repaint();
        });
        fps.setRepeats(true);
        fps.start();

        if (!failedToUpdate){
            Timer timer = new Timer(5000, e -> {
                updatePlaneData();
                revalidate();
                repaint();
            });
            timer.setRepeats(true);
            timer.start();
        }
    }

    public boolean isViewAll() {
        return viewAll;
    }

    public void setViewAll(boolean viewAll) {
        this.viewAll = viewAll;
    }

    public boolean isViewTable() {
        return viewTable;
    }

    public void setViewTable(boolean viewTable) {
        this.viewTable = viewTable;
    }

    public boolean isViewCities() {
        return viewCities;
    }

    public void setViewCities(boolean viewCities) {
        this.viewCities = viewCities;
    }

    public int getTablePage() {
        return this.tablePage;
    }

    public void setTablePage(int tablePage) {
        this.tablePage = tablePage;
    }

    /**
     * Paints the components of this Panel. This method is responsible for rendering
     * the background, translating the coordinate system, and drawing various elements
     * such as cities, country vectors, planes, zoom level, a table, and keys. It handles
     * the visual representation based on current state and user interactions.
     *
     * @param g1d the Graphics object used for drawing
     */
    public void paintComponent(Graphics g1d){
        Graphics2D g = (Graphics2D) g1d;

        //Background
        g.setColor(new Color(0,0,0));
        g.fillRect(0,0, getWidth(), getHeight());

        //Translate
        g.translate((double) getWidth()/2 + moveX * zoom, (double) getHeight()/2 + moveY * zoom);

        //Cities
        if (viewCities){
            for (City c : cityData.getCities()){
                //int size = (int) ((Math.max(2, Math.log10(c.getPopulation() / 1000000.0) * zoom)));
                int size = (int) (Math.max(1, 0.2 * zoom));
                int x = (int) (CountryData.wga84ToMercatorX(c.getCoordinates()[0]) * zoom + moveX);
                int y = (int) (-CountryData.wga84ToMercatorY(c.getCoordinates()[1]) * zoom + moveY);
                g.setColor(new Color(30,30,30, 30));
                g.fillOval(x - size/2, y - size/2, size, size);

                g.setColor(new Color(134, 134, 134));
                g.setFont(new Font("Ariel", Font.BOLD, size/5));
                g.drawString(c.getName().toUpperCase(), x - size/2, y + size/10);
            }
        }

        //Vectors
        for (CountryVector cv : countryData.getCountryVectors()){
            for (double[][] c : cv.getCoordinates()){
                Path2D path = new Path2D.Double();
                path.moveTo(c[0][0] * zoom + moveX, -c[0][1] * zoom + moveY);
                for (int i = 1; i < c.length; i++) {
                    path.lineTo(c[i][0] * zoom + moveX, -c[i][1] * zoom + moveY);
                }
                path.closePath();

                g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(new Color(255,255,255));
                g.draw(path);
            }
        }

        //Planes
        for (Plane plane : planeData.planes){
            if (plane.getAltitude() > 30){
                double[] gps = plane.getCoordinates();
                int x = (int) (CountryData.wga84ToMercatorX(gps[0]) * zoom + moveX);
                int y = (int) (-CountryData.wga84ToMercatorY(gps[1]) * zoom + moveY);
                int size = (int) (Math.max(1, zoom / 50));
                float stoke = (int) (Math.max(1, zoom / 125));

                //Heading Arrow
                double arrowLenght = plane.getVelocity() / 2500;
                double ang = plane.getHeading() + 90;
                double withX = (int) (CountryData.wga84ToMercatorX(gps[0] + Math.cos(Math.toRadians(ang)) * arrowLenght * -1) * zoom + moveX);
                double withY = (int) (-CountryData.wga84ToMercatorY(gps[1] + Math.sin(Math.toRadians(ang)) * arrowLenght) * zoom + moveY);
                Path2D path = new Path2D.Double();
                path.moveTo(x, y);
                path.lineTo(withX, withY);

                g.setColor(new Color(53, 181, 68));
                g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(path);

                //Plane Icon
                g.setColor(new Color(0,0,0));
                g.fillRect(x - size/2, y - size/2, size, size);

                g.setColor(new Color(53, 181, 68));
                g.setStroke(new BasicStroke(stoke, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
                g.drawRect(x - size/2, y - size/2, size, size);

                //Text
                if (currentMouseX >= x - size/2.0 && currentMouseX <= x + size/2.0 && currentMouseY >= y - size/2.0 && currentMouseY <= y + size/2.0){

                }

                if (viewAll){
                    double offset = 1;
                    int textSize = (int) Math.floor(zoom / 50);
                    g.setColor(new Color(53,181,68));
                    g.setFont(new Font("System", Font.BOLD, textSize));
                    g.drawString(plane.getCallSign(), x - textSize, (int) (y - textSize * 3 - offset));
                    g.drawString(plane.getAltitudeATC(), x - textSize, (int) (y - textSize * 2 - offset));
                    g.drawString(String.valueOf((int) (Math.floor(plane.getVelocity() * Plane.M_TO_KNOTS))), x - textSize, (int) (y - textSize * 1 - offset));
                }
            }
        }

        //Translate back
        g.translate((double) -getWidth()/2 - moveX * zoom, (double) -getHeight()/2 - moveY * zoom);

        //Zoom text
        double zoomRounded = Math.round(zoom * 10.0) / 10.0;
        g.setColor(new Color(255,255,255));
        g.setFont(new Font("Ariel", Font.BOLD, 30));
        g.drawString("zoom: " + zoomRounded, getWidth() / 8, getHeight() / 8);

        //Table
        if (viewTable){
            int minX = (int) (getWidth() - (getWidth() / 3.0));
            int minY = 0;
            int maxX = (int) (getWidth() / 3.0) - 35;
            int maxY = getHeight();

            g.setColor(new Color(25,25,25));
            g.fillRect(minX, minY, maxX, maxY);

            int fontSize = 20;
            int headerOffset = 2;
            int offset = 10;
            tablePage = Math.max(0, Math.min(planeData.planes.size() / (maxY / fontSize) - headerOffset, tablePage));
            for (int i = -1; i < (maxY / fontSize) - headerOffset; i++) {
                int prevCells = 0;
                for (int j = 0; j < 4; j++) {
                    int x = (minX + fontSize) + prevCells;
                    int y = minY + fontSize * (i + headerOffset) + offset;

                    int cellSize = switch (j){
                        case 0 -> 100;
                        case 1 -> 135;
                        case 2 -> 125;
                        case 3 -> 135;
                        default -> 10;
                    };

                    Color cellColor;
                    if (i == -1) cellColor = new Color(20,20,20);
                    else cellColor = new Color(5,5,5);
                    g.setColor(cellColor);
                    g.fillRect(x, y, cellSize - offset, -fontSize + offset / 10);

                    String text;
                    if (i == -1){
                        text = switch (j){
                            case 0 -> "ORDER";
                            case 1 -> "CALL SIGN";
                            case 2 -> "ALTITUDE";
                            case 3 -> "VELOCITY";
                            default -> "ERROR";
                        };
                    }
                    else {
                        text = switch (j){
                            case 0 -> i + 1 + (maxY / fontSize - headerOffset) * tablePage + ".";
                            case 1 -> planeData.planes.get(i + (maxY / fontSize - headerOffset) * tablePage).getCallSign();
                            case 2 -> ((int) Math.floor(planeData.planes.get(i + (maxY / fontSize - headerOffset) * tablePage).getAltitude())) + " ft";
                            case 3 -> ((int) Math.floor(planeData.planes.get(i + (maxY / fontSize - headerOffset) * tablePage).getVelocity() * Plane.M_TO_KNOTS)) + " kts";
                            default -> "ERROR";
                        };
                    }
                    g.setColor(new Color(255,255,255));
                    g.setFont(new Font("Ariel", Font.BOLD, fontSize));
                    g.drawString(text, x + offset, y);

                    prevCells += cellSize;
                }
            }
        }

        //Keys
        int minX = getWidth() - 35;
        int minY = 0;
        int maxX = minX;
        int maxY = getHeight() / 7;

        int fontSize = 50;
        int offset = 5;
        //Table
        if (viewTable) g.setColor(new Color(0, 105, 0));
        else g.setColor(new Color(25,25,25));
        g.fillRect(minX, minY, maxX, maxY);

        g.setColor(new Color(255,255,255));
        g.setFont(new Font("Ariel", Font.BOLD, fontSize));
        g.drawString("T", minX, (int) (maxY / 2.0 + fontSize / 2));

        //Text
        if (viewAll) g.setColor(new Color(0, 105, 0));
        else g.setColor(new Color(25,25,25));
        g.fillRect(minX, minY + maxY + offset, maxX + maxY + offset, maxY);

        g.setColor(new Color(255,255,255));
        g.setFont(new Font("Ariel", Font.BOLD, fontSize));
        g.drawString(" I", minX, (int) ((maxY / 2.0) + maxY + offset + fontSize / 2));

        //Cities
        if (viewCities) g.setColor(new Color(0, 105, 0));
        else g.setColor(new Color(25,25,25));
        g.fillRect(minX, minY + maxY*2 + offset*2, maxX + maxY*2 + offset*2, maxY);

        g.setColor(new Color(255,255,255));
        g.setFont(new Font("Ariel", Font.BOLD, fontSize));
        g.drawString("C", minX, (int) ((maxY / 2.0) + maxY*2 + offset*2 + fontSize / 2));

        lastTime = System.currentTimeMillis();
    }

    /**
     * This method attempts to fetch new plane data from an external source,
     * If the data retrieval fails, it catches he exception, displays an error message to the user,
     * and generates random plane data for visualization purposes.
     */

    private int i = 0;
    public void updatePlaneData(){
        try {
            this.planeData = new PlaneData(api);
            System.out.println("Updated.");
            i++;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            if (i < 1){
                JOptionPane.showInternalMessageDialog(null, "API is currently unavailable.\n" +
                        "Planes are generated randomly for program visualisation.");
                this.planeData = new PlaneData(api, true);
                i++;
            }
        }
    }

    /**
     * Handles the mouse wheel moved events to adjust the zoom level and zoom level does not drop below 1.
     *
     * @param e the MouseWheelEvent object representing the mouse wheel event
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double zoomDPI = 1.5;
        zoom -= e.getWheelRotation() * zoomDPI * (zoom / 4);

        if (zoom < 1) zoom = 1;

        repaint();
    }

    /**
     * This method adjusts the view position based on the mouse movement and the current zoom level.
     *
     * @param e the MouseEvent object representing the mouse dragged event
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        double dpi = 0.122;
        double xi = dpi / Math.max(2.5, zoom) * 8;
        double yi = dpi / Math.max(2.5, zoom) * 8;
        moveX = -((pressedX - (e.getX())) * xi - relesedX);
        moveY = -(pressedY - (e.getY())) * yi + relesedY;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        currentMouseX = e.getX();
        currentMouseY = e.getY();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressedX = e.getX();
        pressedY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        relesedX = moveX;
        relesedY = moveY;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
