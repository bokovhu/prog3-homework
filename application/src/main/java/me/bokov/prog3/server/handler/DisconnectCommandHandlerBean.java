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
import me.bokov.prog3.event.UserDisconnectedEvent;
import me.bokov.prog3.server.ChatClientMessageHandlingContext;
import me.bokov.prog3.server.ClientCommandHandlerBean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class DisconnectCommandHandlerBean implements ClientCommandHandlerBean {

    private Event <UserDisconnectedEvent> userDisconnectedEvent;

    @Override
    public Set<String> getHandledCommands() {
        return new HashSet<>(
                Arrays.asList("DISCONNECT")
        );
    }

    @Override
    public CommandHandler <ChatClientMessageHandlingContext> getMessageHandler() {
        return (context, request) -> {

            userDisconnectedEvent.fire(
                    new UserDisconnectedEvent(
                            new Date(),
                            "USER_DISCONNECTED",
                            context.getChatClient().getSessionValue("username").toString()
                    )
            );

            context.getChatClient().stop();

            return null;

        };
    }
}
