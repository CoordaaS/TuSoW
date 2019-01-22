package it.unibo.coordination.linda.test;

import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import org.apache.commons.collections4.MultiSet;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.Objects;

public class TestBaseLinda<T extends Tuple, TT extends Template> implements TupleTemplateFactory<T, TT> {

    private final TupleTemplateFactory<T, TT> tupleTemplateFactory;

    public TestBaseLinda(TupleTemplateFactory<T, TT> tupleTemplateFactory) {
        this.tupleTemplateFactory = Objects.requireNonNull(tupleTemplateFactory);
    }

    @Override
    public TT getATemplate() {
        return tupleTemplateFactory.getATemplate();
    }

    @Override
    public T getATuple() {
        return tupleTemplateFactory.getATuple();
    }

    @Override
    public T getMessageTuple(String recipient, String payload) {
        return tupleTemplateFactory.getMessageTuple(recipient, payload);
    }

    @Override
    public TT getMessageTemplate(String recipient) {
        return tupleTemplateFactory.getMessageTemplate(recipient);
    }

    @Override
    public TT getGeneralMessageTemplate() {
        return tupleTemplateFactory.getGeneralMessageTemplate();
    }

    @Override
    public MultiSet<T> getSomeTuples() {
        return tupleTemplateFactory.getSomeTuples();
    }

    @Override
    public Quartet<MultiSet<T>, TT, MultiSet<T>, TT> getSomeTuplesOfTwoSorts() {
        return tupleTemplateFactory.getSomeTuplesOfTwoSorts();
    }

    @Override
    public Pair<MultiSet<T>, TT> getSomeTuplesOfOneSort() {
        return tupleTemplateFactory.getSomeTuplesOfOneSort();
    }

    @Override
    public Pair<T, TT> getATupleAndATemplateMatchingIt() {
        return tupleTemplateFactory.getATupleAndATemplateMatchingIt();
    }
}
