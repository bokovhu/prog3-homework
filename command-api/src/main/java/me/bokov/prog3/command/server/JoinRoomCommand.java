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

package me.bokov.prog3.command.server;

import me.bokov.prog3.command.Command;

import javax.json.JsonObject;

/**
 * Represents a {@code JOIN-ROOM} command.
 *
 * This command is sent by the server to indicate to the client that it should join a given chat room
 */
public interface JoinRoomCommand extends Command {

    int SUCCESS = 200;

    /**
     * Sets the room ID to join into in the command
     * @param roomId the ID of the room to join
     * @return this command instance
     */
    JoinRoomCommand roomId (Long roomId);

}
