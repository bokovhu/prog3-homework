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

import me.bokov.prog3.command.CommandHandler;
import me.bokov.prog3.command.endpoint.ChatClientEndpoint;
import me.bokov.prog3.command.endpoint.ConnectionInformationImpl;
import me.bokov.prog3.command.request.Request;
import me.bokov.prog3.command.request.RequestBuilder;
import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.command.response.ResponseBuilder;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.server.ConnectedChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.spi.CDI;
import javax.json.JsonValue;
import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ConnectedChatClientImpl implements ConnectedChatClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Socket clientSocket;

    private InputStream clientInputStream;
    private OutputStream clientOutputStream;

    private Thread inputReaderThread;
    private Thread outputWriterThread;

    private final Map<String, CommandHandler <ChatClientMessageHandlingContext>> chatClientMessageHandlerMap = new HashMap<>();
    private CommandHandler <ChatClientMessageHandlingContext> invalidMessageHandler = null;

    private final BlockingDeque<String> outgoingRawMessageQueue = new LinkedBlockingDeque<>(64);

    private final Map <String, Object> sessionData = Collections.synchronizedMap(new HashMap<>());

    private ChatClientEndpoint chatClientEndpoint;

    public ChatClientEndpoint getClientEndpoint() {
        return chatClientEndpoint;
    }

    private void initializeMessageHandlers() {

        invalidMessageHandler = (ctx, req) -> {
            logger.warn("Invalid message received: '{}', command: {}", req.getMessageId(), req.getCommand());
            return ResponseBuilder.create().messageId(req.getMessageId()).code(999).build();
        };

        CDI.current().select(ClientCommandHandlerBean.class)
                .forEach(
                        clientCommandHandlerBean -> {

                            clientCommandHandlerBean.getHandledCommands()
                                    .forEach(
                                            s -> chatClientMessageHandlerMap.put(s, clientCommandHandlerBean.getMessageHandler())
                                    );

                        }
                );

    }

    private void startInputReader() {

        inputReaderThread = new Thread(
                new InputReaderTask(this)
        );
        inputReaderThread.setName("SERVER - Input reader thread for a client");
        inputReaderThread.start();

    }

    private void startOutputWriter() {

        outputWriterThread = new Thread(
                new OutputWriterTask(this)
        );
        outputWriterThread.setName("SERVER - Output writer thread for a client");
        outputWriterThread.start();

    }

    public void sendRawMessage(String rawMessage) {

        outgoingRawMessageQueue.addLast(rawMessage);

    }

    public void sendResponse (Response response) {

        sendRawMessage(response.toString());

    }

    public void sendResponse (String messageId, int responseCode, JsonValue data) {

        sendResponse(
                ResponseBuilder.create()
                        .messageId(messageId)
                        .code(responseCode)
                        .data(data)
                        .build()
        );

    }

    private void createClientEndpoint () {

        this.chatClientEndpoint = new ChatClientEndpointImpl(
                this,
                new ConnectionInformationImpl(
                        clientSocket.getLocalPort(),
                        clientSocket.getPort(),
                        clientSocket.getLocalAddress().toString(),
                        clientSocket.getInetAddress().toString()
                )
        );

    }

    @Override
    public void start(Socket socket) {

        this.clientSocket = socket;

        try {

            clientInputStream = clientSocket.getInputStream();
            clientOutputStream = clientSocket.getOutputStream();

        } catch (Exception exc) {

            throw new IllegalStateException(exc);

        }

        initializeMessageHandlers();
        startInputReader();
        startOutputWriter();
        createClientEndpoint();

    }

    public void stop() {

        try {
            inputReaderThread.interrupt();
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        try {
            outputWriterThread.interrupt();
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        try {
            clientInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // chatServer.removeClient(this);

    }

    @Override
    public void send(Request request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Response readResponse(String messageId, long timeoutInMillisec) {
        throw new UnsupportedOperationException();
    }

    class InputReaderTask implements Runnable {

        private final ConnectedChatClientImpl chatClient;

        InputReaderTask(ConnectedChatClientImpl chatClient) {
            this.chatClient = chatClient;
        }

        @Override
        public void run() {

            BufferedReader bufferedReader = null;

            try {

                bufferedReader = new BufferedReader(new InputStreamReader(this.chatClient.clientInputStream));

            } catch (Exception exc) {

                chatClient.stop();
                throw new IllegalStateException(exc);

            }

            while (true) {

                try {

                    String messageLine = bufferedReader.readLine();

                    Request clientRequest = RequestBuilder.requestFromMessage(messageLine);
                    Response response = null;

                    if (chatClient.chatClientMessageHandlerMap.containsKey(clientRequest.getCommand())) {

                        response = chatClient.chatClientMessageHandlerMap.get(clientRequest.getCommand())
                                .handleCommand(
                                        chatClient.getMessageHandlingContext(),
                                        clientRequest
                                );

                    } else {

                        response = chatClient.invalidMessageHandler.handleCommand(
                                chatClient.getMessageHandlingContext(),
                                clientRequest
                        );

                    }

                    if (response != null) {
                        chatClient.sendResponse(response);
                    }

                } catch (Exception exc) {

                    chatClient.stop();
                    throw new IllegalStateException(exc);

                }

            }

        }

    }

    class OutputWriterTask implements Runnable {

        private final ConnectedChatClientImpl chatClient;

        OutputWriterTask(ConnectedChatClientImpl chatClient) {
            this.chatClient = chatClient;
        }

        @Override
        public void run() {

            BufferedWriter bufferedWriter = null;

            try {

                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(chatClient.clientOutputStream)
                );

            } catch (Exception exc) {

                chatClient.stop();
                throw new IllegalStateException(exc);

            }

            while (true) {

                try {

                    String rawMessage = chatClient.outgoingRawMessageQueue.takeFirst();
                    bufferedWriter.append(rawMessage)
                            .append("\r\n")
                            .flush();

                } catch (Exception exc) {

                    chatClient.stop();
                    throw new IllegalStateException(exc);

                }

            }

        }
    }

    public ChatClientMessageHandlingContext getMessageHandlingContext () {
        return new ChatClientMessageHandlingContext(this);
    }

    public Object getSessionValue (String key) {

        synchronized (sessionData) {
            return sessionData.get(key);
        }

    }

    public void setSessionValue (String key, Object value) {

        synchronized (sessionData) {
            sessionData.put(key, value);
        }

    }

    public void clearSession () {

        synchronized (sessionData) {
            sessionData.clear();
        }

    }

    public boolean isSessionValueSet (String key) {

        synchronized (sessionData) {
            return sessionData.containsKey(key)
                    && sessionData.get(key) != null;
        }

    }

    @Override
    public void removeSessionValue(String key) {

        synchronized (sessionData) {
            sessionData.remove(key);
        }

    }

}
