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

package me.bokov.prog3.client.handler;

import me.bokov.prog3.AsyncHelper;
import me.bokov.prog3.client.ChatClientCommandHandlerProviderBean;
import me.bokov.prog3.client.ChatClientMessageHandlingContext;
import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.command.server.NewInvitationCommand;
import me.bokov.prog3.ui.ChatUIBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides a command handler for the {@code NEW-INVITATION} command
 */
@ApplicationScoped
public class NewInvitationCommandHandlerProviderBean implements ChatClientCommandHandlerProviderBean {

    @Inject
    private ChatUIBean chatUIBean;

    @Override
    public Collection<String> getHandledCommands() {
        return Arrays.asList("NEW-INVITATION");
    }

    @Override
    public CommandHandler<ChatClientMessageHandlingContext> getCommandHandler() {
        return (context, request) -> {

            AsyncHelper.runAsync(
                    () -> chatUIBean.handleNewInvitation(request.getData().asJsonObject())
            );

            return ResponseBuilder.create().messageId(request.getMessageId()).code(NewInvitationCommand.SUCCESS).build();

        };
    }
}
