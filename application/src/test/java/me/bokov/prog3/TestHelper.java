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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestHelper {

    public static void startApplicationInTempEnv (String [] args) throws Exception {

        Application.destroyInstance();

        Path tempDirPath = Files.createTempDirectory("chatter-test");

        File tempDirFile = tempDirPath.toFile();

        System.out.println("Will use '" + tempDirFile.getAbsolutePath() + "' as data directory for testing purposes\n");

        String [] actualArgs = new String[(args != null ? args.length : 0) + 2];
        actualArgs [0] = "--data-directory";
        actualArgs [1] = tempDirFile.getAbsolutePath();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                actualArgs [i + 2] = args [i];
            }
        }

        Application.createInstance(actualArgs);

    }

}
