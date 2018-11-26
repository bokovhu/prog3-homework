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

    private static final long WAIT_INTERVAL_MS = 10L;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ClientBase<CTX> client;

    private PrintStream outputPrintStream;

    public PacketWriterTask(ClientBase<CTX> client) {
        this.client = client;
    }

    private void setUp() {

        this.outputPrintStream = new PrintStream(this.client.getOutputStream());

    }

    private void writeMessagesIfAvailable () {

        // Only write requests is the client is not "marked" for stopping
        while (this.client.hasOutgoingRequest() && this.client.isRunning()) {

            Request r = this.client.takeOutgoingRequest();
            logger.info("[ ID = {} ] Outgoing request is ready to be sent", client.getId());
            this.outputPrintStream.println(r.toString());

        }

        // Always write responses
        while (this.client.hasOutgoingResponse()) {

            Response r = this.client.takeOutgoingResponse();
            logger.info("[ ID = {} ] Outgoing response is ready to be sent", client.getId(), r);
            this.outputPrintStream.println(r.toString());

        }

    }

    private void performWriteLoop() {

        while (this.client.isRunning()) {

            try {

                writeMessagesIfAvailable();

                Thread.sleep(WAIT_INTERVAL_MS);

            } catch (Exception exc) {

                exc.printStackTrace();

            }

        }

        // Write any leftover responses
        writeMessagesIfAvailable();

    }

    @Override
    public void run() {

        logger.info("[ ID = {} ] Packet writer started", client.getId());

        setUp();
        performWriteLoop();

        try {
            this.outputPrintStream.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        logger.info("[ ID = {} ] Packet writer finished", client.getId());

    }
}
