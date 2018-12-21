package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage extends AbstractRepresentation implements Comparable<ChatMessage> {

    private Link chatRoom = null;
    private Integer index = null;
    private User sender = null;
    private String content = null;
    private OffsetDateTime timestamp = null;

    public ChatMessage() {

    }

    public ChatMessage(Link chatRoom, Integer index, User sender, String content, OffsetDateTime timestamp) {
        this.chatRoom = chatRoom;
        this.index = index;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public ChatMessage(ChatMessage clone) {
        this.chatRoom = ifNonNull(clone.chatRoom, Link::new);
        this.index = clone.index;
        this.sender = ifNonNull(clone.sender, User::new);
        this.content = clone.content;
        this.timestamp = clone.timestamp;
    }


    @JsonProperty("chatRoom")
    @JacksonXmlProperty(localName = "chatRoom")
    public Link getChatRoom() {
        return chatRoom;
    }

    public ChatMessage setChatRoom(Link chatRoom) {
        this.chatRoom = chatRoom;
        return this;
    }


    @JsonProperty("index")
    @JacksonXmlProperty(localName = "index")
    public Integer getIndex() {
        return index;
    }

    public ChatMessage setIndex(Integer index) {
        this.index = index;
        return this;
    }


    @JsonProperty("sender")
    @JacksonXmlProperty(localName = "sender")
    public User getSender() {
        return sender;
    }

    public ChatMessage setSender(User sender) {
        this.sender = sender;
        return this;
    }


    @JsonProperty("content")
    @JacksonXmlProperty(localName = "content")
    public String getContent() {
        return content;
    }

    public ChatMessage setContent(String content) {
        this.content = content;
        return this;
    }


    @JsonProperty("timestamp")
    @JacksonXmlProperty(localName = "timestamp")
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public ChatMessage setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ChatMessage setTimestampToNow() {
        return setTimestamp(OffsetDateTime.now());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChatMessage chatMessage = (ChatMessage) o;
        return Objects.equals(chatRoom, chatMessage.chatRoom) &&
                Objects.equals(index, chatMessage.index) &&
                Objects.equals(sender, chatMessage.sender) &&
                Objects.equals(content, chatMessage.content) &&
                Objects.equals(timestamp, chatMessage.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoom, index, sender, content, timestamp);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "chatRoom='" + chatRoom + '\'' +
                ", index=" + index +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public static ChatMessage fromJSON(String representation) throws IOException {
        return AbstractRepresentation.fromJSON(representation, ChatMessage.class);
    }

    public static ChatMessage fromYAML(String representation) throws IOException {
        return AbstractRepresentation.fromYAML(representation, ChatMessage.class);
    }

    public static ChatMessage fromXML(String representation) throws IOException {
        return AbstractRepresentation.fromXML(representation, ChatMessage.class);
    }

    public static ChatMessage parse(String mimeType, String payload) throws IOException {
        return parse(mimeType, payload, ChatMessage.class);
    }

    @Override
    public int compareTo(ChatMessage o) {
        return o.index - index;
    }
}
