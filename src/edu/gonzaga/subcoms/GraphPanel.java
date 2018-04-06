package edu.gonzaga.subcoms;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Aaron
 */
public class GraphPanel extends JPanel {
    
    private XYSeries series;
    private XYSeriesCollection collection;
    private JFreeChart chart;
    private ChartPanel graph;
    private JButton clear;
    private boolean started = false;
    private long startTime = 0;
    
    
    static int numGraphs = 0; 
    public GraphPanel(int w, int h) {
        this(w,h,"Unnamed Graph " + ++numGraphs);
    }
    
    public GraphPanel(int w, int h, String name) {
        this(w,h,name,"x","y");
    }
    
    public GraphPanel(int w, int h, String name, String x, String y) {
        series = new XYSeries(name);
        collection = new XYSeriesCollection(series);
        chart = ChartFactory.createXYLineChart(name, x, y, collection);
        graph = new ChartPanel(chart);
        graph.setPreferredSize(new Dimension(w,h-30));
        FlowLayout flow = new FlowLayout();
        flow.setVgap(0);
        super.setLayout(flow);
        super.add(graph);
        clear = new JButton("Clear Data");
        clear.setPreferredSize(new Dimension(w,30));
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                series.clear();
                started = false;
            }
        });
        super.add(clear);
        super.setPreferredSize(new Dimension(w,h));
        
        
    }
    
    public void addPoint(double x, double y) {
        series.add(x-startTime,y);
    }
    
    public void setStartTime(long s) {
        startTime = s;
        started = true;
    }
    
    public boolean hasStarted() {
        return started;
    }

    
}
