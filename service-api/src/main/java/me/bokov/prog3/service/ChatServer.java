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

import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.service.server.ServerConfiguration;

import java.util.List;

public interface ChatServer {

    void start(ServerConfiguration configuration);

    boolean isRunning();

    ServerConfiguration getServerConfiguration();

    void stop();

    List<ChatUserVO> getConnectedUsers();

    void addConnectedUser(ChatUserVO user);

    void removeConnectedUser(ChatUserVO user);

    void broadcast(Request request);

    void banUserByUsername(Long userId);

    void banUserByIp(Long userId);

    void unbanUser(Long userId);

}
