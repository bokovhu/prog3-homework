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

import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.client.HelloCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ChatClientMessageHandlingContext;
import me.bokov.prog3.server.ClientCommandHandlerBean;
import me.bokov.prog3.server.dao.UserDao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class HelloCommandHandlerBean implements ClientCommandHandlerBean {

    @Inject
    private UserDao userDao;

    @Override
    public Set<String> getHandledCommands() {
        return new HashSet<>(
                Arrays.asList("HELLO")
        );
    }

    @Override
    public CommandHandler <ChatClientMessageHandlingContext> getMessageHandler() {

        return (ctx, req) -> {

            if (!(req.getData() instanceof JsonObject)) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(999)
                        .build();
            }

            JsonObject json = req.getData().asJsonObject();

            if (!json.containsKey("username")) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(999)
                        .build();
            }

            String userIpAddress = ctx.getChatClient()
                    .getClientEndpoint()
                    .getConnectionInformation()
                    .getRemoteAddress();

            String username = json.getString("username");

            if (userDao.isUserBanned(userIpAddress, username)) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.BANNED)
                        .build();
            }

            userDao.ensureUser(username);

            if (ctx.getChatServer().getServerConfig().isPasswordEnabled()) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.LOGIN_REQUIRED)
                        .build();
            } else {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.SUCCESS)
                        .build();
            }

        };

    }

}
