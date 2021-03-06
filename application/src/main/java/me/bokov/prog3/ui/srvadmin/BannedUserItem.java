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

public class BannedUserItem extends JPanel {

    private final String username;
    private final Long userId;

    public BannedUserItem(String username, Long userId) {
        this.username = username;
        this.userId = userId;
        initPanel();
    }

    private void initPanel() {

        JLabel usernameLabel = new JLabel(username);
        JButton unbanButton = new JButton("Unban");

        unbanButton.setBackground(Color.GREEN);
        unbanButton.setForeground(Color.WHITE);

        setLayout(new GridLayout(1, 2, 4, 4));

        add(usernameLabel);
        add(unbanButton);

        unbanButton.addActionListener(
                e -> {

                    try {

                        CDI.current().select(ChatServer.class).get()
                                .unbanUser(userId);

                        AsyncHelper.runAsync(
                                () -> {
                                    CDI.current().select(ServerAdministrationUIBean.class)
                                            .get().reloadData();
                                }
                        );

                    } catch (Exception exc) {

                        CDI.current().select(ErrorUIBean.class).get().showThrowable(exc);

                    }

                }
        );

    }

}
