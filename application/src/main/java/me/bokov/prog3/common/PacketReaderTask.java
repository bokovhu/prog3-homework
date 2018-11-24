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

import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.command.request.RequestBuilder;
import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.command.response.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.*;

public class PacketReaderTask <CTX> implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final long WAIT_INTERVAL_MS = 10L;

    private final ClientBase <CTX> client;

    private Scanner inputScanner;

    public PacketReaderTask(ClientBase<CTX> client) {
        this.client = client;
    }

    private void setUp () {

        this.inputScanner = new Scanner(client.getInputStream());

    }

    private void performReadLoop () {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future <String> singleLineFuture = executorService.submit(() -> this.inputScanner.nextLine());

        while (client.isRunning()) {

            try {

                if (singleLineFuture.isDone()) {

                    String line = singleLineFuture.get();
                    String message = line.trim();

                    logger.info("New packet arrived, length: {}, content: {}", message.length(), message);

                    if (message.startsWith("Q")) {

                        Request request = RequestBuilder.requestFromMessage(message);
                        Response response = this.client.handleRequest(request);
                        this.client.addOutgoingResponse(response);

                    } else if (message.startsWith("A")) {

                        Response response = ResponseBuilder.responseFromMessage(message);
                        this.client.addIncomingResponse(response);

                    }

                    singleLineFuture = executorService.submit(() -> this.inputScanner.nextLine());

                }

                Thread.sleep(WAIT_INTERVAL_MS);

            } catch (Exception exc) {

                exc.printStackTrace();

            }

        }

        logger.info("Canceling packet reading");

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

        logger.info("Packet reader started");

        setUp();
        performReadLoop();

        logger.info("Packet reader finished");

    }

}
