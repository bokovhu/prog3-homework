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

package me.bokov.prog3.server.handler;

import me.bokov.prog3.net.command.client.HelloCommand;
import me.bokov.prog3.server.ChatClientMessageHandler;
import me.bokov.prog3.server.ClientMessageHandlerBean;
import me.bokov.prog3.server.dao.UserDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class HelloMessageHandlerBean implements ClientMessageHandlerBean {

    @Inject
    private UserDao userDao;

    @Override
    public Set<String> getHandledCommands() {
        return new HashSet<>(
                Arrays.asList("HELLO")
        );
    }

    @Override
    public ChatClientMessageHandler getMessageHandler() {

        return (client, messageId, command, data) -> {

            if (!(data instanceof JsonObject)) {
                client.sendResponse(messageId, 999, null);
                return;
            }

            JsonObject json = data.asJsonObject();

            if (!json.containsKey("username")) {
                client.sendResponse(messageId, 999, null);
                return;
            }

            if (userDao.isUserBanned(client.getClientEndpoint().getConnectionInformation().getRemoteAddress(), json.getString("username"))) {
                client.sendResponse(messageId, HelloCommand.BANNED, null);
                client.stop();
                return;
            }

            userDao.ensureUser(json.getString("username"));

            if (client.getChatServer().getServerConfig().isPasswordEnabled()) {
                client.sendResponse(messageId, HelloCommand.LOGIN_REQUIRED, null);
            } else {
                client.sendResponse(messageId, HelloCommand.SUCCESS, null);
            }

        };

    }

}
