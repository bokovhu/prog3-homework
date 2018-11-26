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
import me.bokov.prog3.ui.chat.ChatRoomTab;
import me.bokov.prog3.ui.chat.MessageComposerPanel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ChatUIBean extends ScreenBase {

    @Inject
    private ChatClient chatClient;

    @Inject
    private ImageStoreBean imageStoreBean;

    JButton createRoomButton;
    JTabbedPane chatRoomsTabbedPane;
    private List<ChatRoomTab> chatRoomTabs = new ArrayList<>();
    MessageComposerPanel messageComposerPanel;

    private void initChatRoomTabs() {

        chatRoomsTabbedPane = new JTabbedPane();

        createRoomButton = new JButton(i18n.getText("chat.create-room"));
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

        chatRoomsTabbedPane.addChangeListener(
                e -> {

                    if (chatRoomsTabbedPane.getSelectedComponent() instanceof ChatRoomTab) {

                        ChatRoomTab tab = (ChatRoomTab) chatRoomsTabbedPane.getSelectedComponent();

                        tab.getTabTitleLabel().setText(tab.getRoomName());
                        tab.getMessagesPanel().setUnreadMessageCount(0);

                    }

                }
        );

    }

    private void initMessageComposer() {

        messageComposerPanel = new MessageComposerPanel();

    }

    @Override
    public void initialize() {

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        initChatRoomTabs();
        initMessageComposer();

        GridBagConstraints createRoomGbc = new GridBagConstraints();
        createRoomGbc.gridx = 0;
        createRoomGbc.gridy = 0;
        createRoomGbc.gridwidth = 1;
        createRoomGbc.gridheight = 1;
        createRoomGbc.weightx = 0.0;
        createRoomGbc.weighty = 0.0;
        createRoomGbc.anchor = GridBagConstraints.WEST;
        panel.add(createRoomButton, createRoomGbc);

        GridBagConstraints tabsGbc = new GridBagConstraints();
        tabsGbc.gridx = 0;
        tabsGbc.gridy = 1;
        tabsGbc.gridwidth = 1;
        tabsGbc.gridheight = 1;
        tabsGbc.weightx = 1.0;
        tabsGbc.weighty = 1.0;
        tabsGbc.fill = GridBagConstraints.BOTH;

        panel.add(chatRoomsTabbedPane, tabsGbc);


        GridBagConstraints composerGbc = new GridBagConstraints();
        composerGbc.gridx = 0;
        composerGbc.gridy = 2;
        composerGbc.gridwidth = 1;
        composerGbc.gridheight = 1;
        composerGbc.weightx = 1.0;
        composerGbc.weighty = 0.0;
        composerGbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(messageComposerPanel, composerGbc);

    }

    public void addRoomTab(Long roomId) {

        SwingUtilities.invokeLater(
                () -> {

                    if (chatRoomTabs.stream().noneMatch(t -> t.getRoomId().equals(roomId))) {

                        ChatRoomTab tab = new ChatRoomTab(roomId);
                        chatRoomsTabbedPane.addTab(tab.getRoomName(), tab);
                        chatRoomsTabbedPane.setTabComponentAt(
                                chatRoomsTabbedPane.getTabCount() - 1,
                                tab.getTabTitleLabel()
                        );
                        chatRoomTabs.add(tab);
                        tab.getMessagesPanel().updateMessages();

                        if (chatRoomsTabbedPane.getTabCount() == 2) {
                            chatRoomsTabbedPane.setSelectedIndex(1);
                        }

                    }

                }
        );

    }

    public void removeRoomTab (Long roomId) {

        SwingUtilities.invokeLater(
                () -> {

                    chatRoomTabs.stream().filter(t -> t.getRoomId().equals(roomId)).findFirst()
                    .ifPresent(
                            tab -> {

                                chatRoomsTabbedPane.removeTabAt(
                                        chatRoomsTabbedPane.indexOfComponent(tab)
                                );

                                chatRoomTabs.remove(tab);

                            }
                    );

                }
        );

    }

    public void handleNewMessage(Long roomId, JsonObject messageObject) {

        SwingUtilities.invokeLater(
                () -> {

                    chatRoomTabs.stream().filter(t -> t.getRoomId().equals(roomId))
                            .findFirst()
                            .ifPresent(
                                    chatRoomTab -> {

                                        chatRoomTab.getMessagesPanel().addMessage(messageObject);

                                        if (chatRoomTab.getMessagesPanel().getUnreadMessageCount() > 0) {
                                            chatRoomTab.getTabTitleLabel().setText(
                                                    chatRoomTab.getRoomName() + " (" + chatRoomTab.getMessagesPanel().getUnreadMessageCount() + ")"
                                            );
                                        }

                                        chatRoomTab.invalidate();
                                        chatRoomTab.revalidate();
                                        chatRoomTab.repaint();

                                    }
                            );

                }
        );

    }

    public void handleRoomChange(Long roomId) {

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

    public void handleNewInvitation(JsonObject newInvitationObject) {

        SwingUtilities.invokeLater(
                () -> {

                    System.out.println("handleNewInvitation: " + newInvitationObject.toString());

                    JsonObject invitation = newInvitationObject.getJsonObject("invitation");

                    int acceptResult = JOptionPane.showConfirmDialog(
                            null,
                            i18n.getText(
                                    "chat.new-invitation",
                                    invitation.getJsonObject("invitor").getString("username"),
                                    invitation.getJsonObject("room").getString("name")
                            ),
                            i18n.getText("chat.new-invitation-title"),
                            JOptionPane.YES_NO_OPTION
                    );

                    if (acceptResult == JOptionPane.YES_OPTION) {

                        chatClient.getServerEndpoint().acceptInvitation()
                                .invitationId(invitation.getString("invitationId"))
                                .executeWithoutAnswer();

                    }

                }
        );

    }

    public ChatRoomTab getActiveChatRoomTab() {

        if (!(chatRoomsTabbedPane.getSelectedComponent() instanceof ChatRoomTab)) return null;
        return (ChatRoomTab) chatRoomsTabbedPane.getSelectedComponent();

    }

}
