package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;

public class Panel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener, ActionListener {

    private CountryData countryData;
    private double[] startPoint;

    private PlaneData planeData;

    private double zoom = 30;
    private double moveX;
    private double moveY;
    private double pressedX;
    private double pressedY;
    private double relesedX;
    private double relesedY;

    public Panel(){
        this.countryData = new CountryData("lib/world-administrative-boundaries.csv");
        CountryVector countryBox = countryData.nameToCountryVector("Czech Republic");

        this.startPoint = countryBox.getGeoPoint();
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

        Timer timer = new Timer(10000, this);
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

        //Vectors
        g.translate((double) getWidth() /2 + moveX * zoom, (double) getHeight() /2 + moveY * zoom);

        for (CountryVector cv : countryData.getCountryVectors()){
            for (double[][] c : cv.getCoordinates()){
                Path2D path = new Path2D.Double();
                path.moveTo(c[0][0] * zoom + moveX, -c[0][1] * zoom + moveY);
                for (int i = 1; i < c.length; i++) {
                    path.lineTo(c[i][0] * zoom + moveX, -c[i][1] * zoom + moveY);
                }
                path.closePath();

                if (cv.getName().equalsIgnoreCase("Czech Republic")){
                    g.setColor(new Color(0,255,0));
                    g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                }
                else {
                    g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.setColor(new Color(255,255,255));
                }
                g.draw(path);
            }
        }

        //Planes
        for (Plane plane : planeData.planes){
            //Circle
            double[] gps = plane.getCoordinates();
            int x = (int) (gps[0] * zoom + moveX);
            int y = (int) (-gps[1] * zoom + moveY);
            int size = (int) (Math.max(1, zoom / 25));

            g.setColor(new Color(0, 0, 255));
            g.fillOval(x - size/2, y - size/2, size, size);

            //Text
            g.setColor(new Color(255,255,255));
            g.setFont(new Font("Ariel", Font.BOLD, (int) Math.floor(zoom / 50)));
            g.drawString(plane.getCallSign(), x, y);
        }
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
    public void actionPerformed(ActionEvent e) {
        updatePlaneData();
        System.out.println(planeData.planes.get(0).toString());
        repaint();
    }
}
