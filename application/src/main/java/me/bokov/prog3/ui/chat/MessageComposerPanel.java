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

import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.ui.ChatUIBean;
import me.bokov.prog3.util.I18N;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.enterprise.inject.spi.CDI;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Base64;

public class MessageComposerPanel extends JPanel {

    private I18N i18n;

    private JButton sendFileButton;
    private JButton sendImageButton;

    // TODO: When the sendButton is clicked, the message should actually be sent
    // TODO: When the sendFileButton is clicked, a file chooser should appear, and the file should be actually sent
    // TODO: When the sendImageButton is clicked, a file chooser should appear, and the image should be actually sent

    private JTextField messageTextField;
    private JButton sendButton;

    private void initButtons() {

        sendFileButton = new JButton(i18n.getText("chat.message-composer.send-file"));
        sendImageButton = new JButton(i18n.getText("chat.message-composer.send-image"));

        sendImageButton.addActionListener(
                e -> {

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.addChoosableFileFilter(
                            new FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "gif", "bmp")
                    );
                    int fileChooserResult = fileChooser.showOpenDialog(null);

                    if (fileChooserResult == JFileChooser.APPROVE_OPTION) {

                        try {

                            File imageFile = fileChooser.getSelectedFile();
                            String imageContentBase64 = Base64.getEncoder().encodeToString(
                                    FileUtils.readFileToByteArray(imageFile)
                            );
                            String imageExtension = FilenameUtils.getExtension(imageFile.getName());

                            CDI.current().select(ChatClient.class).get()
                                    .getServerEndpoint()
                                    .sendImage()
                                    .roomId(
                                            CDI.current().select(ChatUIBean.class).get()
                                                    .getActiveChatRoomTab().getRoomId()
                                    )
                                    .imageContent(imageContentBase64)
                                    .extension(imageExtension)
                                    .executeWithoutAnswer();

                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }

                    }

                }
        );

        sendButton = new JButton(i18n.getText("chat.message-composer.send"));

        sendButton.addActionListener(
                e -> {

                    ChatClient chatClient = CDI.current().select(ChatClient.class).get();

                    chatClient.getServerEndpoint()
                            .sendMessage()
                            .messageText(messageTextField.getText())
                            .roomId(
                                    CDI.current().select(ChatUIBean.class).get()
                                            .getActiveChatRoomTab().getRoomId()
                            ).executeWithoutAnswer();

                }
        );

    }

    private void initUpperPart() {

        JPanel buttonsContainerPanel = new JPanel();
        buttonsContainerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));

        buttonsContainerPanel.add(sendFileButton);
        buttonsContainerPanel.add(sendImageButton);

        GridBagConstraints panelGbc = new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);

        add(buttonsContainerPanel, panelGbc);

    }

    private void initBottomPart() {

        JPanel messageInputAndSendContainerPanel = new JPanel();

        messageInputAndSendContainerPanel.setLayout(new GridBagLayout());

        messageTextField = new JTextField();
        messageTextField.setToolTipText(i18n.getText("chat.message-composer.your-message-here"));

        GridBagConstraints textFieldGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, 10, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);

        messageInputAndSendContainerPanel.add(messageTextField, textFieldGbc);


        GridBagConstraints buttonGbc = new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0);

        messageInputAndSendContainerPanel.add(sendButton, buttonGbc);

        GridBagConstraints panelGbc = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);

        add(messageInputAndSendContainerPanel, panelGbc);

    }

    private void initPanel() {

        setLayout(new GridBagLayout());

        initButtons();

        initUpperPart();
        initBottomPart();

    }

    public MessageComposerPanel() {

        i18n = CDI.current().select(I18N.class).get();

        initPanel();

    }

}
