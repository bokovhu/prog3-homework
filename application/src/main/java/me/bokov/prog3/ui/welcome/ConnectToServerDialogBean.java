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

import me.bokov.prog3.command.client.LoginCommand;
import me.bokov.prog3.command.client.RegisterCommand;
import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.service.client.ConnectionConfiguration;
import me.bokov.prog3.ui.ChatUIBean;
import me.bokov.prog3.ui.common.InputGroup;
import me.bokov.prog3.util.Config;
import me.bokov.prog3.util.I18N;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

@ApplicationScoped
public class ConnectToServerDialogBean {

    @Inject
    private I18N i18n;

    @Inject
    private ChatClient chatClient;

    @Inject
    private ChatUIBean chatUIBean;

    @Inject
    private Config config;

    public void show() {

        SwingUtilities.invokeLater(
                () -> {

                    JTextField serverHostnameTextField = new JTextField(40);

                    NumberFormat portFormat = NumberFormat.getInstance();
                    portFormat.setGroupingUsed(false);
                    portFormat.setParseIntegerOnly(true);
                    NumberFormatter portFormatter = new NumberFormatter(portFormat);
                    portFormatter.setValueClass(Integer.class);
                    portFormatter.setMinimum(0);
                    portFormatter.setMaximum(65535);
                    portFormatter.setAllowsInvalid(false);
                    JFormattedTextField serverPortTextField = new JFormattedTextField(portFormatter);

                    JTextField usernameTextField = new JTextField(40);

                    if (config.getLastConnectionHostname() != null) {
                        serverHostnameTextField.setText(config.getLastConnectionHostname());
                    }
                    if (config.getLastConnectionPort() != null) {
                        serverPortTextField.setValue(config.getLastConnectionPort());
                    }
                    if (config.getLastConnectionUsername() != null) {
                        usernameTextField.setText(config.getLastConnectionUsername());
                    }

                    int connectToServerResult = JOptionPane.showConfirmDialog(
                            null,
                            new Object[]{
                                    new InputGroup(i18n.getText("connect-to-server.server-hostname"), serverHostnameTextField),
                                    new InputGroup(i18n.getText("connect-to-server.server-port"), serverPortTextField),
                                    new InputGroup(i18n.getText("connect-to-server.username"), usernameTextField)
                            },
                            i18n.getText("connect-to-server.dialog-title"),
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    if (connectToServerResult == JOptionPane.OK_OPTION) {

                        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration();
                        connectionConfiguration.setUsername(usernameTextField.getText());
                        connectionConfiguration.setHost(serverHostnameTextField.getText());
                        connectionConfiguration.setPort(((Number) serverPortTextField.getValue()).intValue());

                        config.setLastConnectionHostname(connectionConfiguration.getHost());
                        config.setLastConnectionPort(connectionConfiguration.getPort());
                        config.setLastConnectionUsername(connectionConfiguration.getUsername());
                        config.save();

                        ChatClient.ConnectResult connectResult = chatClient.connect(connectionConfiguration);

                        JPasswordField passwordField = new JPasswordField(20);

                        switch (connectResult) {

                            case PROCEED_WITH_LOGIN:

                                int loginPromptResult = JOptionPane.showConfirmDialog(
                                        null,
                                        passwordField,
                                        i18n.getText("connect-to-server.login-with-your-password"),
                                        JOptionPane.OK_CANCEL_OPTION
                                );

                                if (loginPromptResult == JOptionPane.OK_OPTION) {

                                    Response loginResponse = chatClient.getServerEndpoint().login()
                                            .password(new String(passwordField.getPassword()))
                                            .execute();

                                    if (loginResponse.getCode() == LoginCommand.SUCCESS) {

                                        chatClient.setSessionValue("userId", loginResponse.getData().asJsonObject().getJsonNumber("userId").longValue());

                                        chatUIBean.initialize();
                                        chatUIBean.activate();

                                    } else if (loginResponse.getCode() == LoginCommand.INVALID_PASSWORD) {

                                        JOptionPane.showMessageDialog(
                                                null,
                                                i18n.getText("connect-to-server.invalid-password"),
                                                i18n.getText("connect-to-server.invalid-password-title"),
                                                JOptionPane.ERROR_MESSAGE
                                        );

                                    }

                                    if (loginResponse.getCode() != LoginCommand.SUCCESS) {
                                        chatClient.disconnect();
                                    }

                                }

                                break;
                            case PROCEED_WITH_REGISTRATION:

                                int registrationPromptResult = JOptionPane.showConfirmDialog(
                                        null,
                                        passwordField,
                                        i18n.getText("connect-to-server.create-a-password"),
                                        JOptionPane.OK_CANCEL_OPTION
                                );

                                if (registrationPromptResult == JOptionPane.OK_OPTION) {

                                    Response registerResponse = chatClient.getServerEndpoint().register()
                                            .password(new String(passwordField.getPassword()))
                                            .execute();

                                    if (registerResponse.getCode() == RegisterCommand.SUCCESS) {

                                        chatClient.setSessionValue("userId", registerResponse.getData().asJsonObject().getJsonNumber("userId").longValue());

                                        chatUIBean.initialize();
                                        chatUIBean.activate();

                                    } else {
                                        chatClient.disconnect();
                                    }

                                }

                                break;
                            case BANNED:

                                JOptionPane.showMessageDialog(
                                        null,
                                        i18n.getText("connect-to-server.you-are-banned"),
                                        i18n.getText("connect-to-server.you-are-banned-title"),
                                        JOptionPane.ERROR_MESSAGE
                                );

                                chatClient.disconnect();

                                break;
                        }

                    }

                }
        );

    }

}
