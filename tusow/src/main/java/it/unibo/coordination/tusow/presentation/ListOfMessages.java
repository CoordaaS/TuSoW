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

@JacksonXmlRootElement(localName = "listOfMessages")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListOfMessages extends ListRepresentation<ChatMessage> {

    public ListOfMessages() {
    }

    public ListOfMessages(Collection<? extends ChatMessage> collection) {
        super(collection);
    }

    public ListOfMessages(Stream<? extends ChatMessage> stream) {
        super(stream);
    }

    public ListOfMessages(ChatMessage element1, ChatMessage... elements) {
        super(element1, elements);
    }


    @JsonProperty("messages")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "message")
    public List<ChatMessage> getMessages() {
        return getItems();
    }

    public ListOfMessages setMessages(List<ChatMessage> messages) {
        setItems(messages);
        return this;
    }

    public static ListOfMessages fromJSON(String representation) throws IOException {
        return Representation.fromJSON(representation, ListOfMessages.class);
    }

    public static ListOfMessages fromYAML(String representation) throws IOException {
        return Representation.fromYAML(representation, ListOfMessages.class);
    }

    public static ListOfMessages fromXML(String representation) throws IOException {
        return Representation.fromXML(representation, ListOfMessages.class);
    }

    public static ListOfMessages parse(String mimeType, String payload) throws IOException {
        return parse(mimeType, payload, ListOfMessages.class);
    }
}
