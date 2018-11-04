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

import me.bokov.prog3.util.Config;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.enterprise.inject.spi.CDI;

public class Application {

    private Weld weld;
    private WeldContainer weldContainer;

    private static Application INSTANCE = null;

    private void initialize () {

        weld = new Weld();
        weldContainer = weld.initialize();

        // Load config
        CDI.current().select(Config.class).get().load();

    }

    static void createInstance () {
        INSTANCE = new Application();
        INSTANCE.initialize();
    }

    public static Application getInstance () {
        return INSTANCE;
    }

    public Weld getWeld() {
        return weld;
    }

    public WeldContainer getWeldContainer() {
        return weldContainer;
    }

}
