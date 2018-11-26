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

package me.bokov.prog3.common;

import me.bokov.prog3.command.PingCommand;
import me.bokov.prog3.command.endpoint.ConnectionInformation;
import me.bokov.prog3.command.endpoint.Endpoint;

/**
 * Abstract base class for both the client and server endpoints
 */
public abstract class EndpointBase implements Endpoint {

    protected final ClientBase client;
    protected final ConnectionInformation connectionInformation;

    protected EndpointBase(ClientBase client, ConnectionInformation connectionInformation) {
        this.client = client;
        this.connectionInformation = connectionInformation;
    }

    @Override
    public void close() {
        client.stop();
    }

    @Override
    public ConnectionInformation getConnectionInformation() {
        return connectionInformation;
    }

    @Override
    public PingCommand ping() {
        return new PingCommandImpl(client);
    }
}
