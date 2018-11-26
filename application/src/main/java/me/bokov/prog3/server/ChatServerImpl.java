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

import me.bokov.prog3.command.endpoint.ChatClientEndpoint;
import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.event.ClientShouldStopEvent;
import me.bokov.prog3.event.UserDisconnectedEvent;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.service.db.BaseEntity;
import me.bokov.prog3.service.db.entity.ChatRoomMembershipEntity;
import me.bokov.prog3.service.server.ServerChatClient;
import me.bokov.prog3.service.server.ServerConfiguration;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ApplicationScoped
public class ChatServerImpl implements ChatServer {

    final List<ServerChatClient> serverChatClients = Collections.synchronizedList(new ArrayList<>());
    private final List<ChatUserVO> connectedUsers = Collections.synchronizedList(new ArrayList<>());
    ServerSocket serverSocket;
    @Inject
    private Logger logger;
    @Inject
    private Database database;
    private boolean running = false;
    private ExecutorService taskExecutor;
    private ServerConfiguration serverConfiguration;

    private void setUpServerSocket() {

        try {

            serverSocket = new ServerSocket(serverConfiguration.getPort());

        } catch (Exception exc) {

            throw new IllegalStateException(exc);

        }

    }

    void handleServerClientDisconnection(ServerChatClientImpl client) {

        logger.info("Handling server client disconnection");

        synchronized (serverChatClients) {
            serverChatClients.remove(client);
            logger.info("Removed from serverChatClients");
        }

        synchronized (connectedUsers) {
            connectedUsers.removeIf(vo -> vo.getUsername().equals(client.getSessionValue("username")));
            logger.info("Removed the connected user info");
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

        synchronized (this) {
            running = true;
        }

        taskExecutor = Executors.newSingleThreadExecutor();
        taskExecutor.submit(new ChatServerClientConnectionListenerTask(this));

        logger.info("Server started successfully");

    }

    @Override
    public synchronized boolean isRunning() {
        return running;
    }

    @Override
    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    @Override
    public void stop() {

        synchronized (this) {
            this.running = false;
        }

        try {
            taskExecutor.shutdown();
            taskExecutor.awaitTermination(1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serverChatClients.forEach(ServerChatClient::stop);

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (serverChatClients) {
            serverChatClients.clear();
        }

        synchronized (connectedUsers) {
            connectedUsers.clear();
        }

    }

    @Override
    public List<ChatUserVO> getConnectedUsers() {

        synchronized (connectedUsers) {
            return new ArrayList<>(connectedUsers);
        }

    }

    @Override
    public List<ChatClientEndpoint> clientsInRoom(Long roomId) {

        try {

            List <Long> userIdsInRoom = database.getChatRoomMembershipDao()
                    .queryBuilder().where().eq("chat_room_id", roomId)
                    .query()
                    .stream().map(ChatRoomMembershipEntity::getChatUser)
                    .map(BaseEntity::getId)
                    .collect(Collectors.toList());

            synchronized (serverChatClients) {

                return serverChatClients.stream()
                        .filter(c -> c.isSessionValueSet("userId"))
                        .filter(c -> userIdsInRoom.contains(c.getSessionValue("userId")))
                        .map(ServerChatClient::getClientEndpoint)
                        .collect(Collectors.toList());

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public Optional<ChatClientEndpoint> clientByUserId(Long userId) {

        synchronized (serverChatClients) {

            return serverChatClients.stream()
                    .filter(c -> c.isSessionValueSet("userId"))
                    .filter(c -> userId.equals(c.getSessionValue("userId")))
                    .findFirst()
                    .map(ServerChatClient::getClientEndpoint);

        }

    }

    ServerSocket getServerSocket() {
        return serverSocket;
    }

    List<ServerChatClient> getServerChatClients() {
        return serverChatClients;
    }

    public void observeUserDisconnectedEvent(@Observes ClientShouldStopEvent event) {

        List<ServerChatClient> toStop = new ArrayList<>();

        synchronized (serverChatClients) {
            serverChatClients.stream().filter(c -> c.getId().equals(event.getClientId()))
                    .forEach(toStop::add);
        }

        toStop.forEach(ServerChatClient::stop);

    }

}
