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

import me.bokov.prog3.db.DatabaseImpl;
import me.bokov.prog3.event.UserBannedEvent;
import me.bokov.prog3.event.UserUnbannedEvent;
import me.bokov.prog3.service.ChatServer;
import org.slf4j.Logger;
import org.sql2o.Connection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final String SQL_INSERT_USER = "INSERT INTO chat_user (id, username, ban_state) VALUES (nextval ('id_seq'), :username, 'NOT_BANNED')";

    private static final String SQL_BAN_USER_BY_IP = "UPDATE chat_user " +
            "SET ban_state = 'BANNED_BY_IP' " +
            "WHERE id = :userId";

    private static final String SQL_BAN_USER_BY_USERNAME = "UPDATE chat_user " +
            "SET ban_state = 'BANNED_BY_USERNAME' " +
            "WHERE id = :userId";

    private static final String SQL_UNBAN_USER = "UPDATE chat_user " +
            "SET ban_state = 'NOT_BANNED' " +
            "WHERE id = :userId";

    private static final String SQL_GET_ALL_USERS = "SELECT * FROM chat_user";

    private static final String SQL_GET_USERNAME_BY_USER_ID = "SELECT username " +
            "FROM chat_user " +
            "WHERE id = :userId";

    @Inject
    private Logger logger;

    @Inject
    private DatabaseImpl database;

    @Inject
    private ChatServer chatServer;

    @Inject
    private Event <UserBannedEvent> userBannedEvent;

    @Inject
    private Event <UserUnbannedEvent> userUnbannedEvent;

    public void ensureUser(String username) {

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

    public Long getUserIdByUsername(String username) {

        logger.debug("UserDao.getUserIdByUsername ({})", username);

        try (Connection connection = database.getSql2o().open()) {

            return connection.createQuery(SQL_GET_USER_ID_BY_USERNAME)
                    .addParameter("username", username)
                    .executeScalar(Long.class);

        }

    }

    public boolean isUserBanned(String ipAddress, String username) {

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

    public void banUserByIp(Long userId) {

        if (true) {
            throw new UnsupportedOperationException();
        }

        try (Connection connection = database.getSql2o().open()) {

            connection.createQuery(SQL_BAN_USER_BY_IP)
                    .addParameter("userId", userId)
                    .executeUpdate();

            String username = getUsernameByUserId(userId);

            // chatManager.getCurrentRunningServer().disconnectClientByUsername(username);
            userBannedEvent.fire(
                    new UserBannedEvent(
                            new Date(),
                            "USER_BANNED",
                            username,
                            true,
                            false
                    )
            );

        }

    }

    public void banUserByUsername(Long userId) {

        if (true) {
            throw new UnsupportedOperationException();
        }

        try (Connection connection = database.getSql2o().open()) {

            connection.createQuery(SQL_BAN_USER_BY_USERNAME)
                    .addParameter("userId", userId)
                    .executeUpdate();

            String username = getUsernameByUserId(userId);

            // chatManager.getCurrentRunningServer().disconnectClientByUsername(username);

            userBannedEvent.fire(
                    new UserBannedEvent(
                            new Date(),
                            "USER_BANNED",
                            username,
                            false,
                            true
                    )
            );

        }

    }

    public void unbanUser(Long userId) {

        try (Connection connection = database.getSql2o().open()) {

            connection.createQuery(SQL_UNBAN_USER)
                    .addParameter("userId", userId)
                    .executeUpdate();

            userUnbannedEvent.fire(
                    new UserUnbannedEvent(
                            new Date(),
                            "USER_UNBANNED",
                            getUsernameByUserId(userId)
                    )
            );

        }

    }

    public List<JsonObject> getAllUsers() {

        try (Connection connection = database.getSql2o().open()) {

            return
                    connection.createQuery(SQL_GET_ALL_USERS)
                            .executeAndFetchTable()
                            .rows().stream()
                            .map(
                                    row -> Json.createObjectBuilder()
                                            .add("username", row.getString("username"))
                                            .add("banState", row.getString("ban_state"))
                                            .add("id", row.getLong("id"))
                                            .build()
                            ).collect(Collectors.toList());

        }

    }

    public String getUsernameByUserId (Long userId) {

        try (Connection connection = database.getSql2o().open()) {

            return connection.createQuery(SQL_GET_USERNAME_BY_USER_ID)
                    .addParameter("userId", userId)
                    .executeScalar(String.class);

        }

    }

}
