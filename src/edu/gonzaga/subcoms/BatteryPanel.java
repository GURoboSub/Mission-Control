/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gonzaga.subcoms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Aaron
 */
public class BatteryPanel extends JPanel {
    
    int width;
    int height;
    double batteryMax;
    double batteryLevel;
    
    public BatteryPanel(int w, int h, double bm) {
        width = w;
        height = h;
        super.setPreferredSize(new Dimension(width, height));
        batteryMax = bm;
        batteryLevel = bm;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        DecimalFormat df = new DecimalFormat("0.00%");
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.GREEN);
        double percent = batteryLevel/batteryMax;
        g.fillRect(0, height - (int)(height*percent), width, (int)(height*percent));
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, width/5));
        String percentString = df.format(percent);
        g.drawString(percentString, width/2 - SwingUtilities.computeStringWidth(g.getFontMetrics(), percentString)/2, height-height/30);
        
        
    }
    
    public void setBatteryLevel(double b) {
        batteryLevel = b;
        this.repaint();
    }
    
}
