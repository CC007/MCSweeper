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
import com.github.cc007.mcsweeper.api.Field;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class MineField implements Field {

    private List<List<Integer>> tiles;
    private int width = 9;
    private int height = 9;

    public MineField(boolean emptyObject) {
        if (!emptyObject) {
            init();
        }
    }

    public MineField() {
        init();
    }

    public MineField(int size) {
        this(size, size);
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

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public JsonObject serialize() {
        JsonObject output = new JsonObject();
        JsonArray tilesJSON = new JsonArray();
        for (List<Integer> tilesInner : tiles) {
            
            JsonArray innerArray = new JsonArray();
            for (Integer tile : tilesInner) {
                innerArray.add(new JsonPrimitive(tile));
            }
            
            tilesJSON.add(innerArray);
        }
        output.add("tiles", tilesJSON);
        output.addProperty("width", width);
        output.addProperty("height", height);
        return output;
    }

    @Override
    public void deserialize(JsonObject input) {
        
        tiles = new ArrayList<>();
        JsonArray tilesJSON = input.get("tiles").getAsJsonArray();
        
        for (int i = 0; i < tilesJSON.size(); i++) {
            
            List<Integer> innerTiles = new ArrayList<>();
            JsonArray tilesInnerJSON = tilesJSON.get(i).getAsJsonArray();
            
            for (int j = 0; j < tilesInnerJSON.size(); j++) {
                innerTiles.add(tilesInnerJSON.get(j).getAsJsonPrimitive().getAsInt());
            }
            
            tiles.add(innerTiles);
        }
        
        width = input.get("width").getAsInt();
        height = input.get("height").getAsInt();
    }

}
