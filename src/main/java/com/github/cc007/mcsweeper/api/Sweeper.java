/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.mcsweeper.api;

/**
 *
 * @author Rik
 */
public interface Sweeper {

    public Field sweep(int xPos, int yPos);
    
    public Field getField();
    
    public Field resetField();
    
    public boolean isGameOver();
    
    public boolean hasWon();
}
