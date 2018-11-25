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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class ChatServerClientConnectionListenerTask implements Runnable {

    private static final long WAIT_INTERVAL_MS = 10L;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ChatServerImpl chatServer;

    ChatServerClientConnectionListenerTask(ChatServerImpl chatServer) {
        this.chatServer = chatServer;
    }

    @Override
    public void run() {

        logger.info("Starting to listen for client connections");

        try {

            logger.info("Connect using one of the following host:port pairs:");

            int port = chatServer.serverSocket.getLocalPort();

            Enumeration <NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface ni = networkInterfaces.nextElement();

                Enumeration <InetAddress> inetAddresses = ni.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {

                    InetAddress ia = inetAddresses.nextElement();

                    logger.info("- {}:{}", ia.getHostAddress(), port);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future f = executorService.submit(
                () -> {

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

                            synchronized (chatServer.serverChatClients) {

                                ServerChatClientImpl newClient = new ServerChatClientImpl(chatServer);
                                newClient.start(newClientConnectionSocket);
                                chatServer.serverChatClients.add(newClient);

                            }

                        } catch (Exception exc) {
                            throw new RuntimeException(exc);
                        }

                    }

                }
        );

        while (chatServer.isRunning()) {

            try {
                Thread.sleep(WAIT_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        f.cancel(true);
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Listening for new connections is stopped");

    }


}
