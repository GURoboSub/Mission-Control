/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gonzaga.subcoms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Aaron
 */
public class CompassPanel extends JPanel {
    int compassX;
    int compassY;
    int compassX2;
    int compassY2;
    int width = 500;
    int height = 500;
    double angle = 0;
    public CompassPanel(int w, int h) {
        width = w;
        height = h;
        super.setPreferredSize(new Dimension(width, height));
        compassX = width/2 - 5;
        compassX2 = width/2 + 5;
        compassY = 0;
        compassY2 = 0;
        

    }
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(255,239,213));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.BLACK);
        g.fillOval(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.fillRect(width/2, 0, 2, height);
        g.fillRect(0, height/2, width, 2);
        g.setColor(Color.RED);
        int[] x = {width/2, compassX, compassX2};
        int[] y = {height/2, compassY, compassY2};
        g.drawLine(width/2, height/2, compassX, compassY);
        g.fillPolygon(x, y, 3);
        g.drawString("Angle: " + angle, width - width/6, height - height/10);

    }
    
    public void setAngle(double a) {
        angle = a;
        if(a < 0)
            a = 0;
        if(a > 360)
            a = 360;
        compassX = (int)(width/2 + width/2 * Math.cos(a * Math.PI / 180)) - 5;
        compassX2 = compassX + 10;
        compassY = (int)(height/2 - height/2 * Math.sin(a * Math.PI /180)) - 5;
        compassY2 = compassY + 10;
        this.repaint();
    }
}
