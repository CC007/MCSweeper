/* 
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 <http://coolcat007.nl/>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.cc007.mcsweeper.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.github.cc007.mcsweeper.api.Field;
import com.github.cc007.mcsweeper.api.Sweeper;
import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class MineSweeper implements Sweeper {

    private Field knownField;
    private Field hiddenField;
    private int width;
    private int height;
    private int totalBombCount;
    private boolean lost;
    private boolean won;

    public MineSweeper(boolean emptyObject) {
        if (!emptyObject) {
            this.width = 9;
            this.height = 9;
            this.totalBombCount = 10;
            resetField();
        }
    }

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
        resetField();
    }

    @Override
    public Field sweep(int xPos, int yPos) {
        if (knownField.getState(xPos, yPos) == Field.UNKNOWN_STATE) {
            System.out.println("A state will be changed at <" + xPos + "," + yPos + ">");
            if (hiddenField.getState(xPos, yPos) == Field.BOMB_STATE) {
                knownField.setState(xPos, yPos, Field.BOMB_STATE);
                showBombs();
                lost = true;
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
                if (knownField.getState(i, j) == Field.UNKNOWN_STATE || knownField.getState(i, j) == Field.FLAG_STATE) {
                    currentPossibleBombCount++;
                }
            }
        }
        if (currentPossibleBombCount == totalBombCount) {
            won = true;
        }
        return knownField;
    }

    @Override
    public Field flag(int xPos, int yPos) {
        if (knownField.getState(xPos, yPos) == Field.UNKNOWN_STATE) {
            knownField.setState(xPos, yPos, Field.FLAG_STATE);
        } else if (knownField.getState(xPos, yPos) == Field.FLAG_STATE) {
            knownField.setState(xPos, yPos, Field.UNKNOWN_STATE);
        }
        return knownField;
    }

    @Override
    public Field getField() {
        return knownField;
    }

    @Override
    public final Field resetField() {
        lost = false;
        won = false;
        knownField = new MineField(width, height);
        hiddenField = new MineField(width, height);
        generateHiddenField();
        return knownField;
    }

    @Override
    public boolean hasLost() {
        return lost;
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

    private void showBombs() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (hiddenField.getState(i, j) == Field.BOMB_STATE) {
                    knownField.setState(i, j, Field.BOMB_STATE);
                }
            }
        }
    }

    private void showFlags() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (hiddenField.getState(i, j) == Field.BOMB_STATE) {
                    knownField.setState(i, j, Field.FLAG_STATE);
                }
            }
        }
    }

    @Override
    public String toString() {
        return knownField.toString();
    }

    @Override
    public JSONObject serialize() {
        JSONObject output = new JSONObject();
        output.put("knownField", knownField.serialize());
        output.put("hiddenField", hiddenField.serialize());
        output.put("width", width);
        output.put("height", height);
        output.put("totalBombCount", totalBombCount);
        output.put("lost", lost);
        output.put("won", won);
        return output;
    }

    @Override
    public void deserialize(JSONObject input) {
        knownField = new MineField(true);
        knownField.deserialize(input.getJSONObject("knownField"));
        hiddenField = new MineField(true);
        hiddenField.deserialize(input.getJSONObject("hiddenField"));
        width = input.getInt("width");
        height = input.getInt("height");
        totalBombCount = input.getInt("totalBombCount");
        lost = input.getBoolean("lost");
        won = input.getBoolean("won");
    }
}
