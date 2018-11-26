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

import me.bokov.prog3.Application;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * This @ApplicationScoped CDI bean stores configuration for the application
 */
@ApplicationScoped
public class Config {

    @Inject
    private Logger logger;

    private boolean appConfigDirectoryInitialized = false;
    private File appConfigDirectory;

    private Locale userLocale;
    private String lastDatabaseMigration;

    private Map<String, String> configurationMap = new HashMap<>();

    /**
     * Resets configurable options to their default values
     */
    public void resetToDefaults() {

        userLocale = new Locale("en");
        lastDatabaseMigration = null;

    }

    /**
     * Parses configuration options from a Properties object
     * @param properties the parse source
     */
    private void parseProperties(Properties properties) {

        userLocale = new Locale(properties.getProperty("user-locale"));
        lastDatabaseMigration = properties.getProperty("last-database-migration");

        properties.stringPropertyNames()
                .forEach(k -> configurationMap.put(k, properties.getProperty(k)));

    }

    /**
     * Converts the configuration options to a Properties object
     * @return the result of the conversion
     */
    private Properties toProperties() {

        Properties properties = new Properties();

        properties.setProperty("user-locale", userLocale.getLanguage());
        properties.setProperty("last-database-migration", lastDatabaseMigration);

        configurationMap.forEach(properties::setProperty);

        return properties;

    }

    /**
     * This method ensures that the application configuration directory is a valid directory, and exists. If the
     * configuration directory does not exist, it attempts to create it. Failing to do so will result in an
     * IllegalStateException being thrown
     *
     * @throws IllegalStateException when the application configuration directory could not be created
     */
    private void ensureAppConfigDirectory() {

        if (!appConfigDirectoryInitialized) {

            if (Application.getInstance().getCommandLine().hasOption("data-directory")) {

                appConfigDirectory = new File(Application.getInstance().getCommandLine().getOptionValue("data-directory"));

            } else {

                File userHomeDirectory = new File(System.getProperty("user.home"));
                appConfigDirectory = new File(userHomeDirectory, ".chatter");

            }

            if (!appConfigDirectory.exists()) {

                if (!appConfigDirectory.mkdir()) {
                    throw new IllegalStateException("Cannot create application configuration directory!");
                }

            }

            appConfigDirectoryInitialized = true;

        }

    }

    /**
     * This method can be used to retrieve the current application configuration directory. The method first ensures
     * that this directory exists, so that caller can safely use it given the method returns without throwing an
     * exception
     * @return the application configuration directory
     */
    public File getAppConfigDirectory() {
        ensureAppConfigDirectory();
        return appConfigDirectory;
    }

    /**
     * Loads the configuration options from the file system
     */
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

    /**
     * Saves the configuration options to the file system
     */
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

    public DatabaseConnectionConfig getDatabaseConnectionConfig() {

        DatabaseConnectionConfig cfg = new DatabaseConnectionConfig();

        if (configurationMap.containsKey("db-jdbc-url")
                && configurationMap.containsKey("db-jdbc-username")
                && configurationMap.containsKey("db-jdbc-password")) {

            cfg.setJdbcUrl(configurationMap.get("db-jdbc-url"));
            cfg.setJdbcUsername(configurationMap.get("db-jdbc-username"));
            cfg.setJdbcPassword(configurationMap.get("db-jdbc-password"));

        } else {

            File databaseFile = new File(getAppConfigDirectory(), "chatter.db");

            cfg.setJdbcUrl("jdbc:h2:" + databaseFile.getAbsolutePath());
            cfg.setJdbcUsername("sa");
            cfg.setJdbcPassword("");

        }

        return cfg;

    }

}
