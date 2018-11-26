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
import java.util.*;
import java.util.concurrent.*;

/**
 * An abstract base class for {@link me.bokov.prog3.service.ChatClient}, and {@link me.bokov.prog3.service.server.ServerChatClient}
 * @param <CTX> the type of the context to use for command handlers
 */
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
    private UUID id = UUID.randomUUID();

    // Abstract methods

    /**
     * Creates the command handler for invalid messages
     * @return the invalid command handler
     */
    protected abstract CommandHandler<CTX> createInvalidCommandHandler();

    /**
     * The type of the CDI beans that provide the command handlers
     */
    protected abstract Class<? extends CommandHandlerProviderBean<CTX>> getCommandHandlerProviderBeanClass();

    /**
     * Produces a command handling context
     * @return
     */
    protected abstract CTX getCommandHandlingContext();

    // Other methods

    /**
     * This method is called at the beginning of the {@code start} function
     */
    protected void preStart() {
    }

    /**
     * This method is called at the end of the {@code start} function
     */
    protected void postStart() {
    }

    /**
     * This method is called at the beginning of the {@code stop} function
     */
    protected void preStop() {
    }

    /**
     * This method is called at the end of the {@code start} function
     */
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

        logger.info("Booting client for socket {}, ID = {}", socket, id);

        preStart();

        this.socket = socket;
        this.running = true;

        initCommandHandlers();
        startTasks();

        postStart();

        logger.info("Client booting complete ( ID = {} )", id);

    }

    @Override
    public void stop() {

        logger.info("Shutting client for socket {} down, ID = {}", socket, id);

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

        try {
            this.socket.close();
        } catch (IOException e) {
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

                                        logger.info("[ ID = {} ] Found the required response with message ID {}", id, messageId);

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
            executorService.awaitTermination(5000L, TimeUnit.MILLISECONDS);

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

    /**
     * Tries to handle a single incoming request with an adequate {@link CommandHandler}
     * @param request the request to handle
     * @return the {@link Response} created by the command handler
     */
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

    /**
     * Adds an incoming {@link Response} to the incoming responses queue
     */
    void addIncomingResponse(Response r) {

        synchronized (incomingResponses) {
            incomingResponses.addLast(r);
        }

    }

    /**
     * Adds an outgoing {@link Response} to the outgoing responses queue
     */
    void addOutgoingResponse(Response r) {

        synchronized (outgoingResponses) {
            outgoingResponses.addLast(r);
        }

    }

    /**
     * Checks whether an outgoing {@link Request} is currently available
     * @return true, if a response is available, false otherwise
     */
    boolean hasOutgoingRequest() {

        synchronized (outgoingRequests) {
            return !outgoingRequests.isEmpty();
        }

    }

    /**
     * Checks whether an outgoing {@link Response} is currently available
     * @return true, if a response is available, false otherwise
     */
    boolean hasOutgoingResponse() {

        synchronized (outgoingResponses) {
            return !outgoingResponses.isEmpty();
        }

    }

    /**
     * Waits for and takes a single outgoing {@link Request} from the outgoingRequests queue
     * @return the popped response
     */
    Request takeOutgoingRequest() {

        synchronized (outgoingRequests) {
            try {
                return outgoingRequests.take();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    /**
     * Waits for and takes a single outgoing {@link Response} from the outgoingResponses queue
     * @return the popped response
     */
    Response takeOutgoingResponse() {

        synchronized (outgoingResponses) {
            try {
                return outgoingResponses.take();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    /**
     * Retrieves the {@link InputStream} of the socket
     * @return the {@link InputStream} of the socket
     */
    InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Retrieves the {@link OutputStream} of the socket
     * @return the {@link OutputStream} of the socket
     */
    OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public UUID getId() {
        return id;
    }
}
