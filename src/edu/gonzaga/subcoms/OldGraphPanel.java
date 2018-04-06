/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gonzaga.subcoms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Aaron
 */
public class OldGraphPanel extends JPanel {
    int width;
    int height;
    int yScale;
    double xScale = 10;
    String name;
    ArrayList<Double> x;
    ArrayList<Double> y;
    int startTime;
    boolean started;
    static int numGraphs = 0;
    public OldGraphPanel(int w, int h, int yS) {        
        this(w,h,yS,"Unnamed Graph " + ++numGraphs);
    }
    public OldGraphPanel(int w, int h, int yS, String n) {
        width = w;
        height = h;
        yScale = yS;
        name = n;
        startTime = 0;
        started = false;
        super.setPreferredSize(new Dimension(width, height));
        x = new ArrayList<Double>();
        y = new ArrayList<Double>();
    }
    
    
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(255, 79, 0));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.fillRect(width/20, 0, 2, height);
        g.fillRect(0, height/2, width, 2);
        int[] xPoints = new int[x.size()];
        boolean finished = false;
        while(!finished) {
            finished = true;
            for(int i = 0; i < xPoints.length; i++) {
                xPoints[i] = width/20 + (int)((x.get(i)-startTime)*xScale);
                if(xPoints[i] > width) {
                    finished = false;
                    if(xScale > 1)
                        xScale-=.5;
                    else {
                        width+=10;
                        this.setMinimumSize(new Dimension(width, height));
                    }
                    break;
                }
            }
        }
        int[] yPoints = new int[y.size()];
        finished = false;
        while(!finished) {
            finished = true;
            for(int i = 0; i < yPoints.length; i++) {
                yPoints[i] = height/2 - (int)((height/2/yScale) * y.get(i));
                if(yPoints[i] < 0) {
                    finished = false;
                    yScale = y.get(i).intValue();
                    break;
                }
            }
        }
        g.setColor(Color.BLACK);
        g.drawPolyline(xPoints, yPoints, xPoints.length);
        g.drawString(""+yScale, 0, g.getFontMetrics().getHeight());
        g.drawString("-"+yScale, 0,height-5);
        g.drawString(""+((width-width/20)/xScale), width - SwingUtilities.computeStringWidth(g.getFontMetrics(), ""+(xScale*(width-width/20))), height/2+g.getFontMetrics().getHeight());
        g.drawString(name, width/2 - SwingUtilities.computeStringWidth(g.getFontMetrics(), name)/2, g.getFontMetrics().getHeight());
        
    }
    
    public void addPoint(double xCoord, double yCoord) {
        x.add(xCoord);
        y.add(yCoord);
        this.repaint();
    }
    
    public void setStartTime(int s) {
        startTime = s;
        started = true;
    }
    
    public boolean hasStarted() {
        return started;
    }
    
    
    
    
}
