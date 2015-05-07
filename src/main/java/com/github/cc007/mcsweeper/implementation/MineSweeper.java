/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.cc007.mcsweeper.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.github.cc007.mcsweeper.api.Field;
import com.github.cc007.mcsweeper.api.Sweeper;

public class MineSweeper implements Sweeper {

    private Field knownField;
    private Field hiddenField;
    private int width;
    private int height;
    private int totalBombCount;
    private boolean gameOver;
    private boolean won;

    public MineSweeper() {
        this(9);
    }

    public MineSweeper(int size) {
        this(size, size);
    }

    public MineSweeper(int width, int height) {
        this(width, height, 10);
    }

    public MineSweeper(int width, int height, int totalBombCount) {
        this.width = width;
        this.height = height;
        this.totalBombCount = totalBombCount;
    }

    @Override
    public Field sweep(int xPos, int yPos) {
        if (knownField.getState(xPos, yPos) <= Field.UNKNOWN_STATE) {
            System.out.println("A state will be changed at <" + xPos + "," + yPos + ">");
            if (hiddenField.getState(xPos, yPos) == Field.BOMB_STATE) {
                knownField.setState(xPos, yPos, Field.BOMB_STATE);
                gameOver = true;
            } else if (hiddenField.getState(xPos, yPos) == Field.EMPTY_STATE) {
                //flood fill
                knownField.setState(xPos, yPos, Field.EMPTY_STATE);
                if (xPos - 1 >= 0) {
                    sweep(xPos - 1, yPos);
                    if (yPos - 1 >= 0) {
                        sweep(xPos - 1, yPos - 1);
                    }
                    if (yPos + 1 < height) {
                        sweep(xPos - 1, yPos + 1);
                    }
                }
                if (yPos - 1 >= 0) {
                    sweep(xPos, yPos - 1);
                }
                if (yPos + 1 < height) {
                    sweep(xPos, yPos + 1);
                }
                if (xPos + 1 < width) {
                    sweep(xPos + 1, yPos);
                    if (yPos - 1 >= 0) {
                        sweep(xPos + 1, yPos - 1);
                    }
                    if (yPos + 1 < height) {
                        sweep(xPos + 1, yPos + 1);
                    }
                }
            } else {
                knownField.setState(xPos, yPos, hiddenField.getState(xPos, yPos));
            }
        }
        int currentPossibleBombCount = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(knownField.getState(i, j) == Field.UNKNOWN_STATE){
                    currentPossibleBombCount++;
                }
            }
        }
        if(currentPossibleBombCount == totalBombCount){
            won = true;
        }
        return knownField;
    }

    @Override
    public Field getField() {
        return knownField;
    }

    @Override
    public final Field resetField() {
        gameOver = false;
        won = false;
        knownField = new MineField(width, height);
        hiddenField = new MineField(width, height);
        generateHiddenField();
        return knownField;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public boolean hasWon() {
        return won;
    }

    private void generateHiddenField() {
        List<Integer> bombNrs = getBombNrs();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                hiddenField.setState(j, i, Field.EMPTY_STATE);
            }
        }
        int x, y;
        for (Integer bombNr : bombNrs) {
            x = bombNr % width;
            y = bombNr / width;
            hiddenField.setState(x, y, Field.BOMB_STATE);

            //add one to the eight surrounding tiles
            if (x - 1 >= 0) {
                if (hiddenField.getState(x - 1, y) >= 0) {
                    hiddenField.setState(x - 1, y, hiddenField.getState(x - 1, y) + 1);
                }
                if (y - 1 >= 0 && hiddenField.getState(x - 1, y - 1) >= 0) {
                    hiddenField.setState(x - 1, y - 1, hiddenField.getState(x - 1, y - 1) + 1);
                }
                if (y + 1 < height && hiddenField.getState(x - 1, y + 1) >= 0) {
                    hiddenField.setState(x - 1, y + 1, hiddenField.getState(x - 1, y + 1) + 1);
                }
            }
            if (y - 1 >= 0 && hiddenField.getState(x, y - 1) >= 0) {
                hiddenField.setState(x, y - 1, hiddenField.getState(x, y - 1) + 1);
            }
            if (y + 1 < height && hiddenField.getState(x, y + 1) >= 0) {
                hiddenField.setState(x, y + 1, hiddenField.getState(x, y + 1) + 1);
            }
            if (x + 1 < width) {
                if (hiddenField.getState(x + 1, y) >= 0) {
                    hiddenField.setState(x + 1, y, hiddenField.getState(x + 1, y) + 1);
                }
                if (y - 1 >= 0 && hiddenField.getState(x + 1, y - 1) >= 0) {
                    hiddenField.setState(x + 1, y - 1, hiddenField.getState(x + 1, y - 1) + 1);
                }
                if (y + 1 < height && hiddenField.getState(x + 1, y + 1) >= 0) {
                    hiddenField.setState(x + 1, y + 1, hiddenField.getState(x + 1, y + 1) + 1);
                }
            }
        }
    }

    private List<Integer> getBombNrs() {
        List<Integer> bombNrs = new ArrayList();
        Random r = new Random(System.nanoTime());
        for (int i = 0; i < totalBombCount; i++) {
            int temp = r.nextInt(width * height);
            boolean flag = false;
            for (Integer bombNr : bombNrs) {
                if (bombNr == temp) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                i--;
            } else {
                bombNrs.add(temp);
            }
        }
        return bombNrs;
    }

    @Override
    public String toString() {
        return knownField.toString();
    }

}
