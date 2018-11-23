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

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.client.HelloCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.event.UserConnectedEvent;
import me.bokov.prog3.server.ChatClientMessageHandlingContext;
import me.bokov.prog3.server.ClientCommandHandlerBean;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class HelloCommandHandlerBean implements ClientCommandHandlerBean {

    @Inject
    private Database database;

    @Inject
    private Event <UserConnectedEvent> userConnectedEvent;

    @Inject
    private ChatServer chatServer;

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

            boolean isUserBanned = database.getChatUserDao().queryBuilder()
                    .where()
                    .eq("username", username)
                    .and()
                    .ne("ban_state", "NOT_BANNED")
                    .countOf() > 0L;

            if (isUserBanned) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.BANNED)
                        .build();
            }

            if (ctx.getChatClient().isSessionValueSet("username")) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.ALREADY_SAID_HELLO)
                        .build();
            }

            boolean userExistsByUsername = database.getChatUserDao().queryBuilder()
                    .where().eq("username", username).countOf() > 0L;

            ChatUserEntity chatUserEntity = null;

            if (!userExistsByUsername) {

                chatUserEntity = new ChatUserEntity();
                chatUserEntity.setUsername(username);
                chatUserEntity.setBanState("NOT_BANNED");
                database.getChatUserDao().create(chatUserEntity);

            } else {

                chatUserEntity = database.getChatUserDao().queryForEq("username", username)
                        .get(0);

            }

            Long userId = chatUserEntity.getId();

            if (chatServer.getServerConfiguration().isPasswordEnabled()) {
                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.LOGIN_REQUIRED)
                        .build();
            } else {

                userConnectedEvent.fire(
                        new UserConnectedEvent(
                                new Date(),
                                "USER_CONNECTED",
                                username,
                                ctx.getChatClient().getClientEndpoint().getConnectionInformation(),
                                userId
                        )
                );

                return ResponseBuilder.create()
                        .messageId(req.getMessageId())
                        .code(HelloCommand.SUCCESS)
                        .build();
            }

        };

    }

}
