package it.unibo.coordination.tusow.presentation;

import it.unibo.coordination.linda.core.Match;
import it.unibo.coordination.linda.core.Template;
import it.unibo.coordination.linda.core.Tuple;

public interface MatchRepresentation<T extends Tuple, TT extends Template, K, V> extends Match<T, TT, K, V>, Representation {

}
