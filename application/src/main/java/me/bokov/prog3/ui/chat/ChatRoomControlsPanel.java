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

import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.service.common.ChatRoomVO;
import me.bokov.prog3.ui.common.PanelList;
import me.bokov.prog3.util.I18N;

import javax.enterprise.inject.spi.CDI;
import javax.json.JsonArray;
import javax.swing.*;
import java.awt.*;

public class ChatRoomControlsPanel extends JPanel {

    private JButton inviteUserButton;
    private JButton leaveRoomButton;
    private JButton deleteRoomButton;
    private PanelList <UserListItem> usersList;

    private I18N i18n;

    private final Long roomId;

    private void doInviteUser () {

        ChatClient chatClient = CDI.current().select(ChatClient.class).get();

        String username = JOptionPane.showInputDialog(i18n.getText("chat.chat-room-controls.invite-prompt"));

        chatClient.getServerEndpoint()
                .inviteUser().roomId(roomId).invitedUsername(username).execute();

    }

    private void initButtons () {

        inviteUserButton = new JButton(i18n.getText("chat.chat-room-controls.invite-user"));
        inviteUserButton.setBackground(Color.GREEN);
        inviteUserButton.addActionListener(
                e -> doInviteUser()
        );

        leaveRoomButton = new JButton(i18n.getText("chat.chat-room-controls.leave-room"));
        leaveRoomButton.setBackground(Color.RED);
        leaveRoomButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Leave room"));

        deleteRoomButton = new JButton(i18n.getText("chat.chat-room-controls.delete-room"));
        deleteRoomButton.setBackground(Color.RED);
        deleteRoomButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Delete room"));

    }

    private void initUsersList () {

        usersList = new PanelList<>();
        usersList.enableTitle(i18n.getText("chat.chat-room-controls.users"));

    }

    private void initPanel (boolean canLeave, boolean canDelete, boolean canInvite) {

        setLayout(new GridBagLayout ());

        initButtons();
        initUsersList();

        int row = 0;

        if (canInvite) {
            GridBagConstraints gbc = new GridBagConstraints(0, row++, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 4,  4);
            add(inviteUserButton, gbc);
        }
        if (canLeave) {
            GridBagConstraints gbc = new GridBagConstraints(0, row++, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 4,  4);
            add(leaveRoomButton, gbc);
        }
        if (canDelete) {
            GridBagConstraints gbc = new GridBagConstraints(0, row++, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 4,  4);
            add(deleteRoomButton, gbc);
        }
        GridBagConstraints gbc = new GridBagConstraints(0, row++, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 4,  4);
        add(usersList, gbc);

    }

    public ChatRoomControlsPanel(Long roomId) {
        this.roomId = roomId;

        i18n = CDI.current().select(I18N.class).get();

    }

    public void init (boolean isOwner, boolean isLobby) {

        initPanel(!isOwner && !isLobby, isOwner, !isLobby);

    }

    public void loadMembers (ChatRoomVO vo) {

        usersList.clearPanels();

        ChatClient chatClient = CDI.current().select(ChatClient.class).get();

        vo.getMembers().forEach(
                member -> {

                    usersList.addPanel(
                            new UserListItem(
                                    member.getUsername(),
                                    chatClient.getSessionValue("userId").equals(member.getId()),
                                    vo.getOwner() != null && member.getId().equals(vo.getOwner().getId()),
                                    member.getOnline()
                            )
                    );

                }
        );

    }

    public static final class UserListItem extends JPanel {

        private String username;
        private boolean isYou;
        private boolean isOwner;
        private boolean isOnline;

        private void initPanel () {

            I18N i18n = CDI.current().select(I18N.class).get();

            StringBuilder sb = new StringBuilder();

            sb.append(username);

            if (isYou) {
                sb.append(" (").append(i18n.getText("chat.chat-room-controls.you")).append(")");
            } else if (isOwner) {
                sb.append(" (").append(i18n.getText("chat.chat-room-controls.owner")).append(")");
            }

            JLabel label = new JLabel(sb.toString());

            if (!isOnline) {
                label.setForeground(Color.GRAY);
            }

            add(label);

        }

        public UserListItem(String username, boolean isYou, boolean isOwner, boolean isOnline) {

            this.username = username;
            this.isYou = isYou;
            this.isOwner = isOwner;
            this.isOnline = isOnline;

            initPanel();

        }
    }

}
