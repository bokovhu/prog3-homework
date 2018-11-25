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

import me.bokov.prog3.ui.common.PanelList;
import me.bokov.prog3.util.I18N;

import javax.enterprise.inject.spi.CDI;
import javax.swing.*;
import java.awt.*;

public class ChatRoomControlsPanel extends JPanel {

    private JButton inviteUserButton;
    private JButton leaveRoomButton;
    private JButton deleteRoomButton;
    private PanelList <UserListItem> usersList;

    private I18N i18n;

    private void initButtons () {

        inviteUserButton = new JButton(i18n.getText("chat.chat-room-controls.invite-user"));
        inviteUserButton.setBackground(Color.GREEN);
        inviteUserButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Invite user"));

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

        usersList.addPanel(new UserListItem("Someone", false, false));
        usersList.addPanel(new UserListItem("Joe", true, false));
        usersList.addPanel(new UserListItem("Janett", false, true));
        usersList.addPanel(new UserListItem("John Doe", false, false));
        usersList.addPanel(new UserListItem("Jack", false, false));

    }

    private void initPanel () {

        setLayout(new GridLayout(3, 1, 4, 4));

        initButtons();
        initUsersList();

        add(inviteUserButton);
        add(leaveRoomButton);
        add(usersList);

    }

    public ChatRoomControlsPanel () {

        i18n = CDI.current().select(I18N.class).get();

        initPanel();

    }

    public static final class UserListItem extends JPanel {

        private String username;
        private boolean isYou;
        private boolean isOwner;

        private void initPanel () {

            I18N i18n = CDI.current().select(I18N.class).get();

            StringBuilder sb = new StringBuilder();

            sb.append(username);

            if (isYou) {
                sb.append(" (").append(i18n.getText("chat.chat-room-controls.you")).append(")");
            } else if (isOwner) {
                sb.append(" (").append(i18n.getText("chat.chat-room-controls.owner")).append(")");
            }

            add(new JLabel(sb.toString()));

        }

        public UserListItem(String username, boolean isYou, boolean isOwner) {

            this.username = username;
            this.isYou = isYou;
            this.isOwner = isOwner;

            initPanel();

        }
    }

}
