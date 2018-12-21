package it.unibo.coordination.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

class TupleImpl implements Tuple {
    public final Object[] items;

    protected TupleImpl(Object... items) {
        this.items = Objects.requireNonNull(items);
    }

    @Override
    public <X> X get(int index) {
        return (X) items[index];
    }

    public int getSize() {
        return items.length;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(items, items.length);
    }
    

    @Override
    public String toString() {
        return Arrays.stream(items)
                .map(Objects::toString)
                .collect(Collectors.joining(", ", "(", ")"));
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Object obj : items) {
			result = prime * result + obj.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {	
		return obj instanceof TupleImpl
                && Arrays.equals(items, ((TupleImpl) obj).items);
	}
    
    
}
