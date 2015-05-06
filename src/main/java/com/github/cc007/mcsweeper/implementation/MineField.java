/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.mcsweeper.implementation;

import java.util.ArrayList;
import java.util.List;
import com.github.cc007.mcsweeper.api.Field;

public class MineField implements Field {

    private List<List<Integer>> tiles;
    private int width = 9;
    private int height = 9;

    public MineField() {
        init();
    }

    public MineField(int size) {
        this.width = size;
        this.height = size;
        init();
    }

    public MineField(int width, int height) {
        this.width = width;
        this.height = height;
        init();
    }

    private void init() {
        tiles = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            List<Integer> temp = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                temp.add(-2);
            }
            tiles.add(temp);
        }
    }

    @Override
    public int getState(int xPos, int yPos) {
        return tiles.get(yPos).get(xPos);
    }

    @Override
    public void setState(int xPos, int yPos, int state) {
        tiles.get(yPos).set(xPos, state);
    }

    @Override
    public String toString() {
        String returnStr = "";
        returnStr += "+";
        for (int i = 0; i < width; i++) {
            returnStr += "-+";
        }
        returnStr += "\n";
        for (int i = 0; i < height; i++) {
            returnStr += "|";
            for (int j = 0; j < width; j++) {
                if (getState(j, i) == BOMB_STATE) {
                    returnStr += "B";
                } else if (getState(j, i) == EMPTY_STATE) {
                    returnStr += " ";
                } else if (getState(j, i) == UNKNOWN_STATE) {
                    returnStr += "?";
                } else {
                    returnStr += getState(j, i);
                }
                returnStr += "|";
            }
            returnStr += "\n+";
            for (int j = 0; j < width; j++) {
                returnStr += "-+";
            }
            returnStr += "\n";
        }
        return returnStr;
    }

}
