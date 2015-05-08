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

import com.github.cc007.mcsweeper.api.Field;
import com.github.cc007.mcsweeper.api.Sweeper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Rik Schaaf aka CC007 <http://coolcat007.nl/>
 */
public class MineSweeperTest {

    public MineSweeperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of the basic game.
     */
    @Test
    public void testGame() {
        String data = "0\n0\n-1\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Sweeper s = new MineSweeper(8, 10, 3);
        int x, y;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.println(s);
                if (s.hasLost()) {
                    System.out.println("Game over!");
                    break;
                }
                if (s.hasWon()) {
                    System.out.println("You won, congratz!");
                    break;
                }
                System.out.println("Xpos: ");
                x = Integer.parseInt(input.readLine());
                if (x < 0) {
                    System.out.println("Game ended");
                    break;
                }
                System.out.println("Ypos: ");
                y = Integer.parseInt(input.readLine());
                s.sweep(x, y);
            } catch (IOException ex) {
                Logger.getLogger(MineSweeperTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Test of the basic game.
     */
    @Test
    public void testSerialization() {
        String data = "0\n0\n-1\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Sweeper s1 = new MineSweeper(8, 10, 3);
        Sweeper s2 = new MineSweeper(true);
        JSONObject serializedS1 = s1.serialize();
        s2.deserialize(serializedS1);
        int x, y;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.println(s1);
                if (s1.hasLost()) {
                    System.out.println("Game over!");
                    break;
                }
                if (s1.hasWon()) {
                    System.out.println("You won, congratz!");
                    break;
                }
                System.out.println("Xpos: ");
                x = Integer.parseInt(input.readLine());
                if (x < 0) {
                    System.out.println("Game ended");
                    break;
                }
                System.out.println("Ypos: ");
                y = Integer.parseInt(input.readLine());
                s1.sweep(x, y);
            } catch (IOException ex) {
                Logger.getLogger(MineSweeperTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
