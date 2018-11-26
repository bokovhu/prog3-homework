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

package me.bokov.prog3.ui.srvadmin;

import me.bokov.prog3.AsyncHelper;
import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.ui.ErrorUIBean;
import me.bokov.prog3.ui.ServerAdministrationUIBean;

import javax.enterprise.inject.spi.CDI;
import javax.swing.*;
import java.awt.*;

public class UserItem extends JPanel {

    private final String username;
    private final Long userId;
    private final boolean isConnected;

    public UserItem(String username, Long userId, boolean isConnected) {
        this.username = username;
        this.userId = userId;
        this.isConnected = isConnected;
        initPanel();
    }

    private void initPanel() {

        JLabel usernameLabel = new JLabel(username);
        if (!isConnected) {
            usernameLabel.setForeground(Color.GRAY);
        }
        JButton banButton = new JButton("Ban");

        banButton.setBackground(Color.RED);
        banButton.setForeground(Color.WHITE);

        setLayout(new GridLayout(1, 2, 4, 4));

        add(usernameLabel);
        add(banButton);

        banButton.addActionListener(
                (evt) -> {

                    try {

                        CDI.current().select(ChatServer.class).get()
                                .banUser(userId);

                        AsyncHelper.runAsync(
                                () -> {
                                    CDI.current().select(ServerAdministrationUIBean.class)
                                            .get().reloadData();
                                }
                        );

                    } catch (Exception e) {

                        CDI.current().select(ErrorUIBean.class).get().showThrowable(e);

                    }

                }
        );

    }

}
