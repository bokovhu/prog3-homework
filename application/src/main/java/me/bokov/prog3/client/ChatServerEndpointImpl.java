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

package me.bokov.prog3.client;

import me.bokov.prog3.client.command.DisconnectCommandImpl;
import me.bokov.prog3.client.command.HelloCommandImpl;
import me.bokov.prog3.command.client.CreateRoomCommand;
import me.bokov.prog3.command.client.DisconnectCommand;
import me.bokov.prog3.command.client.HelloCommand;
import me.bokov.prog3.command.client.LoginCommand;
import me.bokov.prog3.command.endpoint.ChatServerEndpoint;
import me.bokov.prog3.command.endpoint.ConnectionInformation;
import me.bokov.prog3.common.ClientBase;
import me.bokov.prog3.common.EndpointBase;

public class ChatServerEndpointImpl extends EndpointBase implements ChatServerEndpoint {

    public ChatServerEndpointImpl(ClientBase client, ConnectionInformation connectionInformation) {
        super(client, connectionInformation);
    }

    @Override
    public HelloCommand hello() {
        return new HelloCommandImpl(client);
    }

    @Override
    public LoginCommand login() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateRoomCommand createRoom() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DisconnectCommand disconnect() {
        return new DisconnectCommandImpl(client);
    }
}
