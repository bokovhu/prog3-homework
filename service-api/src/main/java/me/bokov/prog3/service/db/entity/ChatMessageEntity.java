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

package me.bokov.prog3.service.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import me.bokov.prog3.service.db.BaseEntity;

import java.util.Date;

/**
 * Database representation of a chat message
 *
 */
@DatabaseTable (tableName = "chat_message")
public class ChatMessageEntity extends BaseEntity {

    /**
     * The user that sent the message
     */
    @DatabaseField (columnName = "sent_by_user_id", foreign = true, canBeNull = false)
    private ChatUserEntity sentBy;

    /**
     * The room that this message was sent to
     */
    @DatabaseField (columnName = "room_id", foreign = true, canBeNull = false)
    private ChatRoomEntity room;

    /**
     * The exact timestamp this message was sent at
     */
    @DatabaseField (columnName = "sent_date", canBeNull = false)
    private Date sentDate;

    /**
     * True, if this is a text message, false otherwise
     */
    @DatabaseField (columnName = "is_text_message", canBeNull = false)
    private boolean isTextMessage;

    /**
     * True, if this is a file message, false otherwise
     */
    @DatabaseField (columnName = "is_file_message", canBeNull = false)
    private boolean isFileMessage;

    /**
     * True, if this is an image message, false otherwise
     */
    @DatabaseField (columnName = "is_image_message")
    private boolean isImageMessage;

    /**
     * The text of the message in case of text messages
     */
    @DatabaseField (columnName = "message_text", canBeNull = true)
    private String messageText;

    /**
     * The file ID in case of file and image messages
     */
    @DatabaseField (columnName = "file_id", canBeNull = true)
    private String fileId;

    /**
     * The name of the sent file in case of file messages
     */
    @DatabaseField (columnName = "file_name", canBeNull = true)
    private String fileName;

    /**
     * The size of the sent file in bytes in case of file messages
     */
    @DatabaseField (columnName = "file_size", canBeNull = true)
    private Long fileSize;

    /**
     * The file extension of the sent image in case of image messages
     */
    @DatabaseField (columnName = "image_extension", canBeNull = true)
    private String imageExtension;

    public ChatUserEntity getSentBy() {
        return sentBy;
    }

    public void setSentBy(ChatUserEntity sentBy) {
        this.sentBy = sentBy;
    }

    public ChatRoomEntity getRoom() {
        return room;
    }

    public void setRoom(ChatRoomEntity room) {
        this.room = room;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isTextMessage() {
        return isTextMessage;
    }

    public void setTextMessage(boolean textMessage) {
        isTextMessage = textMessage;
    }

    public boolean isFileMessage() {
        return isFileMessage;
    }

    public void setFileMessage(boolean fileMessage) {
        isFileMessage = fileMessage;
    }

    public boolean isImageMessage() {
        return isImageMessage;
    }

    public void setImageMessage(boolean imageMessage) {
        isImageMessage = imageMessage;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public void setImageExtension(String imageExtension) {
        this.imageExtension = imageExtension;
    }
}
