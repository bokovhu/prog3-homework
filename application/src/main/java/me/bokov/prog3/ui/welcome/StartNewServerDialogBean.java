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

package me.bokov.prog3.ui.welcome;

import me.bokov.prog3.service.ChatServer;
import me.bokov.prog3.service.server.ServerConfiguration;
import me.bokov.prog3.ui.ServerAdministrationUIBean;
import me.bokov.prog3.ui.common.InputGroup;
import me.bokov.prog3.util.I18N;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.*;

@ApplicationScoped
public class StartNewServerDialogBean {

    @Inject
    private ServerAdministrationUIBean serverAdministrationUIBean;

    @Inject
    private ChatServer chatServer;

    @Inject
    private I18N i18n;

    public void show() {

        SwingUtilities.invokeLater(
                () -> {

                    JFormattedTextField serverPortTextField = new JFormattedTextField(14672);
                    serverPortTextField.setColumns(5);

                    JCheckBox passwordEnabledCheckBox = new JCheckBox(i18n.getText("start-new-server.password-enabled"), false);
                    JPasswordField serverPasswordField = new JPasswordField("", 24);
                    passwordEnabledCheckBox.addChangeListener(
                            changeEvent -> {
                                if (passwordEnabledCheckBox.isSelected()) serverPasswordField.setEnabled(true);
                                else serverPasswordField.setEnabled(false);
                            }
                    );

                    int startNewServerResult = JOptionPane.showConfirmDialog(
                            null,
                            new Object[]{
                                    new InputGroup(i18n.getText("start-new-server.server-port"), serverPortTextField),
                                    passwordEnabledCheckBox,
                                    new InputGroup(i18n.getText("start-new-server.server-password"), serverPasswordField)
                            },
                            i18n.getText("start-new-server.dialog-title"),
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (startNewServerResult == JOptionPane.OK_OPTION) {

                        ServerConfiguration serverConfiguration = new ServerConfiguration();

                        serverConfiguration.setPassword(new String(serverPasswordField.getPassword()));
                        serverConfiguration.setPasswordEnabled(passwordEnabledCheckBox.isSelected());
                        serverConfiguration.setPort(((Number) serverPortTextField.getValue()).intValue());

                        chatServer.start(serverConfiguration);

                        serverAdministrationUIBean.initialize();
                        serverAdministrationUIBean.activate();

                    }

                }
        );
    }

}
