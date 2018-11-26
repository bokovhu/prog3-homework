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
 * Represents a {@code SEND-FILE} command.
 *
 * This command is used to send a file message to a chat room
 */
public interface SendFileCommand extends Command {

    int SUCCESS = 200;
    int INVALID_ROOM_ID = 400;
    int FILE_NAME_REQUIRED = 401;
    int FILE_CONTENT_REQUIRED = 402;

    /**
     * Sets the name of the file to be sent in the command
     * @param fileName the name of the file to be sent
     * @return this command instance
     */
    SendFileCommand fileName (String fileName);

    /**
     * Sets the ID of the room to which this message is sent in the command
     * @param roomId the ID of the room to which this message is sent
     * @return this command instance
     */
    SendFileCommand roomId (Long roomId);

    /**
     * Sets the binary content of the file to be sent, encoded in Base64 in the command
     * @param contentBase64 the binary content of the file, encoded in Base64
     * @return this command instance
     */
    SendFileCommand fileContent (String contentBase64);

}
