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
 * Represents a {@code SEND-MESSAGE} command.
 *
 * This command is used to send simple text messages to chat rooms.
 */
public interface SendMessageCommand extends Command {

    int SUCCESS = 200;
    int INVALID_ROOM_ID = 400;
    int MESSAGE_TEXT_REQUIRED = 401;

    /**
     * Sets the text of the message in the command
     * @param messageText the text of the message
     * @return this command instance
     */
    SendMessageCommand messageText (String messageText);

    /**
     * Sets the ID of the room to which this message is to be sent to in the command
     * @param roomId the ID of the room to which this message is to be sent to
     * @return this command instance
     */
    SendMessageCommand roomId (Long roomId);

}
