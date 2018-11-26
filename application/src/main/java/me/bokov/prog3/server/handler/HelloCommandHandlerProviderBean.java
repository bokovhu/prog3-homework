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

import me.bokov.prog3.command.Command;
import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.client.HelloCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.event.UserConnectedEvent;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Arrays;
import java.util.Collection;

@ApplicationScoped
public class HelloCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Inject
    private Event<UserConnectedEvent> userConnectedEvent;

    @Inject
    private ChatServer chatServer;

    @Override
    public Collection<String> getHandledCommands() {

        return Arrays.asList("HELLO");

    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {

        return (ctx, req) -> {

            if (req.getData() == null || req.getData().getValueType() != JsonValue.ValueType.OBJECT) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(Command.INVALID)
                        .build();
            }

            JsonObject json = req.getData().asJsonObject();

            if (!json.containsKey("username") || json.isNull("username") || json.getString("username").isEmpty()) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.USERNAME_REQUIRED)
                        .build();
            }

            String userIpAddress = ctx.getChatClient()
                    .getClientEndpoint()
                    .getConnectionInformation()
                    .getRemoteAddress();

            String username = json.getString("username");

            boolean isUserBanned = database.getChatUserDao().queryBuilder()
                    .where()
                    .eq("username", username)
                    .and()
                    .ne("ban_state", "NOT_BANNED")
                    .countOf() > 0L;

            if (isUserBanned) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(Command.BANNED)
                        .build();
            }

            if (ctx.getChatClient().isSessionValueSet("username")) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.ALREADY_SAID_HELLO)
                        .build();
            }

            ctx.getChatClient().setSessionValue("username", username);

            boolean userExistsByUsername = database.getChatUserDao().queryBuilder()
                    .where().eq("username", username).countOf() > 0L;

            ChatUserEntity chatUserEntity = null;

            if (!userExistsByUsername) {

                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.FIRST_TIME)
                        .build();

            }

            return ResponseBuilder.create()
                    .messageId(req.getMessageId())
                    .code(HelloCommand.CONTINUE)
                    .build();
        };

    }

}
