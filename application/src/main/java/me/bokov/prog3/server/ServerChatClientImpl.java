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

package me.bokov.prog3.server;

import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.endpoint.ChatClientEndpoint;
import me.bokov.prog3.command.endpoint.ConnectionInformation;
import me.bokov.prog3.command.endpoint.ConnectionInformationImpl;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.common.ClientBase;
import me.bokov.prog3.common.CommandHandlerProviderBean;
import me.bokov.prog3.event.UserDisconnectedEvent;
import me.bokov.prog3.service.server.ServerChatClient;

import javax.enterprise.inject.spi.CDI;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerChatClientImpl extends ClientBase <ServerChatClientMessageHandlingContext> implements ServerChatClient {

    private final ChatServerImpl chatServer;
    private ChatClientEndpoint chatClientEndpoint;

    public ServerChatClientImpl(ChatServerImpl chatServer) {
        this.chatServer = chatServer;
    }

    public ChatClientEndpoint getClientEndpoint() {
        return chatClientEndpoint;
    }

    @Override
    protected CommandHandler<ServerChatClientMessageHandlingContext> createInvalidCommandHandler() {
        return (ctx, req) -> {
            logger.warn("Invalid message received: '{}', command: {}", req.getMessageId(), req.getCommand());
            return ResponseBuilder.create().messageId(req.getMessageId()).code(999).build();
        };
    }

    @Override
    protected Class<? extends CommandHandlerProviderBean<ServerChatClientMessageHandlingContext>> getCommandHandlerProviderBeanClass() {
        return ServerChatClientCommandHandlerProviderBean.class;
    }

    @Override
    protected ServerChatClientMessageHandlingContext getCommandHandlingContext() {
        return new ServerChatClientMessageHandlingContext(this);
    }

    @Override
    protected void postStart() {

        ConnectionInformation connectionInformation = new ConnectionInformationImpl(
                this.socket.getLocalPort(),
                this.socket.getPort(),
                this.socket.getLocalAddress().toString(),
                this.socket.getInetAddress().toString()
        );

        this.chatClientEndpoint = new ChatClientEndpointImpl(this, connectionInformation);

        // This is a hack, but setting up an executor service is heavier
        // The whole thing is set up like this, because we need to defer actual disconnection to a later time, when
        // the answer to the DISCONNECT message was already sent
        // The CDI event is observed in the ChatServer, and that's the point from where the client is shut down on the
        // server side.
        new Thread(
                () -> {

                    while (isRunning()) {
                        if (isSessionValueSet("disconnected")) {
                            CDI.current().getBeanManager().fireEvent(
                                    new UserDisconnectedEvent(
                                            new Date(),
                                            "USER_DISCONNECTED",
                                            getSessionValue("username").toString()
                                    )
                            );
                        }

                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
        ).start();

    }

    @Override
    protected void postStop() {

        logger.info("{}.postStop ()", getClass());

        this.chatServer.handleServerClientDisconnection(this);

    }
}
