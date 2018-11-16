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

package me.bokov.prog3.server.dao;

import me.bokov.prog3.db.Database;
import org.slf4j.Logger;
import org.sql2o.Connection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserDao {

    private static final String SQL_CHECK_USER_BANNED_BY_IP = "SELECT COUNT (*) " +
            "FROM chat_user " +
            "WHERE ban_state = 'BANNED_BY_IP' " +
            "AND banned_ip = :ipAddress";

    private static final String SQL_CHECK_USER_BANNED_BY_USERNAME = "SELECT COUNT (*) " +
            "FROM chat_user " +
            "WHERE ban_state = 'BANNED_BY_USERNAME' " +
            "AND username = :username";

    private static final String SQL_CHECK_USER_EXISTS_BY_USERNAME = "SELECT COUNT (*) " +
            "FROM chat_user " +
            "WHERE username = :username";

    private static final String SQL_GET_USER_ID_BY_USERNAME = "SELECT id " +
            "FROM chat_user " +
            "WHERE username = :username";

    private static final String SQL_INSERT_USER = "INSERT INTO chat_user (id, username) VALUES (nextval ('id_seq'), :username)";

    @Inject
    private Logger logger;

    @Inject
    private Database database;

    public void ensureUser (String username) {

        logger.debug("UserDao.ensureUser ({})", username);

        try (Connection connection = database.getSql2o().open()) {

            Long existenceCheckResult = connection.createQuery(SQL_CHECK_USER_EXISTS_BY_USERNAME)
                    .addParameter("username", username)
                    .executeScalar(Long.class);

            if (existenceCheckResult.compareTo(0L) == 0) {

                connection.createQuery(SQL_INSERT_USER)
                        .addParameter("username", username)
                        .executeUpdate();

            }

        }

    }

    public Long getUserIdByUsername (String username) {

        logger.debug("UserDao.getUserIdByUsername ({})", username);

        try (Connection connection = database.getSql2o().open()) {

            return connection.createQuery(SQL_GET_USER_ID_BY_USERNAME)
                    .addParameter("username", username)
                    .executeScalar(Long.class);

        }

    }

    public boolean isUserBanned (String ipAddress, String username) {

        logger.debug("UserDao.isUserBanned ({}, {})", ipAddress, username);

        try (Connection connection = database.getSql2o().open()) {

            Long byIpBanCheckResult = connection.createQuery(SQL_CHECK_USER_BANNED_BY_IP)
                    .addParameter("ipAddress", ipAddress)
                    .executeScalar(Long.class);

            if (byIpBanCheckResult.compareTo(0L) != 0) return true;

            Long byUsernameCheckResult = connection.createQuery(SQL_CHECK_USER_BANNED_BY_USERNAME)
                    .addParameter("username", username)
                    .executeScalar(Long.class);

            if (byUsernameCheckResult.compareTo(0L) != 0) return true;

        }

        return false;

    }

}
