package it.unibo.coordination.prologx;

import alice.tuprolog.*;
import alice.tuprolog.exceptions.InvalidTermException;
import alice.tuprolog.exceptions.NoMoreSolutionException;
import alice.tuprolog.exceptions.NoSolutionException;
import alice.tuprolog.presentation.TermUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PrologUtils {

    public static boolean assertOn(Prolog engine, Term term) {
        try {
            final var si = engine.solve(assertTerm(term));
            return si.isSuccess();
        } finally {
            engine.solveEnd();
        }
    }

    public static Optional<Term> retractFrom(Prolog engine, Term term) {
        try {
            final var si = engine.solve(retractTerm(term));
            if (si.isSuccess()) {
                final var retraction = (Struct) si.getSolution();
                return Optional.of(retraction.getArg(0));
            }
            return Optional.empty();
        } catch (NoSolutionException e) {
            return Optional.empty();
        } finally {
            engine.solveEnd();
        }
    }

    public static Stream<SolveInfo> solveStream(Prolog engine, Term goal) {
        return Stream.generate(new Supplier<SolveInfo>() {

            private boolean first = true;

            private SolveInfo last = null;

            @Override
            public SolveInfo get() {
                if (first) {
                    last = engine.solve(goal);
                    if (!last.isSuccess()) {
                        engine.solveEnd();
                    }
                    first = false;
                } else {
                    try {
                        if (last.isSuccess() && last.hasOpenAlternatives()) {
                            last = engine.solveNext();
                        } else {
                            last = null;
                            engine.solveEnd();
                        }
                    } catch (NoMoreSolutionException e) {
                        throw new IllegalStateException(e);
                    }
                }
                return last;
            }
        }).takeWhile(solveInfo -> solveInfo != null && solveInfo.isSuccess());
    }

    public static Stream<Term> solutionsStream(Prolog engine, Term goal) {
        return solveStream(engine, goal).map(si -> {
            try {
                return si.getSolution();
            } catch (NoSolutionException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    @Deprecated
    public static Stream<Term> listToStream(Term term) {
        if (term.isList()) {
            return listToStream((Struct) term);
        } else {
            throw new IllegalArgumentException("Not a list: " + term);
        }
    }

    @Deprecated
    public static Stream<Term> listToStream(Struct list) {
        final Iterator<? extends Term> i = list.listIterator();
        final Stream.Builder<Term> sb = Stream.builder();

        while (i.hasNext()) {
            sb.accept(i.next());
        }

        return sb.build();
    }

    public static Term streamToConjunction(Stream<? extends Term> terms) {
        final List<Term> termList = terms.collect(Collectors.toList());
        final int size = termList.size();

        if (termList.isEmpty()) {
            return Struct.truth(true);
        } else if (size == 1) {
            return termList.get(0);
        }

        return Struct.tuple(terms);
    }

    public static Stream<Term> conjunctionToStream(Term term) {
        Struct struct;
        if (term instanceof Struct && (struct = (Struct) term).getName().equals(",") && struct.getArity() == 2) {
            return Stream.concat(
                Stream.of(struct.getArg(0)),
                Stream.of(struct.getArg(1)).flatMap(PrologUtils::conjunctionToStream)
            );
        } else {
            return Stream.of(term);
        }
    }

    public static Struct unificationTerm(String var, Term term) {
        return unificationTerm(Var.of(var), term);
    }

    public static Struct unificationTerm(Term term1, Term term2) {
        return Struct.of("=", term1, term2);
    }

    public static Struct assertTerm(Term term) {
        return Struct.of("assert", term);
    }

    public static Struct retractTerm(Term term) {
        return Struct.of("retract", term);
    }

    public static Term anyToTerm(Object payload) {
        if (payload == null) {
            return Var.anonymous();
        } else if (payload instanceof Term) {
            return (Term) payload;
        } else if (payload instanceof PrologSerializable) {
            return ((PrologSerializable) payload).toTerm();
        } else {
            try {
                return Term.createTerm(payload.toString());
            } catch (InvalidTermException e) {
                return Struct.atom(payload.toString());
            }
        }
    }

    private static final Prolog HELPER = new Prolog();

    public static Term dynamicObjectToTerm(Object object) {
        return TermUtils.dynamicObjectToTerm(object);
    }

    public static Object termToDynamicObject(Term term) {
        return TermUtils.termToDynamicObject(term);
    }

    public static Stream<Term> argumentsStream(Struct struct) {
        return IntStream.range(0, struct.getArity()).mapToObj(struct::getArg);
    }
}
