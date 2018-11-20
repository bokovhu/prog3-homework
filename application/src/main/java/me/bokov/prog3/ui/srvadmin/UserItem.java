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

import me.bokov.prog3.server.dao.UserDao;

import javax.enterprise.inject.spi.CDI;
import javax.swing.*;
import java.awt.*;

public class UserItem extends JPanel {

    private final String username;
    private final boolean isConnected;

    private void initPanel () {

        JLabel usernameLabel = new JLabel(username);
        if (!isConnected) {
            usernameLabel.setForeground(Color.GRAY);
        }
        JButton banButton = new JButton("Ban");

        banButton.setBackground(Color.RED);
        banButton.setForeground(Color.WHITE);

        setLayout(new FlowLayout());

        add (usernameLabel);
        add(banButton);

        banButton.addActionListener(
                (evt) -> {

                    UserDao userDao = CDI.current().select(UserDao.class).get();
                    userDao.banUserByIp(
                            userDao.getUserIdByUsername(username)
                    );

                }
        );

    }

    public UserItem(String username, boolean isConnected) {
        this.username = username;
        this.isConnected = isConnected;
        initPanel();
    }

}
