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

package me.bokov.prog3.server;

import me.bokov.prog3.command.endpoint.ChatClientEndpoint;
import me.bokov.prog3.command.endpoint.ConnectionInformation;
import me.bokov.prog3.command.server.NewInvitationCommand;
import me.bokov.prog3.command.server.NewMessageCommand;
import me.bokov.prog3.command.server.RoomDeletedCommand;
import me.bokov.prog3.command.server.YouAreBannedCommand;

public class ChatClientEndpointImpl implements ChatClientEndpoint {

    private final ConnectedChatClientImpl chatClient;
    private final ConnectionInformation connectionInformation;

    public ChatClientEndpointImpl(ConnectedChatClientImpl chatClient, ConnectionInformation connectionInformation) {
        this.chatClient = chatClient;
        this.connectionInformation = connectionInformation;
    }


    @Override
    public YouAreBannedCommand youAreBanned() {
        throw new UnsupportedOperationException("Not yet supported!");
    }

    @Override
    public RoomDeletedCommand roomDeleted() {
        throw new UnsupportedOperationException("Not yet supported!");
    }

    @Override
    public NewMessageCommand newMessage() {
        throw new UnsupportedOperationException("Not yet supported!");
    }

    @Override
    public NewInvitationCommand newInvitation() {
        throw new UnsupportedOperationException("Not yet supported!");
    }

    @Override
    public void close() {
        chatClient.stop();
    }

    @Override
    public ConnectionInformation getConnectionInformation() {
        return connectionInformation;
    }
}
