package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@JacksonXmlRootElement(localName = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends Representation {

    private UUID id = null;
    private String username = null;
    private String fullName = null;
    private String email = null;
    private String password = null;
    private Link link = null;
    private Role role = null;

    public enum Role implements Comparable<Role> {
        USER("user"),
        ADMIN("admin");

        private String value;


        Role(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        @JacksonXmlText
        public String toString() {
            return value;
        }

    }

    public User() {

    }

    public User(User clone) {
        this.id = clone.id;
        this.username = clone.username;
        this.fullName = clone.fullName;
        this.email = clone.email;
        this.link = ifNonNull(clone.link, Link::new);
        this.role = clone.role;
    }

    public User(UUID id, String username, String fullName, String email, Link link, Role role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.link = link;
        this.role = role;
    }


    @JsonProperty("id")
    @JacksonXmlProperty(localName = "id")
    public UUID getId() {
        return id;
    }

    public User setId(UUID id) {
        this.id = id;
        return this;
    }


    @JsonProperty("username")
    @JacksonXmlProperty(localName = "username")
    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }


    @JsonProperty("fullName")
    @JacksonXmlProperty(localName = "fullName")
    public String getFullName() {
        return fullName;
    }

    public User setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }


    @JsonProperty("email")
    @JacksonXmlProperty(localName = "email")
    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    @JsonProperty("password")
    @JacksonXmlProperty(localName = "password")
    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }


    @JsonProperty("link")
    @JacksonXmlProperty(localName = "link")
    public Link getLink() {
        return link;
    }

    public User setLink(Link link) {
        this.link = link;
        return this;
    }

    public User setLinkUrl(String link) {
        this.link = new Link(link);
        return this;
    }


    @JsonProperty("role")
    @JacksonXmlProperty(localName = "role")
    public Role getRole() {
        return role;
    }

    public User setRole(Role role) {
        this.role = role;
        return this;
    }

    public User setPropertiesToNonNullsOf(User other) {
        assignIfNonNull(other::getId, this::setId);
        assignIfNonNull(other::getEmail, this::setEmail);
        assignIfNonNull(other::getFullName, this::setFullName);
        assignIfNonNull(other::getLink, this::setLink);
        assignIfNonNull(other::getPassword, this::setPassword);
        assignIfNonNull(other::getRole, this::setRole);
        assignIfNonNull(other::getUsername, this::setUsername);
        return this;
    }

    public boolean sameUserOf(User user) {
        return user != null && (
                Objects.equals(id, user.id)
                        || Objects.equals(username, user.username)
                        || Objects.equals(email, user.email)
                        || Objects.equals(link, user.link)
        );
    }

    public boolean isIdentifiedBy(String identifier) {
        return identifier != null && (
                (id != null && Objects.equals(id.toString(), identifier))
                        || Objects.equals(username, identifier)
                        || Objects.equals(email, identifier)
                        || (link != null && Objects.equals(link.getUrl(), identifier))
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(fullName, user.fullName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(link, user.link) &&
                Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, fullName, email, link, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", link='" + link + '\'' +
                ", role=" + role +
                '}';
    }

    public static User fromJSON(String representation) throws IOException {
        return Representation.fromJSON(representation, User.class);
    }

    public static User fromYAML(String representation) throws IOException {
        return Representation.fromYAML(representation, User.class);
    }

    public static User fromXML(String representation) throws IOException {
        return Representation.fromXML(representation, User.class);
    }

    public static User parse(String mimeType, String payload) throws IOException {
        return parse(mimeType, payload, User.class);
    }

}
