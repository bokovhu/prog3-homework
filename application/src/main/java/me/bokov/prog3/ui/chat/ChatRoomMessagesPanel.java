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

import me.bokov.prog3.command.response.Response;
import me.bokov.prog3.service.ChatClient;
import me.bokov.prog3.ui.ImageStoreBean;
import me.bokov.prog3.util.DateTimeUtils;
import me.bokov.prog3.util.I18N;
import org.apache.commons.io.FileUtils;

import javax.enterprise.inject.spi.CDI;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.MessageFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class ChatRoomMessagesPanel extends JPanel {

    private static final ImageIcon CAT_1_IMAGE = new ImageIcon(".test-images/cat-1.jpg");
    private static final ImageIcon CAT_2_IMAGE = new ImageIcon(".test-images/cat-2.jpg");
    private static final ImageIcon CAT_3_IMAGE = new ImageIcon(".test-images/cat-3.jpg");
    private static final ImageIcon CAT_4_IMAGE = new ImageIcon(".test-images/cat-4.jpg");

    private I18N i18n;

    private JTextPane chatTextPane;
    private List<MessageItem> messageItems = new ArrayList<>();

    private final Long roomId;

    private void fetchMessages () {

        ChatClient chatClient = CDI.current().select(ChatClient.class).get();

        Response response = chatClient.getServerEndpoint()
                .getMessages()
                .roomId(roomId)
                .execute();

        JsonArray messagesArray = response.getData().asJsonObject()
                .getJsonArray("messages");

        messagesArray.forEach(
                msg -> addMessage(msg.asJsonObject())
        );

    }

    private void initPanel() {

        // TODO: Should retrieve the messages from the chat room
        // TODO: Should update when a NewMessageEvent is fired

        setBackground(Color.WHITE);
        setLayout(new GridLayout(1, 1));

        chatTextPane = new JTextPane();
        add(new JScrollPane(chatTextPane));

        chatTextPane.setEditable(false);

        fetchMessages();

    }

    public ChatRoomMessagesPanel(Long roomId) {
        this.roomId = roomId;

        i18n = CDI.current().select(I18N.class).get();

        initPanel();

    }

    public void addMessage (JsonObject messageObject) {

        ImageStoreBean imageStoreBean = CDI.current().select(ImageStoreBean.class).get();

        if (messageObject.getBoolean("isTextMessage")) {

                    addMessageItem(
                            ChatRoomMessagesPanel.MessageItem.text(
                                    new Date(messageObject.getJsonNumber("sentDate").longValue()),
                                    messageObject.getJsonObject("sentBy").getString("username"),
                                    messageObject.getString("messageText")
                            )
                    );

        }

        if (messageObject.getBoolean("isFileMessage")) {

                    addMessageItem(
                            ChatRoomMessagesPanel.MessageItem.file(
                                    new Date(messageObject.getJsonNumber("sentDate").longValue()),
                                    messageObject.getJsonObject("sentBy").getString("username"),
                                    messageObject.getString("fileName"),
                                    messageObject.getJsonNumber("fileSize").longValue(),
                                    messageObject.getString("fileId")
                            )
                    );

        }

        if (messageObject.getBoolean("isImageMessage")) {

                    addMessageItem(
                            ChatRoomMessagesPanel.MessageItem.image(
                                    new Date(messageObject.getJsonNumber("sentDate").longValue()),
                                    messageObject.getJsonObject("sentBy").getString("username"),
                                    imageStoreBean.getImageIconByFileId(
                                            messageObject.getString("fileId"),
                                            messageObject.getString("imageExtension")
                                    )
                            )
                    );

        }

    }

    public void addMessageItem(MessageItem mi) {

        messageItems.add(mi);
        updateMessages();

    }

    public void updateMessages() {

        chatTextPane.setStyledDocument(new DefaultStyledDocument());

        StyledDocument sd = chatTextPane.getStyledDocument();

        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_LEFT);
        StyleConstants.setFontFamily(attributeSet, "Courier New");
        chatTextPane.setParagraphAttributes(attributeSet, true);

        // messageItems.forEach(mi -> mi.modifyDocument(jTextPane, sd));
        for (MessageItem mi : messageItems) {
            mi.modifyDocument(chatTextPane, sd);
        }

    }

    public static final class MessageItem {

        private I18N i18n = CDI.current().select(I18N.class).get();

        private Date sentDate;
        private String sentBy;

        public enum Type {
            TEXT_MESSAGE,
            FILE,
            IMAGE
        }

        private Type type;

        private String fileName;
        private Long fileSize;

        private ImageIcon image;

        private String messageText;

        private String fileId;

        public static MessageItem text(Date date, String by, String text) {

            MessageItem item = new MessageItem();

            item.sentDate = date;
            item.sentBy = by;
            item.type = Type.TEXT_MESSAGE;
            item.messageText = text;

            return item;

        }

        public static MessageItem file(Date date, String by, String fileName, Long fileSize, String fileId) {

            MessageItem item = new MessageItem();

            item.sentDate = date;
            item.sentBy = by;
            item.type = Type.FILE;
            item.fileName = fileName;
            item.fileSize = fileSize;
            item.fileId = fileId;

            return item;

        }

        public static MessageItem image(Date date, String by, ImageIcon img) {

            MessageItem item = new MessageItem();

            item.sentDate = date;
            item.sentBy = by;
            item.type = Type.IMAGE;
            item.image = img;

            return item;

        }

        private String getDateText() {

            Date currentDate = new Date();

            StringBuilder sb = new StringBuilder();

            int dayDiff = DateTimeUtils.differenceInDays(sentDate, currentDate);

            if (dayDiff == 0) {

                // today
                sb.append(i18n.getText("chat.message.today"));

            } else if (dayDiff == 1) {

                // yesterday
                sb.append(i18n.getText("chat.message.yesterday"));

            } else if (dayDiff <= 7) {

                sb.append(MessageFormat.format(i18n.getText("chat.message.n-days-ago"), dayDiff));

            } else {

                sb.append(sentDate.toString());

            }

            sb.append(" ")
                    .append(sentDate.toInstant().atOffset(ZoneOffset.UTC).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            return sb.toString();

        }

        private void addDateAndSentBy(StyledDocument sd) {

            Style dateStyle = sd.addStyle(null, null);
            StyleConstants.setForeground(dateStyle, Color.BLUE);

            Style sentByStyle = sd.addStyle(null, null);
            StyleConstants.setForeground(sentByStyle, Color.ORANGE);

            try {

                sd.insertString(sd.getLength(), getDateText(), dateStyle);
                sd.insertString(sd.getLength(), " " + sentBy + " > ", sentByStyle);

            } catch (BadLocationException e) {
                e.printStackTrace();
            }


        }

        private void addTextToDocument(StyledDocument sd) {

            addDateAndSentBy(sd);

            try {
                sd.insertString(sd.getLength(), messageText + "\n", null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }

        private void doDownloadAndSaveFile() {

            try {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(fileName));

                int fileChooserResult = fileChooser.showSaveDialog(null);
                if (fileChooserResult == JFileChooser.APPROVE_OPTION) {

                    File saveToFile = fileChooser.getSelectedFile();

                    ChatClient chatClient = CDI.current().select(ChatClient.class).get();
                    Response downloadResponse = chatClient.getServerEndpoint().download()
                            .fileId(fileId)
                            .execute();

                    FileUtils.writeByteArrayToFile(
                            saveToFile,
                            Base64.getDecoder().decode(downloadResponse.getData().asJsonObject().getString("content"))
                    );

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void addFileToDocument(JTextPane pane, StyledDocument sd) {

            addDateAndSentBy(sd);

            JButton fileButton = new JButton(fileName + " (" + FileUtils.byteCountToDisplaySize(fileSize) + ")");
            fileButton.setBorder(BorderFactory.createEmptyBorder());
            fileButton.setBackground(Color.WHITE);
            fileButton.addActionListener(e -> doDownloadAndSaveFile());

            pane.insertComponent(fileButton);

            try {
                sd.insertString(sd.getLength(), "\n", null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }

        private void addImageToDocument(JTextPane pane, StyledDocument sd) {

            addDateAndSentBy(sd);

            JLabel imageLabel = new JLabel(image);
            imageLabel.setPreferredSize(new Dimension(128, 128));
            imageLabel.setMaximumSize(new Dimension(128, 128));

            imageLabel.addMouseListener(
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            super.mouseClicked(e);
                            JOptionPane.showMessageDialog(null, new JLabel(image), "", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
            );

            pane.insertComponent(imageLabel);

            try {
                sd.insertString(sd.getLength(), "\n", null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }

        public void modifyDocument(JTextPane pane, StyledDocument sd) {

            switch (this.type) {

                case TEXT_MESSAGE:
                    addTextToDocument(sd);
                    break;
                case FILE:
                    addFileToDocument(pane, sd);
                    break;
                case IMAGE:
                    addImageToDocument(pane, sd);
                    break;
            }

        }

    }

}
