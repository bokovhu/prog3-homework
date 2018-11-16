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

import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

@ApplicationScoped
public class Config {

    @Inject
    private Logger logger;

    private boolean appConfigDirectoryInitialized = false;
    private File appConfigDirectory;

    private Locale userLocale;
    private String lastDatabaseMigration;

    public void resetToDefaults() {

        userLocale = new Locale("en");
        lastDatabaseMigration = null;

    }

    private void parseProperties(Properties properties) {

        userLocale = new Locale(properties.getProperty("user-locale"));
        lastDatabaseMigration = properties.getProperty("last-database-migration");

    }

    private Properties toProperties() {

        Properties properties = new Properties();

        properties.setProperty("user-locale", userLocale.getLanguage());
        properties.setProperty("last-database-migration", lastDatabaseMigration);

        return properties;

    }

    private void ensureAppConfigDirectory () {

        if (!appConfigDirectoryInitialized) {

            File userHomeDirectory = new File(System.getProperty("user.home"));
            appConfigDirectory = new File(userHomeDirectory, ".chatter");

            if (!appConfigDirectory.exists()) {

                if (!appConfigDirectory.mkdir()) {
                    throw new IllegalStateException("Cannot create application configuration directory!");
                }

            }

            appConfigDirectoryInitialized = true;

        }

    }

    public File getAppConfigDirectory () {
        ensureAppConfigDirectory();
        return appConfigDirectory;
    }

    public void load() {

        logger.info("Loading configuration from file system");

        File configFile = new File(getAppConfigDirectory(), "config.properties");

        if (configFile.exists()) {

            logger.info("Config file exists");

            Properties properties = new Properties();

            try (FileReader fr = new FileReader(configFile)) {

                properties.load(fr);
                parseProperties(properties);

            } catch (IOException e) {

                e.printStackTrace();

                resetToDefaults();

            }

        } else {

            logger.info("Config file does not exist, resetting to defaults");

            resetToDefaults();

        }

    }

    public void save() {

        logger.info("Saving configuration to file system");

        File configFile = new File(getAppConfigDirectory(), "config.properties");

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

    public String getLastDatabaseMigration() {
        return lastDatabaseMigration;
    }

    public void setLastDatabaseMigration(String lastDatabaseMigration) {
        this.lastDatabaseMigration = lastDatabaseMigration;
    }

}
