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

package me.bokov.prog3.common;

import me.bokov.prog3.AsyncHelper;
import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.command.request.RequestBuilder;
import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.event.ClientShouldStopEvent;
import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.service.server.ServerChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.concurrent.*;

public class PacketReaderTask<CTX> implements Runnable {

    private static final long WAIT_INTERVAL_MS = 10L;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ClientBase<CTX> client;

    private BufferedReader inputReader;

    private boolean shouldStop = false;

    public PacketReaderTask(ClientBase<CTX> client) {
        this.client = client;
    }

    private void setUp() {

        this.inputReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

    }

    private void performReadLoop() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> singleLineFuture = executorService.submit(() -> this.inputReader.readLine());

        while (client.isRunning()) {

            if (isShouldStop()) break;

            try {

                if (singleLineFuture.isDone()) {

                    String line = singleLineFuture.get();

                    if (line == null) {

                        synchronized (this) {
                            shouldStop = true;
                        }

                        CDI.current().getBeanManager().fireEvent(
                                new ClientShouldStopEvent(client.getId())
                        );

                        break;

                    }

                    String message = line.trim();

                    logger.info("[ ID = {} ] New packet arrived, length: {}", client.getId(), message.length(), message);

                    if (message.startsWith("Q")) {

                        Request request = RequestBuilder.requestFromMessage(message);
                        Response response = this.client.handleRequest(request);
                        this.client.addOutgoingResponse(response);

                        if (client instanceof ServerChatClient && request.getCommand().equals("DISCONNECT")) {
                            AsyncHelper.runAsync( () -> {
                                CDI.current().getBeanManager().fireEvent(
                                        new ClientShouldStopEvent(client.getId())
                                );
                            } );
                        }

                        if (client instanceof ChatClient && request.getCommand().equals("YOU-ARE-BANNED")) {
                            AsyncHelper.runAsync( () -> {
                                CDI.current().getBeanManager().fireEvent(
                                        new ClientShouldStopEvent(client.getId())
                                );
                            } );
                        }

                    } else if (message.startsWith("A")) {

                        Response response = ResponseBuilder.responseFromMessage(message);
                        this.client.addIncomingResponse(response);

                    }

                    singleLineFuture = executorService.submit(() -> this.inputReader.readLine());

                }

                Thread.sleep(WAIT_INTERVAL_MS);

            } catch (Exception exc) {

                if (exc instanceof ExecutionException) {
                    ExecutionException execExc = (ExecutionException) exc;
                    if (execExc.getCause() instanceof SocketException) {

                        AsyncHelper.runAsync( () -> {
                            CDI.current().getBeanManager().fireEvent(
                                    new ClientShouldStopEvent(client.getId())
                            );
                        } );

                        synchronized (this) {
                            shouldStop = true;
                        }

                    }
                }

                exc.printStackTrace();

            }

        }

        logger.info("[ ID = {} ] Canceling packet reading", client.getId());

        if (!singleLineFuture.isDone() && !singleLineFuture.isCancelled()) {
            singleLineFuture.cancel(true);
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        logger.info("[ ID = {} ] Packet reader started", client.getId());

        setUp();
        performReadLoop();

        logger.info("[ ID = {} ] Packet reader finished", client.getId());

    }

    public synchronized boolean isShouldStop() {
        return shouldStop;
    }
}
