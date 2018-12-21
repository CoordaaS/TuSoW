package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "chatRoom")
public class ChatRoom extends AbstractRepresentation {

    private String name = null;
    private User owner = null;
    private List<User> members;
    private Integer membersCount = null;
    private List<ChatMessage> messages;
    private Integer messagesCount = null;


    public enum AccessLevel {
        PUBLIC("public"),
        OPEN("open"),
        CLOSED("closed");

        private String value;

        AccessLevel(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        @JacksonXmlText
        public String toString() {
            return value;
        }
    }

    private AccessLevel accessLevel = null;
    private Link link = null;

    public ChatRoom() {

    }

    public ChatRoom(ChatRoom clone) {
            this.name = clone.name;
            this.owner = ifNonNull(clone.owner, User::new);
            this.members = ifNonNull(clone.members, ms -> ms.stream().map(User::new).collect(Collectors.toList()));
            this.membersCount = clone.membersCount;
            this.messages = ifNonNull(clone.messages, ms -> ms.stream().map(ChatMessage::new).collect(Collectors.toList()));
            this.messagesCount = clone.messagesCount;
            this.accessLevel = clone.accessLevel;
            this.link = ifNonNull(clone.link, Link::new);
    }

    public ChatRoom(String name, User owner, List<User> members, Integer membersCount, List<ChatMessage> messages, Integer messagesCount, AccessLevel accessLevel, Link link) {
        this.name = name;
        this.owner = owner;
        this.members = members;
        this.membersCount = membersCount;
        this.messages = messages;
        this.messagesCount = messagesCount;
        this.accessLevel = accessLevel;
        this.link = link;
    }


    @JsonProperty("name")
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    public ChatRoom setName(String name) {
        this.name = name;
        return this;
    }


    @JsonProperty("owner")
    @JacksonXmlProperty(localName = "owner")
    public User getOwner() {
        return owner;
    }

    public ChatRoom setOwner(User owner) {
        this.owner = owner;
        return this;
    }


    @JsonProperty("members")
    @JacksonXmlElementWrapper(localName = "members")
    @JacksonXmlProperty(localName = "member")
    public List<User> getMembers() {
        return members;
    }

    public ChatRoom setMembers(Collection<User> members) {
        this.members = new ArrayList<>(members);
        return this;
    }

    public ChatRoom setMembersFromStream(Stream<User> memebers) {
        this.members = memebers.collect(Collectors.toList());
        return this;
    }


    @JsonProperty("membersCount")
    @JacksonXmlProperty(localName = "membersCount")
    public Integer getMembersCount() {
        return membersCount;
    }

    public ChatRoom setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
        return this;
    }


    @JsonProperty("messages")
    @JacksonXmlElementWrapper(localName = "messages")
    @JacksonXmlProperty(localName = "message")
    public List<ChatMessage> getMessages() {
        return messages;
    }

    public ChatRoom setMessages(Collection<ChatMessage> messages) {
        this.messages = new ArrayList<>(messages);
        return this;
    }

    public ChatRoom setMessagesFromStream(Stream<ChatMessage> messages) {
        this.messages = messages.collect(Collectors.toList());
        return this;
    }

    @JsonProperty("messagesCount")
    @JacksonXmlProperty(localName = "messagesCount")
    public Integer getMessagesCount() {
        return messagesCount;
    }

    public ChatRoom setMessagesCount(Integer messagesCount) {
        this.messagesCount = messagesCount;
        return this;
    }


    @JsonProperty("accessLevel")
    @JacksonXmlProperty(localName = "accessLevel")
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public ChatRoom setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
        return this;
    }


    @JsonProperty("link")
    @JacksonXmlProperty(localName = "link")
    public Link getLink() {
        return link;
    }

    public ChatRoom setLink(Link link) {
        this.link = link;
        return this;
    }

    public ChatRoom setLinkUrl(String link) {
        return setLink(new Link(link));
    }

    public boolean sameChatRoomOf(ChatRoom other) {
        return other != null && Objects.equals(name, other.name);
    }

    public boolean isIdentifiedBy(String identifier) {
        return identifier != null && Objects.equals(name, identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(name, chatRoom.name) &&
                Objects.equals(owner, chatRoom.owner) &&
                Objects.equals(members, chatRoom.members) &&
                Objects.equals(membersCount, chatRoom.membersCount) &&
                Objects.equals(messages, chatRoom.messages) &&
                Objects.equals(messagesCount, chatRoom.messagesCount) &&
                Objects.equals(accessLevel, chatRoom.accessLevel) &&
                Objects.equals(link, chatRoom.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, owner, members, membersCount, messages, messagesCount, accessLevel, link);
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", members=" + members +
                ", membersCount=" + membersCount +
                ", messages=" + messages +
                ", messagesCount=" + messagesCount +
                ", accessLevel=" + accessLevel +
                ", link='" + link + '\'' +
                '}';
    }

    public static ChatRoom fromJSON(String representation) throws IOException {
        return AbstractRepresentation.fromJSON(representation, ChatRoom.class);
    }

    public static ChatRoom fromYAML(String representation) throws IOException {
        return AbstractRepresentation.fromYAML(representation, ChatRoom.class);
    }

    public static ChatRoom fromXML(String representation) throws IOException {
        return AbstractRepresentation.fromXML(representation, ChatRoom.class);
    }

    public static ChatRoom parse(String mimeType, String payload) throws IOException {
        return parse(mimeType, payload, ChatRoom.class);
    }

}
