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
import me.bokov.prog3.server.ServerConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.*;

@ApplicationScoped
public class WelcomeUIBean extends ScreenBase {

    private JButton connectToServerButton;
    private JButton startNewServerButton;

    @Inject
    private ChatManager chatManager;

    @Inject
    private ServerAdministrationUIBean serverAdministrationUIBean;

    public void initialize() {

        panel = new JPanel();

        panel.add(new JLabel(i18n.getText("welcome.title")));

        connectToServerButton = new JButton(i18n.getText("welcome.connect-to-server"));
        startNewServerButton = new JButton(i18n.getText("welcome.start-new-server"));

        panel.add(connectToServerButton);
        panel.add(startNewServerButton);

        connectToServerButton.addActionListener(
                e -> JOptionPane.showMessageDialog(null, "Connect to server clicked!")
        );
        startNewServerButton.addActionListener(
                e -> {

                    JFormattedTextField serverPortTextField = new JFormattedTextField(Integer.valueOf(14672));
                    serverPortTextField.setColumns(5);

                    JCheckBox passwordEnabledCheckBox = new JCheckBox("Password enabled", false);
                    JPasswordField serverPasswordField = new JPasswordField("", 24);
                    passwordEnabledCheckBox.addChangeListener(
                            changeEvent -> {
                                if (passwordEnabledCheckBox.isSelected()) serverPasswordField.setEnabled(true);
                                else serverPasswordField.setEnabled(false);
                            }
                    );

                    int startNewServerResult = JOptionPane.showConfirmDialog(
                            null,
                            new Object[] {
                                    "Server port: ", serverPortTextField,
                                    passwordEnabledCheckBox,
                                    "Server password: ", serverPasswordField
                            },
                            "Start new server",
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (startNewServerResult == JOptionPane.OK_OPTION) {

                        ServerConfig serverConfig = new ServerConfig();

                        serverConfig.setPassword(new String(serverPasswordField.getPassword()));
                        serverConfig.setPasswordEnabled(passwordEnabledCheckBox.isSelected());
                        serverConfig.setPort(((Number) serverPortTextField.getValue()).intValue());

                        chatManager.startNewServer(serverConfig);

                    }

                    serverAdministrationUIBean.initialize();
                    serverAdministrationUIBean.activate();

                }
        );

    }

}
