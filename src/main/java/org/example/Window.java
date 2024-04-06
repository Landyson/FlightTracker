package org.example;

import javax.swing.*;

public class Window extends JFrame {

    public Window(){
        this.setTitle("Letadla");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1920, 1080);

        Panel panel = new Panel();
        this.add(panel);

        this.setVisible(true);
    }

}
