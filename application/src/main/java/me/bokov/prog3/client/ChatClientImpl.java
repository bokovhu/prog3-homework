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

package me.bokov.prog3.client;

import me.bokov.prog3.AsyncHelper;
import me.bokov.prog3.command.Command;
import me.bokov.prog3.command.CommandException;
import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.client.HelloCommand;
import me.bokov.prog3.command.endpoint.ChatServerEndpoint;
import me.bokov.prog3.command.endpoint.ConnectionInformation;
import me.bokov.prog3.command.endpoint.ConnectionInformationImpl;
import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.common.ClientBase;
import me.bokov.prog3.common.CommandHandlerProviderBean;
import me.bokov.prog3.event.ClientShouldStopEvent;
import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.service.client.ConnectionConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.CDI;
import java.net.Socket;

/**
 * The default chat client implementation
 */
@ApplicationScoped
public class ChatClientImpl extends ClientBase<ChatClientMessageHandlingContext> implements ChatClient {

    private boolean connected = false;

    private ChatServerEndpoint chatServerEndpoint;

    @Override
    protected CommandHandler<ChatClientMessageHandlingContext> createInvalidCommandHandler() {
        return (context, request) -> ResponseBuilder.create().messageId(request.getMessageId()).code(999).build();
    }

    @Override
    protected Class<? extends CommandHandlerProviderBean<ChatClientMessageHandlingContext>> getCommandHandlerProviderBeanClass() {
        return ChatClientCommandHandlerProviderBean.class;
    }

    @Override
    protected ChatClientMessageHandlingContext getCommandHandlingContext() {
        return new ChatClientMessageHandlingContext(this);
    }

    @Override
    public ConnectResult connect(ConnectionConfiguration configuration) {

        if (connected) throw new IllegalStateException("Already connected!");

        try {

            Socket connectionSocket = new Socket(configuration.getHost(), configuration.getPort());

            start(connectionSocket);

            ConnectionInformation connectionInformation = new ConnectionInformationImpl(
                    connectionSocket.getLocalPort(),
                    connectionSocket.getPort(),
                    connectionSocket.getLocalAddress().toString(),
                    connectionSocket.getInetAddress().toString()
            );

            this.chatServerEndpoint = new ChatServerEndpointImpl(this, connectionInformation);

            try {

                Response helloResponse = getServerEndpoint().hello().username(configuration.getUsername())
                        .execute();

                logger.info("Got hello response from server: {}", helloResponse.toString());

                connected = true;

                if (helloResponse.getCode() == HelloCommand.CONTINUE) return ConnectResult.PROCEED_WITH_LOGIN;
                else if (helloResponse.getCode() == HelloCommand.FIRST_TIME)
                    return ConnectResult.PROCEED_WITH_REGISTRATION;

            } catch (CommandException ce) {

                if (ce.getErrorCode() == Command.BANNED) return ConnectResult.BANNED;

            }

            disconnect();

            throw new IllegalStateException("Invalid response");

        } catch (Exception exc) {
            throw new IllegalStateException(exc);
        }

    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void disconnect() {

        if (!connected) throw new IllegalStateException("Not yet connected!");

        try {
            getServerEndpoint().disconnect().executeWithoutAnswer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        stop();
        connected = false;

    }

    @Override
    public ChatServerEndpoint getServerEndpoint() {
        return chatServerEndpoint;
    }

    public void handleClientShouldStopEvent (@Observes ClientShouldStopEvent evt) {

        if (evt.getClientId().equals(getId())) {

            AsyncHelper.runAsync(
                    () -> {
                        CDI.current().select(ChatClient.class).get()
                                .disconnect();
                    }
            );

        }

    }

}
