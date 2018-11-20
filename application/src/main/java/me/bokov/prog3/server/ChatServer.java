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

import me.bokov.prog3.command.endpoint.ConnectionInformation;
import me.bokov.prog3.command.endpoint.ConnectionInformationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String CLIENT_CONNECTION_LISTENER_THREAD_NAME = "SERVER - Client Connection Listener Thread";

    private final ServerConfig serverConfig;

    private ServerSocket serverSocket;

    private Thread clientConnectionListenerThread;

    private final List<ChatClient> connectedClients = Collections.synchronizedList(new ArrayList<>());

    public ChatServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    private void setUpServerSocket() {

        try {

            serverSocket = new ServerSocket(serverConfig.getPort());
            // serverSocket.bind(new InetSocketAddress(bindToHost, port));

        } catch (Exception exc) {

            throw new IllegalStateException(exc);

        }

    }

    public void broadcastRawMessage(String rawMessage) {

        connectedClients.forEach(c -> c.sendRawMessage(rawMessage));

    }

    public void removeClient(ChatClient client) {

        synchronized (connectedClients) {
            connectedClients.remove(client);
        }

    }

    public void disconnectClientByUsername(String username) {

        synchronized (connectedClients) {

            connectedClients.stream().filter(cc -> cc.isSessionValueSet("username"))
                    .filter(cc -> cc.getSessionValue("username").equals(username))
                    .findFirst()
                    .ifPresent(
                            ChatClient::stop
                    );

        }

    }

    public Set<String> getConnectedUsernames() {

        synchronized (connectedClients) {

            return connectedClients.stream()
                    .filter(cc -> cc.isSessionValueSet("username"))
                    .map(cc -> cc.getSessionValue("username").toString())
                    .collect(Collectors.toSet());

        }

    }

    public void start() {

        logger.info("Starting server");

        setUpServerSocket();

        clientConnectionListenerThread = new Thread(
                new ClientConnectionListenerTask(this)
        );
        clientConnectionListenerThread.setName(CLIENT_CONNECTION_LISTENER_THREAD_NAME);
        clientConnectionListenerThread.start();

        logger.info("Server started successfully");

    }

    class ClientConnectionListenerTask implements Runnable {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final ChatServer chatServer;

        ClientConnectionListenerTask(ChatServer chatServer) {
            this.chatServer = chatServer;
        }

        @Override
        public void run() {

            logger.info("Starting to listen for client connections");

            while (true) {

                try {

                    Socket newClientConnectionSocket = chatServer.serverSocket.accept();

                    ConnectionInformation connectionInformation = new ConnectionInformationImpl(
                            newClientConnectionSocket.getLocalPort(),
                            newClientConnectionSocket.getPort(),
                            newClientConnectionSocket.getLocalAddress().toString(),
                            newClientConnectionSocket.getInetAddress().toString()
                    );

                    logger.info("New connection from {}:{}", connectionInformation.getRemoteAddress(), connectionInformation.getRemotePort());

                    synchronized (chatServer.connectedClients) {

                        ChatClient newClient = new ChatClient(chatServer, newClientConnectionSocket);
                        newClient.start();
                        chatServer.connectedClients.add(newClient);

                    }

                } catch (Exception exc) {

                    logger.error("Error during listening for client connections", exc);

                    throw new IllegalStateException(exc);

                }

            }

        }

    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }


}
