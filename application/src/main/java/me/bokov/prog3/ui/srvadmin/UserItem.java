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

import com.j256.ormlite.dao.Dao;
import me.bokov.prog3.event.UserBannedEvent;
import me.bokov.prog3.service.Database;
import me.bokov.prog3.service.db.entity.ChatUserEntity;
import me.bokov.prog3.ui.ErrorUIBean;

import javax.enterprise.inject.spi.CDI;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class UserItem extends JPanel {

    private final String username;
    private final boolean isConnected;

    public UserItem(String username, boolean isConnected) {
        this.username = username;
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

                        Dao<ChatUserEntity, Long> chatUserDao = CDI.current().select(Database.class).get().getChatUserDao();

                        ChatUserEntity chatUserEntity = chatUserDao.queryForEq("username", username).get(0);
                        chatUserEntity.setBanState("BANNED_BY_IP");

                        chatUserDao.update(chatUserEntity);

                        CDI.current().getBeanManager().fireEvent(
                                new UserBannedEvent(
                                        new Date(),
                                        "USER_BANNED",
                                        username,
                                        true,
                                        false
                                )
                        );

                    } catch (Exception e) {

                        CDI.current().select(ErrorUIBean.class).get().showThrowable(e);

                    }

                }
        );

    }

}
