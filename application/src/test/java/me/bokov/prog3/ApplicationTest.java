/*
 *     Chatter - my Programming III. homework assignment
 *     Copyright (C) 2018  Botond János Kovács
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.bokov.prog3;

import org.junit.After;
import org.junit.Test;

public class ApplicationTest {

    @After
    public void afterEachTest() {

        Application.destroyInstance();

    }

    @Test
    public void test1() {

        Application.createInstance(new String[]{"--no-gui"});

    }

    @Test
    public void test2() {

        Application.createInstance(new String[]{"--server", "--port", "10467"});

    }

}