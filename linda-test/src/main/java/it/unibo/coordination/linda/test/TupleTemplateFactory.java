package it.unibo.coordination.linda.test;

import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;
import org.apache.commons.collections4.MultiSet;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

public interface TupleTemplateFactory<T extends Tuple<T>, TT extends Template<T>, K, V, M extends Match<T, TT, K, V>> {
    TT getATemplate();
    T getATuple();

    T getMessageTuple(String recipient, String payload);
    TT getMessageTemplate(String recipient);
    TT getGeneralMessageTemplate();

    MultiSet<T> getSomeTuples();

    Quartet<MultiSet<T>, TT, MultiSet<T>, TT> getSomeTuplesOfTwoSorts();

    Pair<MultiSet<T>, TT> getSomeTuplesOfOneSort();

    Pair<T, TT> getATupleAndATemplateMatchingIt();

    Triplet<T, TT, M> getSuccessfulMatch();
    Triplet<T, TT, M> getFailedMatch();

    Pair<TT, M> getEmptyMatch();

}
