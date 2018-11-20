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

package me.bokov.prog3.db;

import me.bokov.prog3.util.Config;
import org.slf4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This @ApplicationScoped CDI bean manages the database connection of the application.
 *
 * The database used in the app is an embedded H2 database, the database file is located in the
 * {USER HOME}/.chatter/chatter.db file.
 *
 * When initialized, the bean also performs database migrations. These migrations are simple .SQL files, that are stored
 * on the classpath. The order of the migrations is determined by the natural order of the name of the migration files.
 */
@ApplicationScoped
public class Database {

    @Inject
    private Logger logger;

    private static final String MIGRATIONS_BASENAME = "/me/bokov/prog3/sql/ddl/";

    private boolean initialized = false;

    @Inject
    private Config config;

    private Sql2o sql2o;

    /**
     * Performs the database migration via first scanning for migration SQL files on the classpath, then determining
     * which migrations to run, and lastly it runs the SQL code found in migration files, and sets the last migration
     * file name in the configuration after each successful migration.
     */
    private void migrate() {

        logger.info("Migrating database");

        List<String> migrationFileNames = new ArrayList<>();

        try {

            try (InputStreamReader isr = new InputStreamReader(Database.class.getResourceAsStream(MIGRATIONS_BASENAME));
                 BufferedReader br = new BufferedReader(isr)) {

                String line = null;

                while ((line = br.readLine()) != null) migrationFileNames.add(line.trim());

            }

        } catch (Exception exc) {

            throw new IllegalStateException("Could not run database migrations!", exc);

        }

        Collections.sort(migrationFileNames);

        logger.info("Discovered the following migration files: {}", migrationFileNames);

        for (String migrationFileName : migrationFileNames) {

            if (config.getLastDatabaseMigration() == null || migrationFileName.compareTo(config.getLastDatabaseMigration()) > 0) {

                logger.info("Running migration {}", migrationFileName);

                String sql = "";

                try (InputStreamReader isr = new InputStreamReader(Database.class.getResourceAsStream(MIGRATIONS_BASENAME + migrationFileName));
                     BufferedReader br = new BufferedReader(isr)) {

                    String line = null;
                    while ((line = br.readLine()) != null) sql += line + "\n";

                } catch (Exception exc) {

                    throw new IllegalStateException("Could not read migration file '" + migrationFileName + "'", exc);

                }

                try (Connection connection = sql2o.open()) {

                    connection.createQuery(sql)
                            .executeUpdate();

                }

                config.setLastDatabaseMigration(migrationFileName);
                config.save();

            }

        }

    }

    /**
     * Initializes the database.
     *
     * If the database was already initialized, an IllegalStateException is thrown
     *
     * @throws IllegalStateException if the database was already initialized
     */
    public void init() {

        logger.info("Initializing database");

        if (!initialized) {

            File databaseFile = new File(config.getAppConfigDirectory(), "chatter.db");

            sql2o = new Sql2o(
                    "jdbc:h2:" + databaseFile.getAbsolutePath(),
                    "sa",
                    ""
            );

            migrate();

            initialized = true;

            logger.info("Initialization successful");

        } else {

            logger.warn("Database is already initialized!");

            throw new IllegalStateException("Already initialized!");

        }

    }

    public Sql2o getSql2o() {
        return sql2o;
    }

}
