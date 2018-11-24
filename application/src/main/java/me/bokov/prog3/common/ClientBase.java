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

import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.service.common.CommunicationCapableService;
import me.bokov.prog3.service.common.SessionCapableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

public abstract class ClientBase<CTX> implements CommunicationCapableService, SessionCapableService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // Fields
    private final Map<String, CommandHandler<CTX>> commandHandlerMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Object> sessionData = Collections.synchronizedMap(new HashMap<>());
    private final BlockingDeque<Request> outgoingRequests = new LinkedBlockingDeque<>();
    private final BlockingDeque<Response> outgoingResponses = new LinkedBlockingDeque<>();
    private final BlockingDeque<Response> incomingResponses = new LinkedBlockingDeque<>();
    protected Socket socket;
    private CommandHandler<CTX> invalidCommandHandler = null;
    private ExecutorService taskExecutor;

    private boolean running = false;

    // Abstract methods

    protected abstract CommandHandler<CTX> createInvalidCommandHandler();

    protected abstract Class<? extends CommandHandlerProviderBean<CTX>> getCommandHandlerProviderBeanClass();

    protected abstract CTX getCommandHandlingContext();

    // Other methods

    protected void preStart() {
    }

    protected void postStart() {
    }

    protected void preStop() {
    }

    protected void postStop() {
    }

    private void startTasks() {

        taskExecutor = Executors.newCachedThreadPool();

        taskExecutor.submit(new PacketReaderTask<CTX>(this));
        taskExecutor.submit(new PacketWriterTask<CTX>(this));

    }

    private void initCommandHandlers() {

        this.invalidCommandHandler = createInvalidCommandHandler();

        CDI.current().select(getCommandHandlerProviderBeanClass()).forEach(
                bean -> bean.getHandledCommands().forEach(cmd -> commandHandlerMap.put(cmd, bean.getCommandHandler()))
        );

    }

    @Override
    public void start(Socket socket) {

        logger.info("Booting client for socket {}", socket);

        preStart();

        this.socket = socket;
        this.running = true;

        initCommandHandlers();
        startTasks();

        postStart();

        logger.info("Client booting complete");

    }

    @Override
    public void stop() {

        logger.info("Shutting client for socket {} down", socket);

        preStop();

        synchronized (this) {

            this.running = false;

        }

        try {

            taskExecutor.shutdown();
            taskExecutor.awaitTermination(60000L, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {

            e.printStackTrace();

        }

        postStop();

        logger.info("Client shutdown complete");

    }

    @Override
    public void send(Request request) {

        synchronized (outgoingRequests) {
            outgoingRequests.addLast(request);
        }

    }

    @Override
    public Response readResponse(String messageId, long timeoutInMillisec) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<Response> future = executorService.submit(

                () -> {

                    while (true) {

                        synchronized (incomingResponses) {

                            if (!incomingResponses.isEmpty()) {

                                Iterator<Response> responseIterator = incomingResponses.iterator();

                                while (responseIterator.hasNext()) {

                                    Response r = responseIterator.next();
                                    if (r.getMessageId().equals(messageId)) {

                                        logger.info("Found the required response with message ID {}: {}", messageId, r);

                                        responseIterator.remove();
                                        return r;

                                    }

                                }

                            }

                        }

                        Thread.sleep(10L);

                    }

                }

        );

        try {

            Response r = future.get(timeoutInMillisec, TimeUnit.MILLISECONDS);

            executorService.shutdown();
            executorService.awaitTermination(1000L, TimeUnit.MILLISECONDS);

            return r;

        } catch (Exception exc) {

            throw new RuntimeException(exc);

        }

    }

    @Override
    public void setSessionValue(String key, Object value) {

        synchronized (sessionData) {
            sessionData.put(key, value);
        }

    }

    @Override
    public Object getSessionValue(String key) {

        synchronized (sessionData) {
            return sessionData.get(key);
        }

    }

    @Override
    public boolean isSessionValueSet(String key) {

        synchronized (sessionData) {
            return sessionData.containsKey(key);
        }

    }

    @Override
    public void removeSessionValue(String key) {

        synchronized (sessionData) {
            sessionData.remove(key);
        }

    }

    @Override
    public void clearSession() {

        synchronized (sessionData) {
            sessionData.clear();
        }

    }

    Response handleRequest(Request request) throws Exception {

        if (commandHandlerMap.containsKey(request.getCommand())) {

            return commandHandlerMap.get(request.getCommand())
                    .handleCommand(
                            getCommandHandlingContext(),
                            request
                    );

        } else {

            return invalidCommandHandler.handleCommand(
                    getCommandHandlingContext(),
                    request
            );

        }

    }

    public synchronized boolean isRunning() {
        return running;
    }

    void addIncomingResponse(Response r) {

        synchronized (incomingResponses) {
            incomingResponses.addLast(r);
        }

    }

    void addOutgoingResponse(Response r) {

        synchronized (outgoingResponses) {
            outgoingResponses.addLast(r);
        }

    }

    boolean hasOutgoingRequest() {

        synchronized (outgoingRequests) {
            return !outgoingRequests.isEmpty();
        }

    }

    boolean hasOutgoingResponse() {

        synchronized (outgoingResponses) {
            return !outgoingResponses.isEmpty();
        }

    }

    Request takeOutgoingRequest() {

        synchronized (outgoingRequests) {
            try {
                return outgoingRequests.take();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    Response takeOutgoingResponse() {

        synchronized (outgoingResponses) {
            try {
                return outgoingResponses.take();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
