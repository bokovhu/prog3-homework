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

package me.bokov.prog3.service.db.dao;

import com.j256.ormlite.dao.Dao;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import java.sql.SQLException;

/**
 * The DAO interface that is used to access chat user entities
 */
public interface ChatUserDao extends Dao<ChatUserEntity, Long> {

    /**
     * Ensures the existence of a given username.
     *
     * If the user exists, the function returns that already existing user.
     * If the user did not exist before, it gets created, and the newly created user is returned.
     * @param username the username to ensure
     * @return the user
     * @throws SQLException
     */
    ChatUserEntity ensureUserByUsername(String username) throws SQLException;

    /**
     * Checks whether a given user is banned.
     * @param id the ID of the user to check
     * @return True, if the user is banned, false otherwise
     * @throws SQLException
     */
    boolean isUserBanned(Long id) throws SQLException;

}
