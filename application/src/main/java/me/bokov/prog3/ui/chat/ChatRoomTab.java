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

package me.bokov.prog3.ui.chat;

import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.service.common.ChatRoomVO;

import javax.enterprise.inject.spi.CDI;
import javax.json.JsonObject;
import javax.swing.*;
import java.awt.*;

public class ChatRoomTab extends JPanel {

    private final Long roomId;

    private ChatRoomVO roomData;

    private ChatRoomMessagesPanel messagesPanel;
    private ChatRoomControlsPanel controlsPanel;

    private void initPanel () {

        messagesPanel = new ChatRoomMessagesPanel();
        controlsPanel = new ChatRoomControlsPanel();

        setLayout(new GridBagLayout());

        GridBagConstraints messagesGbc = new GridBagConstraints();
        messagesGbc.gridx = 0;
        messagesGbc.gridy = 0;
        messagesGbc.gridwidth = 1;
        messagesGbc.gridheight = 1;
        messagesGbc.weightx = 1.0;
        messagesGbc.weighty = 1.0;
        messagesGbc.fill = GridBagConstraints.BOTH;
        messagesGbc.anchor = GridBagConstraints.NORTH;

        add(messagesPanel, messagesGbc);


        GridBagConstraints controlsGbc = new GridBagConstraints();
        controlsGbc.gridx = 1;
        controlsGbc.gridy = 0;
        controlsGbc.gridwidth = 1;
        controlsGbc.gridheight = 1;
        controlsGbc.weightx = 0.0;
        controlsGbc.weighty = 1.0;
        controlsGbc.anchor = GridBagConstraints.NORTH;

        add(controlsPanel, controlsGbc);

    }

    private void fetchRoom () {

        ChatClient chatClient = CDI.current().select(ChatClient.class).get();

        Response response = chatClient.getServerEndpoint().getRoom().roomId(this.roomId).execute();

        JsonObject roomObject = response.getData().asJsonObject().getJsonObject("room");

        roomData = new ChatRoomVO();
        roomData.setName(roomObject.getString("name"));

    }

    public ChatRoomTab(Long roomId) {

        this.roomId = roomId;

        fetchRoom();
        initPanel();

    }

    public String getRoomName () {
        return roomData.getName();
    }

    public ChatRoomMessagesPanel getMessagesPanel() {
        return messagesPanel;
    }

    public ChatRoomControlsPanel getControlsPanel() {
        return controlsPanel;
    }
}
