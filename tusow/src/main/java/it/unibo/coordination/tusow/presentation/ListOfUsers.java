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

@JacksonXmlRootElement(localName = "listOfUsers")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListOfUsers extends ListRepresentation<User> {

    public ListOfUsers() {
    }

    public ListOfUsers(Collection<? extends User> collection) {
        super(collection);
    }

    public ListOfUsers(Stream<? extends User> stream) {
        super(stream);
    }

    public ListOfUsers(User element1, User... elements) {
        super(element1, elements);
    }

    @JsonProperty("users")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "user")
    public List<User> getUsers() {
        return getItems();
    }

    public ListOfUsers setUsers(List<User> users) {
        setItems(users);
        return this;
    }

    public static ListOfUsers fromJSON(String representation) throws IOException {
        return AbstractRepresentation.fromJSON(representation, ListOfUsers.class);
    }

    public static ListOfUsers fromYAML(String representation) throws IOException {
        return AbstractRepresentation.fromYAML(representation, ListOfUsers.class);
    }

    public static ListOfUsers fromXML(String representation) throws IOException {
        return AbstractRepresentation.fromXML(representation, ListOfUsers.class);
    }

    public static ListOfUsers parse(String mimeType, String payload) throws IOException {
        return parse(mimeType, payload, ListOfUsers.class);
    }
}
