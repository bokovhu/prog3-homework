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
import me.bokov.prog3.command.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

public class PacketWriterTask<CTX> implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final long WAIT_INTERVAL_MS = 10L;

    private final ClientBase<CTX> client;

    private PrintStream outputPrintStream;

    public PacketWriterTask(ClientBase<CTX> client) {
        this.client = client;
    }

    private void setUp() {

        this.outputPrintStream = new PrintStream(this.client.getOutputStream());

    }

    private void performWriteLoop() {

        while (this.client.isRunning()) {

            try {

                if (this.client.hasOutgoingRequest()) {

                    Request r = this.client.takeOutgoingRequest();
                    logger.info("Outgoing request is ready to be sent: {}", r);
                    this.outputPrintStream.println(r.toString());

                }

                if (this.client.hasOutgoingResponse()) {

                    Response r = this.client.takeOutgoingResponse();
                    logger.info("Outgoing response is ready to be sent: {}", r);
                    this.outputPrintStream.println(r.toString());

                }

                Thread.sleep(WAIT_INTERVAL_MS);

            } catch (Exception exc) {

                exc.printStackTrace();

            }

        }

    }

    @Override
    public void run() {

        logger.info("Packet writer started");

        setUp();
        performWriteLoop();

        logger.info("Packet writer finished");

    }
}
