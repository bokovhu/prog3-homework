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

package me.bokov.prog3.service;

import com.j256.ormlite.support.ConnectionSource;
import me.bokov.prog3.service.db.dao.*;

/**
 * Interface for database access
 */
public interface Database {

    /**
     * Initializes the database connection, and creates DAOs and tables
     */
    void start();

    /**
     * Checks if the database is set up and running
     * @return true, if the database is running, false otherwise
     */
    boolean isRunning();

    /**
     * Destroys the database connection and stops the database service
     */
    void stop();

    /**
     * Retrieve the ORMLite connection source
     * @return the ORMLite connection source
     */
    ConnectionSource getConnectionSource();

    /**
     * Retrieve the database access object used to access chat users
     * @return the DAO for chat users
     */
    ChatUserDao getChatUserDao();

    /**
     * Retrieve the database access object used to access chat rooms
     * @return the DAO for chat rooms
     */
    ChatRoomDao getChatRoomDao();

    /**
     * Retrieve the database access object used to access chat room memberships
     * @return the DAO for chat room memberships
     */
    ChatRoomMembershipDao getChatRoomMembershipDao();

    /**
     * Retrieve the database access object used to access chat invitations
     * @return the DAO for chat invitations
     */
    ChatInvitationDao getChatInvitationDao ();

    /**
     * Retrieve the database access object used to access chat messages
     * @return the DAO for chat messages
     */
    ChatMessageDao getChatMessageDao ();

}
