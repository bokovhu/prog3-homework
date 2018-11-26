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
import me.bokov.prog3.command.client.LoginCommand;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;
import me.bokov.prog3.service.Database;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Collection;

@ApplicationScoped
public class LoginCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Database database;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("LOGIN");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            if (!context.getChatClient().isSessionValueSet("username")) {
                return ResponseBuilder.create()
                        .messageId(request.getMessageId())
                        .code(999)
                        .build();
            }

            if (request.getData() == null) return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(999)
                    .build();

            JsonObject json = request.getData().asJsonObject();

            if (!json.containsKey("password")) return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(999)
                    .build();

            boolean passwordOk = database.getChatUserDao().queryBuilder()
                    .where().eq("username", context.getChatClient().getSessionValue("username"))
                    .and().eq("password", json.getString("password"))
                    .countOf() > 0L;

            if (passwordOk) {

                context.getChatClient().setSessionValue("authenticated", true);

                Long userId = database.getChatUserDao().queryBuilder()
                        .where().eq("username", context.getChatClient().getSessionValue("username"))
                        .queryForFirst().getId();

                context.getChatClient().setSessionValue("userId", userId);

                return ResponseBuilder.create()
                        .messageId(request.getMessageId())
                        .code(LoginCommand.SUCCESS)
                        .data(Json.createObjectBuilder().add("userId", userId).build())
                        .build();

            }

            context.getChatClient().setSessionValue("authenticated", false);

            return ResponseBuilder.create()
                    .messageId(request.getMessageId())
                    .code(LoginCommand.INVALID_PASSWORD)
                    .build();

        };
    }

}
