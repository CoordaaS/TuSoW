package it.unibo.coordination.tusow.presentation;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import it.unibo.coordination.linda.logic.LogicTuple;
import it.unibo.coordination.prologx.PrologUtils;

import java.util.*;

public class LogicTupleAdapter extends Representation implements LogicTuple {

    private final LogicTuple adapted;

    public LogicTupleAdapter(LogicTuple adapted) {
        this.adapted = Objects.requireNonNull(adapted);
    }

    @Override
    public Struct asTerm() {
        return adapted.asTerm();
    }

    @Override
    public Term getTuple() {
        return adapted.getTuple();
    }

    @Override
    public String toXMLString() {
        return asTerm().toJSON();
    }

    @Override
    public String toJSONString() {
        return writeAsJSON(PrologUtils.termToObject(asTerm()));
    }

    @Override
    public String toYAMLString() {
        return writeAsYAML(PrologUtils.termToObject(asTerm()));
    }

    public static void main(String[] args) {
        var tuple = new LogicTupleAdapter(LogicTuple.of("[a, b, c]"));

        System.out.println(tuple.toJSONString());
        System.out.println();
        System.out.println(tuple.toYAMLString());
        System.out.println();
        System.out.println(tuple.toXMLString());
    }
}
