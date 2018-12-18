package it.unibo.tuprolog.utils;

import alice.tuprolog.InvalidTermException;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrologUtils {

    public static Stream<Term> parseList(String string) {
        try {
            return listToStream(Term.createTerm(string));
        } catch (NullPointerException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    public static Stream<Term> listToStream(Term term) {
        if (term.isList()) {
            return listToStream((Struct) term);
        } else {
            throw new IllegalArgumentException("Not a list: " + term);
        }
    }

    public static Stream<Term> listToStream(Struct list) {
        final Iterator<? extends Term> i = list.listIterator();
        final Stream.Builder<Term> sb = Stream.builder();

        while (i.hasNext()) {
            sb.accept(i.next());
        }

        return sb.build();
    }

    public static Struct streamToList(Stream<? extends Term> terms) {
        final Term[] temp = terms.toArray(Term[]::new);
        return new Struct(temp);
    }

    public static Term streamToConjunction(Stream<? extends Term> terms) {
        final List<Term> termList = terms.collect(Collectors.toList());
        final int size = termList.size();

        if (termList.isEmpty()) {
            return Struct.TRUE;
        } else if (size == 1) {
            return termList.get(0);
        }

        Struct conjunction = new Struct(",", termList.get(size - 2), termList.get(size - 1));
        for (int i = size - 3; i >= 0; i--) {
            conjunction = new Struct(",", termList.get(i), conjunction);
        }

        return conjunction;
    }

    public static Struct unificationTerm(String var, Term term) {
        return new Struct("=", new Var(var), term);
    }

    public static Struct assertTerm(Term term) {
        return new Struct("assert", term);
    }

    public static Struct retractTerm(Term term) {
        return new Struct("retract", term);
    }

    public static Term objectToTerm(Object payload) {
        if (payload == null) {
            return new Var();
        } else if (payload instanceof Term) {
            return (Term) payload;
        } else if (payload instanceof PrologSerializable) {
            return ((PrologSerializable) payload).toTerm();
        } else {
            try {
                return Term.createTerm(payload.toString());
            } catch (InvalidTermException e) {
                return new Struct(payload.toString());
            }
        }
    }
}
