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

import me.bokov.prog3.common.net.ChatManager;
import me.bokov.prog3.event.UserBannedEvent;
import me.bokov.prog3.event.UserConnectedEvent;
import me.bokov.prog3.event.UserDisconnectedEvent;
import me.bokov.prog3.event.UserUnbannedEvent;
import me.bokov.prog3.server.dao.UserDao;
import me.bokov.prog3.ui.common.PanelList;
import me.bokov.prog3.ui.srvadmin.BannedUserItem;
import me.bokov.prog3.ui.srvadmin.UserItem;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.Set;

@ApplicationScoped
public class ServerAdministrationUIBean extends ScreenBase {

    private PanelList<UserItem> usersList;
    private PanelList<BannedUserItem> bannedUsersList;

    @Inject
    private Logger logger;

    @Inject
    private UserDao userDao;

    @Inject
    private ChatManager chatManager;

    @Override
    public void initialize() {

        panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 0;
        titleGbc.gridy = 0;
        titleGbc.gridwidth = 3;
        titleGbc.gridheight = 1;

        panel.add(new JLabel("Server is running!"), titleGbc);

        GridBagConstraints usersListGbc = new GridBagConstraints();
        usersListGbc.gridx = 0;
        usersListGbc.gridy = 1;
        usersListGbc.gridwidth = 1;
        usersListGbc.gridheight = 1;

        usersList = new PanelList<>();
        usersList.setBackground(Color.LIGHT_GRAY);

        panel.add(usersList, usersListGbc);

        GridBagConstraints bannedUsersListGbc = new GridBagConstraints();
        bannedUsersListGbc.gridx = 1;
        bannedUsersListGbc.gridy = 1;
        bannedUsersListGbc.gridwidth = 1;
        bannedUsersListGbc.gridheight = 1;

        bannedUsersList = new PanelList<>();
        bannedUsersList.setBackground(Color.LIGHT_GRAY);

        panel.add(bannedUsersList, bannedUsersListGbc);

        reloadData();

    }

    public void reloadData() {

        Set<String> currentlyConnectedUserNames = chatManager.getCurrentRunningServer().getConnectedUsernames();

        usersList.clearPanels();
        bannedUsersList.clearPanels();
        userDao.getAllUsers()
                .forEach(
                        userJson -> {

                            logger.info(userJson.toString());

                            switch (userJson.getString("banState")) {
                                case "NOT_BANNED":
                                    usersList.addPanel(
                                            new UserItem(
                                                    userJson.getString("username"),
                                                    currentlyConnectedUserNames.contains(userJson.getString("username"))
                                            )
                                    );
                                    break;
                                default:
                                    bannedUsersList.addPanel(
                                            new BannedUserItem(userJson.getString("username"))
                                    );
                                    break;
                            }

                        }
                );

        panel.invalidate();
        panel.revalidate();
        panel.repaint();

    }

    public void handleUserConnectedEvent(@Observes UserConnectedEvent userConnectedEvent) {

        reloadData();

    }

    public void handleUserBannedEvent(@Observes UserBannedEvent userBannedEvent) {

        reloadData();

    }

    public void handleUserDisconnectedEvent(@Observes UserDisconnectedEvent userDisconnectedEvent) {

        reloadData();

    }

    public void handleUserUnbannedEvent(@Observes UserUnbannedEvent userUnbannedEvent) {

        reloadData();

    }

}
