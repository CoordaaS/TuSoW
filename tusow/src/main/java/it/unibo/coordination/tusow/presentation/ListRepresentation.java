package it.unibo.coordination.tusow.presentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ListRepresentation<X extends Representation> extends Representation {

    private List<X> list;

    public Stream<X> stream() {
        return list.stream();
    }

    public ListRepresentation() {
    }

    public ListRepresentation(Collection<? extends X> collection) {
        list = new ArrayList<>(collection);
    }

    public ListRepresentation(Stream<? extends X> stream) {
        list = stream.collect(Collectors.toList());
    }

    public ListRepresentation(X element1, X... elements) {
        this(Stream.concat(Stream.of(element1), Stream.of(elements)));
    }

    protected List<X> getItems() {
        return list;
    }

    protected void setItems(List<X> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListRepresentation<?> that = (ListRepresentation<?>) o;
        return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    @Override
    public String toString() {
        return "ListRepresentation{" +
                "list=" + list +
                '}';
    }
}
