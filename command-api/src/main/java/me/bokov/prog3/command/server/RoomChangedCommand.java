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

/**
 * Represents a {@code ROOM-CHANGED} command.
 *
 * This command indicates to the client that it should refetch data (using the {@code GET-ROOM} command) of a given
 * chat room, because something has changed in it
 */
public interface RoomChangedCommand extends Command {

    int SUCCESS = 200;

    /**
     * Sets the ID of the room that changed in the command
     * @param roomId the ID of the room that changed
     * @return this command instance
     */
    RoomChangedCommand roomId (Long roomId);

}
