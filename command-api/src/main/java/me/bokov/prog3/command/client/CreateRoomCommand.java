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
 * Represents a {@code CREATE-ROOM} command.
 *
 * This command can be used to create a new chat room
 */
public interface CreateRoomCommand extends Command {

    // TODO: Errors
    int SUCCESS = 200;
    int ROOM_NAME_REQUIRED = 400;

    /**
     * Sets the name of the room to be created
     * @param roomName the coom name
     * @return this command instance
     */
    CreateRoomCommand roomName(String roomName);

}
