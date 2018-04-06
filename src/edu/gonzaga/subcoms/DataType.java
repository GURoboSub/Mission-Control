/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gonzaga.subcoms;

/**
 *
 * @author Aaron
 */
public enum DataType {
    D_HEADING(0), 
    D_DEPTH(1), 
    D_FORWARD(2), 
    DEPTH_P(10), 
    DEPTH_I(11), 
    DEPTH_D(12), 
    HEADING_P(13), 
    HEADING_I(14), 
    HEADING_D(15);
    
    char id;
    
    DataType(int i) {
        id = (char)i;
    }   
    
    public char getID() {
        return id;
    }
    
    
}



