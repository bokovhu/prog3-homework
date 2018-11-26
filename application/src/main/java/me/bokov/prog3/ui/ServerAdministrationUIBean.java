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

import me.bokov.prog3.event.UserAdministrationEvent;
import me.bokov.prog3.event.UserConnectedEvent;
import me.bokov.prog3.event.UserDisconnectedEvent;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.service.db.entity.ChatUserEntity;
import me.bokov.prog3.ui.common.PanelList;
import me.bokov.prog3.ui.srvadmin.BannedUserItem;
import me.bokov.prog3.ui.srvadmin.UserItem;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServerAdministrationUIBean extends ScreenBase {

    private PanelList<UserItem> usersList;
    private PanelList<BannedUserItem> bannedUsersList;

    @Inject
    private Database database;

    @Inject
    private ChatServer chatServer;

    @Override
    public void initialize() {

        panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        GridBagConstraints titleGbc = new GridBagConstraints(
                0, 0, 3, 1,
                1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(16, 16, 16, 16),
                0, 0
        );

        panel.add(
                new JLabel("Server is running on port " + chatServer.getServerConfiguration().getPort() + ", admin password: " + chatServer.getServerConfiguration().getAdminPassword()),
                titleGbc
        );

        GridBagConstraints usersListGbc = new GridBagConstraints(
                0, 1, 1, 1,
                1.0, 1.0, GridBagConstraints.NORTH, 0,
                new Insets(16, 16, 16, 16),
                0, 0
        );

        usersList = new PanelList<>();
        usersList.enableTitle("Users");

        panel.add(usersList, usersListGbc);

        GridBagConstraints bannedUsersListGbc = new GridBagConstraints(
                1, 1, 1, 1,
                1.0, 1.0, GridBagConstraints.NORTH, 0,
                new Insets(16, 16, 16, 16),
                0, 0
        );

        bannedUsersList = new PanelList<>();
        bannedUsersList.enableTitle("Banned users");

        panel.add(bannedUsersList, bannedUsersListGbc);

        reloadData();

    }

    public void reloadData() {

        Set<String> currentlyConnectedUserNames = chatServer.getConnectedUsers().stream()
                .map(ChatUserVO::getUsername)
                .collect(Collectors.toSet());

        usersList.clearPanels();
        bannedUsersList.clearPanels();

        for (ChatUserEntity chatUserEntity : database.getChatUserDao()) {

            if (chatUserEntity.isBanned()) {
                bannedUsersList.addPanel(
                        new BannedUserItem(chatUserEntity.getUsername(), chatUserEntity.getId())
                );
            } else {
                usersList.addPanel(
                        new UserItem(
                                chatUserEntity.getUsername(),
                                chatUserEntity.getId(),
                                chatServer.clientByUserId(chatUserEntity.getId()).isPresent()
                        )
                );
            }

        }

        panel.invalidate();
        panel.revalidate();
        panel.repaint();

    }

    public void handleUserConnectedEvent(@Observes UserConnectedEvent userConnectedEvent) {

        if (guiEnabled()) {
            reloadData();
        }

    }

    public void handleUserDisconnectedEvent(@Observes UserDisconnectedEvent userDisconnectedEvent) {

        if (guiEnabled()) {
            reloadData();
        }

    }

    public void handleAdminEvent (@Observes UserAdministrationEvent evt) {

        if (guiEnabled()) {
            reloadData();
        }

    }

}
