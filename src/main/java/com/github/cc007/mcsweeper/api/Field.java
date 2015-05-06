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
public interface Field {

    public static final int UNKNOWN_STATE = -2;
    public static final int BOMB_STATE = -1;
    public static final int EMPTY_STATE = 0;

    int getState(int xPos, int yPos);

    void setState(int xPos, int yPos, int state);

}
