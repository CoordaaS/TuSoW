package it.unibo.coordination.tusow.presentation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.IOException;
import java.util.Objects;

@JacksonXmlRootElement(localName = "link")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Link extends Representation {

    private String url = null;

    public Link() {

    }

    public Link(String url) {
        this.url = url;
    }

    public Link(Link clone) {
        this(clone.url);
    }

    @JsonProperty("url")
    @JacksonXmlProperty(localName = "url")
    public String getUrl() {
        return url;
    }

    public Link setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "Link{" +
                "url='" + url + '\'' +
                '}';
    }

    public static Link fromJSON(String representation) throws IOException {
        return Representation.fromJSON(representation, Link.class);
    }

    public static Link fromYAML(String representation) throws IOException {
        return Representation.fromYAML(representation, Link.class);
    }

    public static Link fromXML(String representation) throws IOException {
        return Representation.fromXML(representation, Link.class);
    }

    public static Link parse(String mimeType, String payload) throws IOException {
        return parse(mimeType, payload, Link.class);
    }

}
