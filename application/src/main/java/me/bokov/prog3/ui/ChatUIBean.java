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

package me.bokov.prog3.ui;

import me.bokov.prog3.AsyncHelper;
import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.ui.chat.ChatRoomMessagesPanel;
import me.bokov.prog3.ui.chat.ChatRoomTab;
import me.bokov.prog3.ui.chat.MessageComposerPanel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ChatUIBean extends ScreenBase {

    @Inject
    private ChatClient chatClient;

    @Inject
    private ImageStoreBean imageStoreBean;

    JTabbedPane chatRoomsTabbedPane;
    private List<ChatRoomTab> chatRoomTabs = new ArrayList<>();
    MessageComposerPanel messageComposerPanel;

    private void initChatRoomTabs () {

        // TODO: The tabs must be updated when a JoinedRoomEvent or LeftRoomEvent is fired

        chatRoomsTabbedPane = new JTabbedPane();
        chatRoomsTabbedPane.addTab("", new JPanel());

        JButton createRoomButton = new JButton(i18n.getText("chat.create-room"));
        createRoomButton.setBorder(BorderFactory.createEmptyBorder());
        createRoomButton.addActionListener(
                e -> {

                    AsyncHelper.runAsync(
                            () -> {

                                String roomName = JOptionPane.showInputDialog(null, i18n.getText("chat.room-name"));
                                chatClient.getServerEndpoint().createRoom()
                                        .roomName(roomName)
                                        .execute();

                            }
                    );

                }
        );

        chatRoomsTabbedPane.setTabComponentAt(0, createRoomButton);

    }

    private void initMessageComposer () {

        messageComposerPanel = new MessageComposerPanel();

    }

    @Override
    public void initialize() {

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        initChatRoomTabs();
        initMessageComposer();

        GridBagConstraints tabsGbc = new GridBagConstraints();
        tabsGbc.gridx = 0;
        tabsGbc.gridy = 0;
        tabsGbc.gridwidth = 1;
        tabsGbc.gridheight = 1;
        tabsGbc.weightx = 1.0;
        tabsGbc.weighty = 1.0;
        tabsGbc.fill = GridBagConstraints.BOTH;

        panel.add(chatRoomsTabbedPane, tabsGbc);


        GridBagConstraints composerGbc = new GridBagConstraints();
        composerGbc.gridx = 0;
        composerGbc.gridy = 1;
        composerGbc.gridwidth = 1;
        composerGbc.gridheight = 1;
        composerGbc.weightx = 1.0;
        composerGbc.weighty = 0.0;
        composerGbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(messageComposerPanel, composerGbc);

    }

    public void addRoomTab (Long roomId) {

        SwingUtilities.invokeLater(
                () -> {

                    ChatRoomTab tab = new ChatRoomTab(roomId);
                    chatRoomsTabbedPane.addTab(tab.getRoomName(), tab);
                    chatRoomTabs.add(tab);
                    tab.getMessagesPanel().updateMessages();

                    if (chatRoomsTabbedPane.getTabCount() == 2) {
                        chatRoomsTabbedPane.setSelectedIndex(1);
                    }

                }
        );

    }

    public void handleNewMessage (Long roomId, JsonObject messageObject) {

        SwingUtilities.invokeLater(
                () -> {

                    chatRoomTabs.stream().filter(t -> t.getRoomId().equals(roomId))
                            .findFirst()
                            .ifPresent(
                                    chatRoomTab -> {

                                        chatRoomTab.getMessagesPanel().addMessage(messageObject);

                                        chatRoomTab.invalidate();
                                        chatRoomTab.revalidate();
                                        chatRoomTab.repaint();

                                    }
                            );

                }
        );

    }

    public void handleRoomChange (Long roomId) {

        SwingUtilities.invokeLater(
                () -> {

                    chatRoomTabs.stream().filter(t -> t.getRoomId().equals(roomId))
                            .findFirst()
                            .ifPresent(
                                    t -> {

                                        t.reloadRoom();

                                        t.invalidate();
                                        t.revalidate();
                                        t.repaint();

                                    }
                            );

                }
        );

    }

    public ChatRoomTab getActiveChatRoomTab () {

        return (ChatRoomTab) chatRoomsTabbedPane.getSelectedComponent();

    }

}
