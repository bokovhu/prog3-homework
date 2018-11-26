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
 * Represents a {@code SEND-IMAGE} command.
 *
 * This command is used to send an image message to a chat room
 */
public interface SendImageCommand extends Command {

    int SUCCESS = 200;
    int INVALID_ROOM_ID = 400;
    int EXTENSION_REQUIRED = 401;
    int INVALID_EXTENSION = 402;
    int IMAGE_CONTENT_REQUIRED = 403;

    /**
     * Sets the file extension of the image (eg. PNG, JPG, etc.) in the command
     * @param extension the file extension of the image
     * @return this command instance
     */
    SendImageCommand extension (String extension);

    /**
     * Sets the ID of the room to which this message is to be sent in the command
     * @param roomId the ID of the room to which this message is to be sent
     * @return this command instance
     */
    SendImageCommand roomId (Long roomId);

    /**
     * Sets the binary image content, encoded in Base64 in the command
     * @param contentBase64 the binary image content, encoded in Base64
     * @return this command instance
     */
    SendImageCommand imageContent (String contentBase64);

}
