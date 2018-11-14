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

import me.bokov.prog3.server.net.ClientConnectionInformation;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {

    private static final String CLIENT_CONNECTION_LISTENER_THREAD_NAME = "SERVER - Client Connection Listener Thread";

    private final ServerConfig serverConfig;

    private ServerSocket serverSocket;

    private Thread clientConnectionListenerThread;

    private List <ChatClient> connectedClients = Collections.synchronizedList(new ArrayList<>());

    public ChatServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    private void setUpServerSocket () {

        try {

            serverSocket = new ServerSocket(serverConfig.getPort());
            // serverSocket.bind(new InetSocketAddress(bindToHost, port));

        } catch (Exception exc) {

            throw new IllegalStateException(exc);

        }

    }

    public void broadcastRawMessage (String rawMessage) {

        connectedClients.forEach(c -> c.sendRawMessage(rawMessage));

    }

    public void removeClient (ChatClient client) {

        connectedClients.remove(client);

    }

    public void start () {

        setUpServerSocket();

        clientConnectionListenerThread = new Thread(
                new ClientConnectionListenerTask(this)
        );
        clientConnectionListenerThread.setName(CLIENT_CONNECTION_LISTENER_THREAD_NAME);
        clientConnectionListenerThread.start();

    }

    class ClientConnectionListenerTask implements Runnable {

        private final ChatServer chatServer;

        ClientConnectionListenerTask(ChatServer chatServer) {
            this.chatServer = chatServer;
        }

        @Override
        public void run() {

            while (true) {

                try {

                    Socket newClientConnectionSocket = chatServer.serverSocket.accept();

                    ClientConnectionInformation connectionInformation = new ClientConnectionInformation();

                    connectionInformation.setIpAddress(newClientConnectionSocket.getInetAddress().getHostAddress());
                    connectionInformation.setRemotePort(newClientConnectionSocket.getPort());

                    System.out.println("New connection from '" + connectionInformation.getIpAddress() + "':" + connectionInformation.getRemotePort());

                    synchronized (chatServer.connectedClients) {

                        ChatClient newClient = new ChatClient(chatServer, newClientConnectionSocket);
                        newClient.start();
                        chatServer.connectedClients.add(newClient);

                    }

                } catch (Exception exc) {

                    throw new IllegalStateException(exc);

                }

            }

        }

    }

}
