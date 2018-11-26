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

package me.bokov.prog3.service.common;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.Serializable;
import java.util.Date;

public class ChatMessageVO implements Serializable {

    private Long id;
    private ChatUserVO sentBy;
    private ChatRoomVO room;
    private Date sentDate;
    private boolean isTextMessage;
    private boolean isFileMessage;
    private boolean isImageMessage;
    private String fileName;
    private String fileId;
    private Long fileSize;
    private String imageExtension;
    private String messageText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatUserVO getSentBy() {
        return sentBy;
    }

    public void setSentBy(ChatUserVO sentBy) {
        this.sentBy = sentBy;
    }

    public ChatRoomVO getRoom() {
        return room;
    }

    public void setRoom(ChatRoomVO room) {
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public JsonObject toJson () {

        JsonObjectBuilder job = Json.createObjectBuilder()
                .add("id", getId())
                .add("isTextMessage", isTextMessage())
                .add("isFileMessage", isFileMessage())
                .add("isImageMessage", isImageMessage())
                .add("sentDate", sentDate.getTime());

        if (getSentBy() != null) {
            job.add("sentBy", getSentBy().toJson());
        }
        if (getRoom() != null) {
            job.add("room", getRoom().toJson());
        }

        if (isFileMessage()) {
            job.add("fileName", getFileName())
                    .add("fileSize", getFileSize());
        }
        if (isImageMessage()) {
            job.add("imageExtension", getImageExtension());
        }
        if (isFileMessage() || isImageMessage()) {
            job.add("fileId", getFileId());
        }
        if (isTextMessage()) {
            job.add("messageText", getMessageText());
        }

        return job.build();

    }

}
