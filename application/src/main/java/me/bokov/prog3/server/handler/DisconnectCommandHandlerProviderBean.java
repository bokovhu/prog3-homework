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
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.event.ClientShouldStopEvent;
import me.bokov.prog3.server.ServerChatClientCommandHandlerProviderBean;
import me.bokov.prog3.server.ServerChatClientMessageHandlingContext;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides a command handler for the {@code DISCONNECT} command
 */
@ApplicationScoped
public class DisconnectCommandHandlerProviderBean implements ServerChatClientCommandHandlerProviderBean {

    @Inject
    private Event <ClientShouldStopEvent> clientShouldStopEvent;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("DISCONNECT");
    }

    @Override
    public CommandHandler<ServerChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            return ResponseBuilder.create().messageId(request.getMessageId()).code(200).build();

        };
    }
}
