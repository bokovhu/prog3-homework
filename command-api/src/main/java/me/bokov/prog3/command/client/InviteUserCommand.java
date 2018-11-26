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

package me.bokov.prog3.command.client;

import me.bokov.prog3.command.Command;

/**
 * Represents an {@code INVITE-USER} command.
 *
 * This command can be used to send invitations to other users
 */
public interface InviteUserCommand extends Command {

    int SUCCESS = 200;
    int USER_NOT_FOUND = 400;
    int INVALID_ROOM_ID = 401;
    int INVITED_USERNAME_REQUIRED = 402;
    int ROOM_ID_REQUIRED = 403;

    /**
     * Sets the username of the user to be invited in the command
     * @param username the username of the user to be invited
     * @return this command instance
     */
    InviteUserCommand invitedUsername (String username);

    /**
     * Sets the ID of the room to which the user is invited in the command
     * @param roomId the ID of the room
     * @return this command instance
     */
    InviteUserCommand roomId (Long roomId);

}
