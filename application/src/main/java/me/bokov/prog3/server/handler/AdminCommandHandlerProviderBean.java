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

import me.bokov.prog3.Application;
import me.bokov.prog3.command.Command;
import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.client.AdminCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.event.UserAdministrationEvent;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.service.db.entity.ChatUserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides a command handler for the {@code ADMIN} command
 */
@ApplicationScoped
public class AdminCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private ChatServer chatServer;

    @Inject
    private Database database;

    @Inject
    private Event <UserAdministrationEvent> adminEvent;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("ADMIN");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            if (context.getChatClient().isBanned()) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(Command.BANNED)
                        .build();
            }

            if (request.getData() == null || request.getData().getValueType() != JsonValue.ValueType.OBJECT) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(Command.INVALID)
                        .build();
            }

            JsonObject json = request.getData().asJsonObject();

            if (!json.containsKey("method") || !json.containsKey("params")
                    || json.isNull("method") || json.isNull("params")) {
                return ResponseBuilder.create().messageId(request.getMessageId())
                        .code(Command.INVALID)
                        .build();
            }

            String method = json.getString("method");
            JsonObject params = json.getJsonObject("params");

            boolean adminAuthenticated = context.getChatClient().isSessionValueSet("adminAuthenticated")
                    && (Boolean) context.getChatClient().getSessionValue("adminAuthenticated");

            ChatUserEntity user = null;

            switch (method) {
                case "AUTHENTICATE":

                    if (params.getString("password").equals(chatServer.getServerConfiguration().getAdminPassword())) {

                        adminAuthenticated = true;
                        context.getChatClient().setSessionValue("adminAuthenticated", true);

                    } else {
                        return ResponseBuilder.create()
                                .messageId(request.getMessageId())
                                .code(AdminCommand.INVALID_PASSWORD)
                                .build();
                    }

                    break;
                case "BAN_USER":

                    if (!adminAuthenticated) {
                        return ResponseBuilder.create()
                                .messageId(request.getMessageId())
                                .code(AdminCommand.NOT_AUTHENTICATED)
                                .build();
                    }

                    chatServer.banUser(params.getJsonNumber("userId").longValue());

                    break;
                case "UNBAN_USER":

                    if (!adminAuthenticated) {
                        return ResponseBuilder.create()
                                .messageId(request.getMessageId())
                                .code(AdminCommand.NOT_AUTHENTICATED)
                                .build();
                    }

                    chatServer.unbanUser(params.getJsonNumber("userId").longValue());

                    break;
                case "GET_ALL_USERS":

                    if (!adminAuthenticated) {
                        return ResponseBuilder.create()
                                .messageId(request.getMessageId())
                                .code(AdminCommand.NOT_AUTHENTICATED)
                                .build();
                    }

                    JsonArrayBuilder jab = Json.createArrayBuilder();

                    database.getChatUserDao()
                            .queryForAll()
                            .stream()
                            .map(ChatUserEntity::toVo)
                            .map(ChatUserVO::toJson)
                            .forEach(jab::add);

                    return ResponseBuilder.create()
                            .messageId(request.getMessageId())
                            .code(AdminCommand.SUCCESS)
                            .data(
                                    Json.createObjectBuilder()
                                            .add("users", jab.build() ).build()
                            )
                            .build();

                default:
                    return ResponseBuilder.create().messageId(request.getMessageId())
                            .code(Command.INVALID)
                            .build();

            }

            adminEvent.fire(new UserAdministrationEvent());

            return ResponseBuilder.create().messageId(request.getMessageId())
                    .code(AdminCommand.SUCCESS)
                    .build();

        };
    }
}
