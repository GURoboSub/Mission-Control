/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gonzaga.subcoms;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.nio.ByteBuffer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author Aaron
 */
public class DataPanel extends JPanel {
    
//    ButtonGroup buttons;
//    JRadioButton setP;
//    JRadioButton setI;
//    JRadioButton setD;
    JButton send;
    JTextArea input;
    JComboBox<DataType> select;
    

    
    
    public DataPanel(int w, int h) {
        
        super.setPreferredSize(new Dimension(w,h));
        FlowLayout flow = new FlowLayout();
//        flow.setVgap(0);
        super.setLayout(flow);
        select = new JComboBox<>(DataType.values());
        select.setSelectedIndex(0);        
        super.add(select);
        
        input = new JTextArea(1,30);
        super.add(input);
        
        send = new JButton("Send");
        send.addActionListener((ActionEvent e) -> {
            char c = select.getItemAt(select.getSelectedIndex()).getID();            
            String numString = input.getText();
            //System.err.println(numString);
            float floatValue = Float.parseFloat(numString) - (float)0.0001;
            //System.err.println(floatValue);
            byte[] b = ByteBuffer.allocate(4).putFloat(floatValue).array();
            TiComs.writeCOMS(c);
            for(int  i = 0; i < 4; i++){
                TiComs.writeCOMS((char)(((char)b[i])&(char)0x00FF));
                //System.err.println(i + ": " + (char)(((char)b[i])&(char)0x00FF));
            }
            TiComs.writeCOMS('~');
            input.setText("");
        });
        super.add(send);


        
//        buttons = new ButtonGroup();
//        setP = new JRadioButton("Set Depth P");
//        setP.addActionListener((ActionEvent e) -> {
//            type = DataType.DEPTH_P;
//        });
//        setI = new JRadioButton("Set Depth I");
//        setI.addActionListener((ActionEvent e) -> {
//            type = DataType.DEPTH_I;
//        });
//        setD = new JRadioButton("Set Depth D");
//        setD.addActionListener((ActionEvent e) -> {
//            type = DataType.DEPTH_D;
//        });
//        
//        
//        buttons.add(setP);
//        buttons.add(setI);
//        buttons.add(setD);
//        
//        super.add(setP);
//        super.add(setI);
//        super.add(setD);
        
        
        
        
        
    }
}
