package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@JacksonXmlRootElement(localName = "listOfRooms")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListOfChatRooms extends ListRepresentation<ChatRoom> {

    public ListOfChatRooms() {
    }

    public ListOfChatRooms(Collection<? extends ChatRoom> collection) {
        super(collection);
    }

    public ListOfChatRooms(Stream<? extends ChatRoom> stream) {
        super(stream);
    }

    public ListOfChatRooms(ChatRoom element1, ChatRoom... elements) {
        super(element1, elements);
    }

    @JsonProperty("rooms")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "room")
    public List<ChatRoom> getRooms() {
        return getItems();
    }

    public ListOfChatRooms setRooms(List<ChatRoom> rooms) {
        setItems(rooms);
        return this;
    }

    public static ListOfChatRooms fromJSON(String representation) throws IOException {
        return AbstractRepresentation.fromJSON(representation, ListOfChatRooms.class);
    }

    public static ListOfChatRooms fromYAML(String representation) throws IOException {
        return AbstractRepresentation.fromYAML(representation, ListOfChatRooms.class);
    }

    public static ListOfChatRooms fromXML(String representation) throws IOException {
        return AbstractRepresentation.fromXML(representation, ListOfChatRooms.class);
    }

    public static ListOfChatRooms parse(String mimeType, String payload) throws IOException {
        return parse(mimeType, payload, ListOfChatRooms.class);
    }
}
