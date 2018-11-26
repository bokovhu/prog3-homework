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
 * Represents a {@code LEAVE-ROOM} command.
 *
 * This command is used to tell the server, that the client will leave a given chat room.
 */
public interface LeaveRoomCommand extends Command {

    int SUCCESS = 200;
    int INVALID_ROOM_ID = 400;
    int NOT_MEMBER = 401;

    /**
     * Sets the ID of the room to be left by the user in the command
     * @param roomId the ID of the room to be left by the user
     * @return this command instance
     */
    LeaveRoomCommand roomId (Long roomId);

}
