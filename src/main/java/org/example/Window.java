package org.example;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Window extends JFrame implements KeyListener {

    Panel panel;

    /**
     * Constructs a Window object.
     */
    public Window() {
        this.setTitle("Letadla");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1920, 1080);

        this.addKeyListener(this);
        this.setFocusable(true);

        this.setFocusable(true);
        this.setVisible(true);

        panel = new Panel();
        this.add(panel);

        revalidate();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Handles key pressed events.
     *
     * @param e the KeyEvent object representing the key pressed event
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_I) {
            panel.setViewAll(!panel.isViewAll());
            System.out.println("Inspect changed");
        }
        else if (e.getKeyCode() == KeyEvent.VK_T) {
            panel.setViewTable(!panel.isViewTable());
            System.out.println("Table changed");
        }
        else if (e.getKeyCode() == KeyEvent.VK_C) {
            panel.setViewCities(!panel.isViewCities());
            System.out.println("Cities changed");
        }
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
        else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) panel.setTablePage(panel.getTablePage() + 1);
        else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) panel.setTablePage(panel.getTablePage() - 1);
        panel.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
