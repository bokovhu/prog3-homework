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

package me.bokov.prog3.service;

import me.bokov.prog3.command.endpoint.ChatServerEndpoint;
import me.bokov.prog3.service.client.ConnectionConfiguration;
import me.bokov.prog3.service.common.CommunicationCapableService;
import me.bokov.prog3.service.common.SessionCapableService;

public interface ChatClient extends SessionCapableService, CommunicationCapableService {

    enum ConnectResult {

        PROCEED_WITH_LOGIN,
        PROCEED_WITH_REGISTRATION,
        BANNED

    }

    ConnectResult connect(ConnectionConfiguration configuration);

    boolean isConnected();

    void disconnect();

    ChatServerEndpoint getServerEndpoint();

}
