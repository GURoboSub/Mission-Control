/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gonzaga.subcoms;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Aaron
 */
public class ConsolePanel extends JPanel {
    
    private JTextArea output;
    private JTextArea input;
    private JButton send;
    String sendString = "";
    JScrollPane scrollPane;
    JScrollBar sb;
    
    public ConsolePanel(int w, int h) {
        super.setPreferredSize(new Dimension(w,h));
        FlowLayout flow = new FlowLayout();
        flow.setVgap(0);
        super.setLayout(flow);
        output = new JTextArea(5,30);
        output.setWrapStyleWord(true);
        output.setEditable(false);
        PrintStream out = new PrintStream(new TextAreaOutputStream(output));
        System.setOut(out);
        

        scrollPane = new JScrollPane(output);
        input = new JTextArea(1,30);
        input.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !input.getText().equals("")) {
                    sendString = input.getText();
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendString = " ";
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    input.setText("");
                }

            }
        });
        int fontHeight = input.getFontMetrics(input.getFont()).getHeight();
        scrollPane.setPreferredSize(new Dimension(w,h-5-fontHeight));
        scrollPane.setAutoscrolls(true);
        sb = scrollPane.getVerticalScrollBar();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        input.setPreferredSize(new Dimension(w, 5+fontHeight));
        super.add(scrollPane);
        super.add(input);
        

    }
    
    public String getInput() {
        String s = sendString;
        sendString = "";
        return s;
    }
    
    public void scrollDown() {
        sb.setValue(sb.getMaximum());
    }
    
}
