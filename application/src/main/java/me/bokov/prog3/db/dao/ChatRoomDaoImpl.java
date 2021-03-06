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
import me.bokov.prog3.service.db.dao.ChatRoomDao;
import me.bokov.prog3.service.db.entity.ChatRoomEntity;

import java.sql.SQLException;

/**
 * The implementation of the DAO responsible for accessing chat room entities
 */
public class ChatRoomDaoImpl extends BaseDaoImpl<ChatRoomEntity, Long> implements ChatRoomDao {

    public ChatRoomDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ChatRoomEntity.class);
    }

    @Override
    public ChatRoomEntity getLobby() {

        try {

            return queryBuilder()
                    .where().eq("is_lobby", 1)
                    .queryForFirst();

        } catch (SQLException sqlEx) {

            throw new IllegalStateException(sqlEx);

        }

    }
}
