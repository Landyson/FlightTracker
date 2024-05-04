package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;

public class Panel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {

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
    private boolean viewAll = false;

    public Panel(){
        this.countryData = new CountryData("lib/world-administrative-boundaries.csv");
        this.cityData = new CityData("lib/worldcities.csv", 250000);
        CountryVector countryBox = countryData.nameToCountryVector("Czech Republic");

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
        addKeyListener(this);

        Timer timer = new Timer(5000, e -> {
            updatePlaneData();
            repaint();
        });
        timer.setRepeats(true);
        timer.start();
    }

    public void paintComponent(Graphics g1d){
        Graphics2D g = (Graphics2D) g1d;

        //Background
        g.setColor(new Color(0,0,0));
        g.fillRect(0,0, getWidth(), getHeight());

        //Zoom text
        double zoomRounded = Math.round(zoom * 10.0) / 10.0;
        g.setColor(new Color(255,255,255));
        g.setFont(new Font("Ariel", Font.BOLD, 30));
        g.drawString("zoom: " + zoomRounded, getWidth() / 8, getHeight() / 8);
        g.drawString("GPS: " + mouseXToGPS(currentMouseX) + ", " + mouseYToGPS(currentMouseY), getWidth() / 8, getHeight() / 8 + 30);

        //Translate
        g.translate((double) getWidth()/2 + moveX * zoom, (double) getHeight()/2 + moveY * zoom);

        //Cities
        for (City c : cityData.getCities()){
            //int size = (int) ((Math.max(2, Math.log10(c.getPopulation() / 1000000.0) * zoom)));
            int size = (int) (Math.max(1, 0.2 * zoom));
            int x = (int) (CountryData.wga84ToMercatorX(c.getCoordinates()[0]) * zoom + moveX);
            int y = (int) (-CountryData.wga84ToMercatorY(c.getCoordinates()[1]) * zoom + moveY);
            g.setColor(new Color(30,30,30));
            g.fillOval(x - size/2, y - size/2, size, size);

            g.setColor(new Color(134, 134, 134));
            g.setFont(new Font("Ariel", Font.BOLD, size/5));
            g.drawString(c.getName().toUpperCase(), x - size/2, y + size/10);
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
    }

    public double mouseXToGPS(double mouseX){
        double mapToFull = 360.0 / getWidth();
        mouseX -= getWidth()/2.0;
        return mouseX * mapToFull - moveX;
    }

    public double mouseYToGPS(double mouseY){
        mouseY -= getHeight()/2.0;
        //System.out.println("move: x: " + moveX + ", y: " + moveY);
        return (mouseY / zoom) + moveY;
    }

    public void updatePlaneData(){
        try {
            this.planeData = new PlaneData(countryData.boxForCountry("Czech Republic"));
            System.out.println("Updated.");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double zoomDPI = 1.5;
        zoom -= e.getWheelRotation() * zoomDPI * (zoom / 4);

        if (zoom < 1) zoom = 1;

        repaint();
    }

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

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("sdsdfasdff0");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("sdsdfasdff1");
        if (e.getKeyCode() == KeyEvent.VK_I) {
            viewAll = !viewAll;
            System.out.println("View changed");
        }
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("sdsdfasdff2");
    }
}
