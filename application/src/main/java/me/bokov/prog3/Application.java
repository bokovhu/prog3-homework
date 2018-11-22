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

import me.bokov.prog3.db.DatabaseImpl;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.ui.ApplicationUIBean;
import me.bokov.prog3.ui.ErrorUIBean;
import me.bokov.prog3.util.Config;
import me.bokov.prog3.util.I18N;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;

/**
 * Handles initialization of the CDI container, as well as the core modules of the application, including the
 * application configuration, internationalization (UI translations), database connection and the application's
 * graphical user interface
 */
public class Application {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Weld weld;
    private WeldContainer weldContainer;

    private static Application INSTANCE = null;

    /**
     * Initializes the core modules of the application
     */
    private void initialize() {

        logger.info("Application initialization started");

        weld = new Weld();
        weldContainer = weld.initialize();

        try {

            // Load config
            CDI.current().select(Config.class).get().load();

            // Load i18n
            CDI.current().select(I18N.class).get().load(CDI.current().select(Config.class).get().getUserLocale());

            // Initialize database
            CDI.current().select(Database.class).get().start();

            // Create UI
            CDI.current().select(ApplicationUIBean.class).get().initialize();

        } catch (Throwable th) {

            logger.error("Error during application initialization", th);

            CDI.current().select(ErrorUIBean.class).get()
                    .showThrowable(th);

        }

        logger.info("Application initialization finished");

    }

    /**
     * Creates the singleton instance
     */
    static void createInstance() {
        INSTANCE = new Application();
        INSTANCE.initialize();
    }

    /**
     *
     * @return the singleton instance
     */
    public static Application getInstance() {
        return INSTANCE;
    }

    /**
     *
     * @return the Weld SE instance
     */
    public Weld getWeld() {
        return weld;
    }

    /**
     *
     * @return the WELD SE CDI container
     */
    public WeldContainer getWeldContainer() {
        return weldContainer;
    }

}
