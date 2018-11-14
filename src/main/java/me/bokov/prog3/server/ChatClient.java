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

import me.bokov.prog3.server.net.ChatClientService;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ChatClient {

    private final ChatServer chatServer;
    private final Socket clientSocket;

    private InputStream clientInputStream;
    private OutputStream clientOutputStream;

    private Thread inputReaderThread;
    private Thread outputWriterThread;

    private Map <String, ChatClientMessageHandler> chatClientMessageHandlerMap = new HashMap<>();
    private ChatClientMessageHandler invalidMessageHandler = null;

    private BlockingDeque <String> outgoingRawMessageQueue = new LinkedBlockingDeque<>(64);

    public ChatClient(ChatServer chatServer, Socket clientSocket) {
        this.chatServer = chatServer;
        this.clientSocket = clientSocket;
    }

    public ChatClientService getClientService () {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    private void initializeMessageHandlers() {

        invalidMessageHandler = (c, m) -> {
            System.out.println("Invalid message received: " + m);
        };

    }

    private void startInputReader () {

        inputReaderThread = new Thread(
                new InputReaderTask(this)
        );
        inputReaderThread.setName("SERVER - Input reader thread for a client");
        inputReaderThread.start();

    }

    private void startOutputWriter () {

        outputWriterThread = new Thread(
                new OutputWriterTask(this)
        );
        outputWriterThread.setName("SERVER - Output writer thread for a client");
        outputWriterThread.start();

    }

    public void sendRawMessage (String rawMessage) {

        outgoingRawMessageQueue.addLast(rawMessage);

    }

    public void start () {

        try {

            clientInputStream = clientSocket.getInputStream();
            clientOutputStream = clientSocket.getOutputStream();

        } catch (Exception exc) {

            throw new IllegalStateException(exc);

        }

        initializeMessageHandlers();

        startInputReader();

        startOutputWriter();

    }

    public void stop () {

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

    }

    class InputReaderTask implements Runnable {

        private final ChatClient chatClient;

        InputReaderTask(ChatClient chatClient) {
            this.chatClient = chatClient;
        }

        @Override
        public void run() {

            BufferedReader bufferedReader = null;

            try {

                bufferedReader = new BufferedReader(new InputStreamReader(this.chatClient.clientInputStream));

            } catch (Exception exc) {
                throw new IllegalStateException(exc);
            }

            while (true) {

                try {

                    String messageLine = bufferedReader.readLine();

                    String [] splittedMessage = messageLine.split(" ", 2);

                    String command = splittedMessage [0];

                    if (chatClient.chatClientMessageHandlerMap.containsKey(command)) {

                        chatClient.chatClientMessageHandlerMap.get(command).handleMessage(chatClient, messageLine);

                    } else {

                        chatClient.invalidMessageHandler.handleMessage(chatClient, messageLine);

                    }

                } catch (Exception exc) {

                    throw new IllegalStateException(exc);

                }

            }

        }

    }

    class OutputWriterTask implements Runnable {

        private final ChatClient chatClient;

        OutputWriterTask(ChatClient chatClient) {
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

                throw new IllegalStateException(exc);

            }

            while (true) {

                try {

                    String rawMessage = chatClient.outgoingRawMessageQueue.takeFirst();
                    bufferedWriter.append(rawMessage)
                            .append("\r\n")
                            .flush();

                } catch (Exception exc) {

                    throw new IllegalStateException(exc);

                }

            }

        }
    }

}