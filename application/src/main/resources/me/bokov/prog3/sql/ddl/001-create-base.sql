CREATE SEQUENCE id_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE chat_user (
    id         bigint                                               NOT NULL PRIMARY KEY,
    username   varchar(255)                                         NOT NULL UNIQUE,
    ban_state  varchar(63)                                          NOT NULL DEFAULT 'NOT_BANNED',
    banned_ip  varchar(255)                                         NULL
);

CREATE TABLE chat_room (
    id            bigint       NOT NULL PRIMARY KEY,
    name          varchar(255) NOT NULL UNIQUE,
    owner_user_id bigint       NOT NULL,

    FOREIGN KEY (owner_user_id) REFERENCES chat_user (id)
);

CREATE TABLE chat_user_room_membership (
    chat_user_id bigint NOT NULL,
    chat_room_id bigint NOT NULL,

    PRIMARY KEY (chat_user_id, chat_room_id),
    FOREIGN KEY (chat_user_id) REFERENCES chat_user (id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_room (id)
);

CREATE TABLE chat_message (
    id              bigint                         NOT NULL PRIMARY KEY,
    sent_by_user_id bigint                         NOT NULL,
    sent_to_room_id bigint                         NOT NULL,
    message_type    ENUM ('TEXT', 'FILE', 'IMAGE') NOT NULL DEFAULT 'TEXT',
    original_text   varchar(65535)                 NULL,
    display_text    varchar(65535)                 NULL,
    file_path       varchar(1023)                  NULL,
    file_hash       varchar(255)                   NULL,
    file_size       int                            NULL,
    sent_on         TIMESTAMP                      NOT NULL,

    FOREIGN KEY (sent_to_room_id) REFERENCES chat_room (id),
    FOREIGN KEY (sent_by_user_id) REFERENCES chat_user (id)
);