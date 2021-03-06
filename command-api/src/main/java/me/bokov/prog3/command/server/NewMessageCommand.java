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
 * Represents a {@code NEW-MESSAGE} command.
 *
 * This command is sent by the server to the client to notify it about a new message in a chat room
 */
public interface NewMessageCommand extends Command {

    int SUCCESS = 200;

    /**
     * Sets the ID of the room to which the message arrived in the command
     * @param roomId the ID of the room to which the message arrived
     * @return this command instance
     */
    NewMessageCommand roomId(String roomId);

    /**
     * Sets the JSON data of the message in the command
     * @param message the JSON data of the message
     * @return this command instance
     */
    NewMessageCommand message(JsonObject message);

}
