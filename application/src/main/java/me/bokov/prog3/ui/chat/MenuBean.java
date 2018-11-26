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

import me.bokov.prog3.command.CommandException;
import me.bokov.prog3.command.client.AdminCommand;
import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.service.common.ChatUserVO;
import me.bokov.prog3.ui.ApplicationUIBean;
import me.bokov.prog3.ui.ErrorUIBean;
import me.bokov.prog3.ui.common.InputGroup;
import me.bokov.prog3.util.Config;
import me.bokov.prog3.util.I18N;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class MenuBean {
    
    private JMenuBar menuBar;
    
    @Inject
    private ApplicationUIBean applicationUIBean;
    
    @Inject
    private I18N i18n;
    
    @Inject
    private ChatClient chatClient;
    
    @Inject
    private ErrorUIBean errorUIBean;

    @Inject
    private Config config;

    private void doAdminAuthentication () {
        
        JPasswordField passwordField = new JPasswordField(20);
        
        int result = JOptionPane.showConfirmDialog(
                null,
                new InputGroup(i18n.getText("menu.admin.authenticate.password"), passwordField),
                i18n.getText("menu.admin.authenticate.title"),
                JOptionPane.OK_CANCEL_OPTION
        );
        
        if (result == JOptionPane.OK_OPTION) {
            
            try {

                chatClient.getServerEndpoint()
                        .admin()
                        .authenticate(new String(passwordField.getPassword()))
                        .execute();

                JOptionPane.showMessageDialog(null, i18n.getText("menu.admin.authenticate.successful"));
                
            } catch (CommandException ce) {
                
                switch (ce.getErrorCode()) {
                    case AdminCommand
                            .INVALID_PASSWORD:
                        errorUIBean.showErrorMessage(i18n.getText("menu.admin.authenticate.invalid-password"));
                        break;
                }
                
            }
            
        }
        
    }
    
    private void doAdminBan () {
        
        try {
            
            Response usersResponse = chatClient.getServerEndpoint()
                    .admin()
                    .getAllUsers()
                    .execute();

            List <UserItem> notBannedUsers = new ArrayList<>();
            
            usersResponse.getData().asJsonObject()
                    .getJsonArray("users")
                    .forEach(
                            user -> {

                                JsonObject userJson = user.asJsonObject();
                                if (!userJson.getBoolean("isBanned")) {
                                    ChatUserVO vo = new ChatUserVO();
                                    notBannedUsers.add(
                                            new UserItem(
                                                    userJson.getString("username"),
                                                    userJson.getJsonNumber("id").longValue()
                                            )
                                    );
                                }
                                
                            }
                    );
            
            if (!notBannedUsers.isEmpty()) {

                UserItem toBan = (UserItem) JOptionPane.showInputDialog(
                        null,
                        i18n.getText("menu.admin.ban.select-user"),
                        i18n.getText("menu.admin.ban.select-user"),
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        notBannedUsers.toArray(),
                        notBannedUsers.get(0)
                );
                
                if (toBan != null) {
                    
                    chatClient.getServerEndpoint()
                            .admin().banUser(toBan.userId).execute();
                    
                }

            }
            
        } catch (CommandException ce) {
            
            switch (ce.getErrorCode()) {
                case AdminCommand.NOT_AUTHENTICATED:
                    errorUIBean.showErrorMessage(i18n.getText("menu.admin.must-authenticate-first"));
                    break;
            }
            
        }
        
    }
    
    private void doAdminUnban () {

        try {

            Response usersResponse = chatClient.getServerEndpoint()
                    .admin()
                    .getAllUsers()
                    .execute();

            List <UserItem> bannedUsers = new ArrayList<>();

            usersResponse.getData().asJsonObject()
                    .getJsonArray("users")
                    .forEach(
                            user -> {

                                JsonObject userJson = user.asJsonObject();
                                if (userJson.getBoolean("isBanned")) {
                                    ChatUserVO vo = new ChatUserVO();
                                    bannedUsers.add(
                                            new UserItem(
                                                    userJson.getString("username"),
                                                    userJson.getJsonNumber("id").longValue()
                                            )
                                    );
                                }

                            }
                    );

            if (!bannedUsers.isEmpty()) {

                UserItem toUnban = (UserItem) JOptionPane.showInputDialog(
                        null,
                        i18n.getText("menu.admin.unban.select-user"),
                        i18n.getText("menu.admin.unban.select-user"),
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        bannedUsers.toArray(),
                        bannedUsers.get(0)
                );

                if (toUnban != null) {

                    chatClient.getServerEndpoint()
                            .admin().unbanUser(toUnban.userId).execute();

                }

            }

        } catch (CommandException ce) {

            switch (ce.getErrorCode()) {
                case AdminCommand.NOT_AUTHENTICATED:
                    errorUIBean.showErrorMessage(i18n.getText("menu.admin.must-authenticate-first"));
                    break;
            }

        }
        
    }
    
    private void initAdminMenu () {

        JMenu menu = new JMenu(i18n.getText("menu.admin"));

        JMenuItem authenticateItem = new JMenuItem(i18n.getText("menu.admin.authenticate"));
        authenticateItem.addActionListener(e -> doAdminAuthentication());
        
        JMenuItem banItem = new JMenuItem(i18n.getText("menu.admin.ban"));
        banItem.addActionListener(e -> doAdminBan());
        
        JMenuItem unbanItem = new JMenuItem(i18n.getText("menu.admin.unban"));
        unbanItem.addActionListener(e -> doAdminUnban());

        menu.add(authenticateItem);
        menu.add(banItem);
        menu.add(unbanItem);
        
        menuBar.add(menu);

    }

    private void changeLanguageTo (String code) {

        config.setUserLocale(new Locale(code));
        config.save();

        JOptionPane.showMessageDialog(
                null,
                i18n.getText("menu.language.will-take-effect-after-restart")
        );

    }
    
    private void initLanguageMenu () {
        
        JMenu menu = new JMenu(i18n.getText("menu.language"));

        JMenuItem englishItem = new JMenuItem("English");
        if (config.getUserLocale().getLanguage().toLowerCase().equals("en")) {
            englishItem.setFont(
                    englishItem.getFont().deriveFont(Font.BOLD)
            );
        }
        englishItem.addActionListener(e -> changeLanguageTo("en"));

        JMenuItem hungarianItem = new JMenuItem("Magyar");
        if (config.getUserLocale().getLanguage().toLowerCase().equals("hu")) {
            hungarianItem.setFont(
                    hungarianItem.getFont().deriveFont(Font.BOLD)
            );
        }
        hungarianItem.addActionListener(e -> changeLanguageTo("hu"));

        menu.add(englishItem);
        menu.add(hungarianItem);

        menuBar.add(menu);

    }
    
    public void init () {
        
        menuBar = new JMenuBar();

        if (chatClient.isConnected()) {
            initAdminMenu();
        }
        
        initLanguageMenu();

        applicationUIBean.getApplicationFrame().setJMenuBar(menuBar);
        
    }

    public static final class UserItem {
        
        private final String username;
        private final Long userId;

        public UserItem(String username, Long userId) {
            this.username = username;
            this.userId = userId;
        }

        public String toString () {
            return username;
        }
        
    }
    
}
