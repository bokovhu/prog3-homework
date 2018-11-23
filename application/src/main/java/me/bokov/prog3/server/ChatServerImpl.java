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
import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.service.server.ConnectedChatClient;
import me.bokov.prog3.service.server.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ChatServerImpl implements ChatServer {

    @Inject
    private Logger logger;

    private boolean running = false;

    private ServerSocket serverSocket;
    private Thread clientConnectionListenerThread;
    private final List<ChatUserVO> connectedUsers = Collections.synchronizedList(new ArrayList<>());
    private final List<ConnectedChatClient> connectedChatClients = Collections.synchronizedList(new ArrayList<>());
    private ServerConfiguration serverConfiguration;

    private void setUpServerSocket() {

        try {

            serverSocket = new ServerSocket(serverConfiguration.getPort());

        } catch (Exception exc) {

            throw new IllegalStateException(exc);

        }

    }

    public void disconnectClientByUsername(String username) {

        synchronized (connectedChatClients) {

            connectedChatClients.stream().filter(cc -> cc.isSessionValueSet("username"))
                    .filter(cc -> cc.getSessionValue("username").equals(username))
                    .findFirst()
                    .ifPresent(
                            ConnectedChatClient::stop
                    );

        }

    }

    public Set<String> getConnectedUsernames() {

        synchronized (connectedChatClients) {

            return connectedChatClients.stream()
                    .filter(cc -> cc.isSessionValueSet("username"))
                    .map(cc -> cc.getSessionValue("username").toString())
                    .collect(Collectors.toSet());

        }

    }

    @Override
    public void start(ServerConfiguration serverConfiguration) {

        if (running) {
            throw new IllegalStateException("Already running!");
        }

        logger.info("Starting server");

        this.serverConfiguration = serverConfiguration;

        setUpServerSocket();

        clientConnectionListenerThread = new Thread(
                new ClientConnectionListenerTask(this)
        );
        clientConnectionListenerThread.start();

        logger.info("Server started successfully");

        running = true;

    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    @Override
    public void stop() {

        try {

            clientConnectionListenerThread.interrupt();

            connectedChatClients.forEach(ConnectedChatClient::stop);

            serverSocket.close();

            synchronized (connectedChatClients) {
                connectedChatClients.clear();
            }

            synchronized (connectedUsers) {
                connectedUsers.clear();
            }

            running = false;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<ChatUserVO> getConnectedUsers() {

        synchronized (connectedUsers) {
            return new ArrayList<>(connectedUsers);
        }

    }

    @Override
    public void addConnectedUser(ChatUserVO user) {

        synchronized (connectedUsers) {
            connectedUsers.add(user);
        }

    }

    @Override
    public void removeConnectedUser(ChatUserVO user) {

        synchronized (connectedUsers) {
            connectedUsers.remove(user);
        }

    }

    @Override
    public void broadcast(Request request) {

        connectedChatClients.forEach(c -> c.send(request));

    }

    @Override
    public void banUserByUsername(Long userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void banUserByIp(Long userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unbanUser(Long userId) {
        throw new UnsupportedOperationException();
    }

    class ClientConnectionListenerTask implements Runnable {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final ChatServerImpl chatServer;

        ClientConnectionListenerTask(ChatServerImpl chatServer) {
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

                    synchronized (chatServer.connectedChatClients) {

                        ConnectedChatClientImpl newClient = new ConnectedChatClientImpl();
                        newClient.start(newClientConnectionSocket);
                        chatServer.connectedChatClients.add(newClient);

                    }

                } catch (Exception exc) {

                    logger.error("Error during listening for client connections", exc);

                    throw new IllegalStateException(exc);

                }

            }

        }

    }

}
