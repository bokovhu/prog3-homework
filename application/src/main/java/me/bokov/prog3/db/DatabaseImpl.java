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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.H2DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.bokov.prog3.db.dao.ChatRoomDaoImpl;
import me.bokov.prog3.db.dao.ChatRoomMembershipDaoImpl;
import me.bokov.prog3.db.dao.ChatUserDaoImpl;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.dao.ChatRoomDao;
import me.bokov.prog3.service.db.dao.ChatRoomMembershipDao;
import me.bokov.prog3.service.db.dao.ChatUserDao;
import me.bokov.prog3.service.db.entity.ChatRoomEntity;
import me.bokov.prog3.service.db.entity.ChatRoomMembershipEntity;
import me.bokov.prog3.service.db.entity.ChatUserEntity;
import me.bokov.prog3.util.Config;
import me.bokov.prog3.util.DatabaseConnectionConfig;
import org.slf4j.Logger;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * This @ApplicationScoped CDI bean manages the database connection of the application.
 *
 * The database used in the app is an embedded H2 database, the database file is located in the
 * {USER HOME}/.chatter/chatter.db file.
 *
 * When running, the bean also performs database migrations. These migrations are simple .SQL files, that are stored
 * on the classpath. The order of the migrations is determined by the natural order of the name of the migration files.
 */
@ApplicationScoped
public class DatabaseImpl implements Database {

    @Inject
    private Logger logger;

    private static final String MIGRATIONS_BASENAME = "/me/bokov/prog3/sql/ddl/";

    private boolean running = false;

    @Inject
    private Config config;

    private ChatUserDao chatUserDao;
    private ChatRoomDao chatRoomDao;
    private ChatRoomMembershipDao chatRoomMembershipDao;

    private ConnectionSource connectionSource;

    private void migrateDatabase () {

        try {

            TableUtils.createTableIfNotExists(connectionSource, ChatUserEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, ChatRoomEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, ChatRoomMembershipEntity.class);

        } catch (Exception e) {
            throw new IllegalStateException("Could not migrate database", e);
        }

    }

    private void initDaos () {

        try {

            chatUserDao = new ChatUserDaoImpl(connectionSource);
            chatRoomDao = new ChatRoomDaoImpl(connectionSource);
            chatRoomMembershipDao = new ChatRoomMembershipDaoImpl(connectionSource);

            DaoManager.registerDao(connectionSource, chatUserDao);
            DaoManager.registerDao(connectionSource, chatRoomDao);
            DaoManager.registerDao(connectionSource, chatRoomMembershipDao);

        } catch (Exception e) {

            throw new IllegalStateException("Could not initialize DAOs", e);

        }

    }

    /**
     * Initializes the database.
     *
     * If the database was already running, an IllegalStateException is thrown
     *
     * @throws IllegalStateException if the database was already running
     */
    public void start() {

        logger.info("Initializing database");

        if (!running) {

            DatabaseConnectionConfig dbCfg = config.getDatabaseConnectionConfig();

            try {

                connectionSource = new JdbcConnectionSource(
                        dbCfg.getJdbcUrl(),
                        dbCfg.getJdbcUsername(),
                        dbCfg.getJdbcPassword(),
                        new H2DatabaseType()
                );

                migrateDatabase();
                initDaos();

                running = true;

                logger.info("Initialization successful");

            } catch (Exception e) {

                throw new IllegalStateException("Could not initialize database", e);

            }

        } else {

            logger.warn("DatabaseImpl is already running!");

            throw new IllegalStateException("Already running!");

        }

    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {

        try {
            connectionSource.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        running = false;

    }

    @Override
    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    @Override
    public ChatUserDao getChatUserDao() {
        return chatUserDao;
    }

    @Override
    public ChatRoomDao getChatRoomDao() {
        return chatRoomDao;
    }

    @Override
    public ChatRoomMembershipDao getChatRoomMembershipDao() {
        return chatRoomMembershipDao;
    }

}
