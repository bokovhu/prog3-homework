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

package me.bokov.prog3.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import me.bokov.prog3.service.db.dao.ChatUserDao;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import java.sql.SQLException;

/**
 * The implementation of the DAO responsible for accessing chat user entities
 */
public class ChatUserDaoImpl extends BaseDaoImpl<ChatUserEntity, Long> implements ChatUserDao {

    public ChatUserDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ChatUserEntity.class);
    }

    @Override
    public ChatUserEntity ensureUserByUsername(String username) throws SQLException {

        if (queryBuilder().where().eq("username", username).countOf() > 0L) {
            return queryBuilder().where().eq("username", username).queryForFirst();
        }

        ChatUserEntity user = new ChatUserEntity();

        user.setUsername(username);
        user.setBanned(false);

        create(user);

        return user;
    }

    @Override
    public boolean isUserBanned(Long id) throws SQLException {
        return queryBuilder().where().eq("id", id).and().eq("is_banned", true).countOf() > 0L;
    }
}
