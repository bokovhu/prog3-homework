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
import me.bokov.prog3.service.db.dao.ChatInvitationDao;
import me.bokov.prog3.service.db.entity.ChatInvitationEntity;

import java.sql.SQLException;

/**
 * The implementation of the DAO responsible for accessing invitation entities
 */
public class ChatInvitationDaoImpl extends BaseDaoImpl<ChatInvitationEntity, Long> implements ChatInvitationDao {

    public ChatInvitationDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ChatInvitationEntity.class);
    }

    @Override
    public ChatInvitationEntity getByInvitationId(String invitationId) {
        try {
            return queryBuilder().where().eq("invitation_id", invitationId).queryForFirst();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
