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

import me.bokov.prog3.db.Database;
import me.bokov.prog3.ui.ApplicationUIBean;
import me.bokov.prog3.ui.ErrorUIBean;
import me.bokov.prog3.util.Config;
import me.bokov.prog3.util.I18N;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;

public class Application {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Weld weld;
    private WeldContainer weldContainer;

    private static Application INSTANCE = null;

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
            CDI.current().select(Database.class).get().init();

            // Create UI
            CDI.current().select(ApplicationUIBean.class).get().initialize();

        } catch (Throwable th) {

            logger.error("Error during application initialization", th);

            CDI.current().select(ErrorUIBean.class).get()
                    .showThrowable(th);

        }

        logger.info("Application initialization finished");

    }

    static void createInstance() {
        INSTANCE = new Application();
        INSTANCE.initialize();
    }

    public static Application getInstance() {
        return INSTANCE;
    }

    public Weld getWeld() {
        return weld;
    }

    public WeldContainer getWeldContainer() {
        return weldContainer;
    }

}
