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

@ApplicationScoped
public class Database {

    private static final String MIGRATIONS_BASENAME = "/me/bokov/prog3/sql/ddl/";

    private boolean initialized = false;

    @Inject
    private Config config;

    private Sql2o sql2o;

    private void migrate() {

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

        for (String migrationFileName : migrationFileNames) {

            if (config.getLastDatabaseMigration() == null || migrationFileName.compareTo(config.getLastDatabaseMigration()) > 0) {

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

    public void init() {

        if (!initialized) {

            File databaseFile = new File(config.getAppConfigDirectory(), "chatter.db");

            sql2o = new Sql2o(
                    "jdbc:h2:" + databaseFile.getAbsolutePath(),
                    "sa",
                    ""
            );

            migrate();

            initialized = true;

        } else {

            throw new IllegalStateException("Already initialized!");

        }

    }

    public Sql2o getSql2o() {
        return sql2o;
    }

}
