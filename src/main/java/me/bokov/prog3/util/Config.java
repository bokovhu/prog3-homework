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

package me.bokov.prog3.util;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

@ApplicationScoped
public class Config {

    private Locale userLocale;

    public void resetToDefaults() {

        userLocale = new Locale("en");

    }

    private void parseProperties(Properties properties) {

        userLocale = new Locale(properties.getProperty("user-locale"));

    }

    private Properties toProperties() {

        Properties properties = new Properties();

        properties.setProperty("user-locale", userLocale.getLanguage());

        return properties;

    }

    public void load() {

        File userHomeDirectory = new File(System.getProperty("user.home"));
        File appConfigDirectory = new File(userHomeDirectory, ".chatter");

        if (appConfigDirectory.exists()) {

            File configFile = new File(appConfigDirectory, "config.properties");

            if (configFile.exists()) {

                Properties properties = new Properties();

                try (FileReader fr = new FileReader(configFile)) {

                    properties.load(fr);
                    parseProperties(properties);

                } catch (IOException e) {

                    e.printStackTrace();

                    resetToDefaults();

                }

            } else {

                resetToDefaults();

            }

        } else {

            resetToDefaults();

        }

    }

    public void save() {

        File userHomeDirectory = new File(System.getProperty("user.home"));
        File appConfigDirectory = new File(userHomeDirectory, ".chatter");

        if (!appConfigDirectory.exists()) {
            if (!appConfigDirectory.mkdirs()) {
                throw new IllegalStateException("Could not create configuration directory '" + appConfigDirectory.getAbsolutePath() + "'");
            }
        }

        File configFile = new File(appConfigDirectory, "config.properties");

        Properties properties = toProperties();

        try (FileWriter fw = new FileWriter(configFile)) {
            properties.store(fw, "");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Locale getUserLocale() {
        return userLocale;
    }

}
