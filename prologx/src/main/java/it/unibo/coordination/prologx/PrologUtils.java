package it.unibo.coordination.prologx;

import alice.tuprolog.Double;
import alice.tuprolog.Float;
import alice.tuprolog.Long;
import alice.tuprolog.*;
import alice.tuprolog.exceptions.NoMoreSolutionException;
import alice.tuprolog.exceptions.NoSolutionException;

import java.util.*;
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
        return unificationTerm(new Var(var), term);
    }

    public static Struct unificationTerm(Term term1, Term term2) {
        return new Struct("=", term1, term2);
    }

    public static Struct assertTerm(Term term) {
        return new Struct("assert", term);
    }

    public static Struct retractTerm(Term term) {
        return new Struct("retract", term);
    }

    public static Term anyToTerm(Object payload) {
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

    private static final Prolog HELPER = new Prolog();

    public static Term dynamicObjectToTerm(Object object) {
        if (object instanceof java.lang.Double) {
            return new Double((java.lang.Double) object);
        } else if (object instanceof java.lang.Integer) {
            return new Int((Integer) object);
        } else if (object instanceof java.lang.Float) {
            return new Float((java.lang.Float) object);
        } else if (object instanceof java.lang.Long) {
            return new Long((java.lang.Long) object);
        } else if (object instanceof String) {
            return new Struct(object.toString());
        } else if (object instanceof List) {
            var terms = ((List<?>) object).stream()
                    .map(PrologUtils::dynamicObjectToTerm)
                    .toArray(Term[]::new);
            return new Struct(terms);
        } else if (object instanceof Map) {
            final Map<String, Object> objectMap = (Map<String, Object>) object;
            if (objectMap.containsKey("var")) {
                final var variable = new Var(objectMap.get("var").toString());
                if (objectMap.get("val") != null) {
                    variable.unify(HELPER, dynamicObjectToTerm(objectMap.get("val")));
                }
                return variable;
            } else if (objectMap.containsKey("fun") && objectMap.containsKey("args")) {
                final Term[] arguments = ((List<?>) objectMap.get("args"))
                        .stream()
                        .peek(it -> {
                            if (it == null) throw new IllegalArgumentException();
                        }).map(PrologUtils::dynamicObjectToTerm)
                        .toArray(Term[]::new);

                return new Struct(objectMap.get("fun").toString(), arguments);
            }
        }
        throw new IllegalArgumentException();
    }

    public static Object termToDynamicObject(Term term) {
        if (term instanceof Double) {
            return ((Double) term).doubleValue();
        } else if (term instanceof Int) {
            return ((Int) term).intValue();
        } else if (term instanceof Float) {
            return ((Float) term).floatValue();
        } else if (term instanceof Long) {
            return ((Long) term).intValue();
        } else if (term instanceof Var && ((Var) term).isBound()) {
            return Map.of("var", ((Var) term).getOriginalName(), "val", termToDynamicObject(((Var) term).getLink()));
        } else if (term instanceof Var) {
            final Map<String, Object> varMap = new LinkedHashMap<>();
            varMap.put("var", ((Var) term).getOriginalName());
            varMap.put("val", null);
            return Collections.unmodifiableMap(varMap);
        } else if (term.isList()) {
            return listToStream(term)
                    .map(PrologUtils::termToDynamicObject)
                    .collect(Collectors.toList());
        } else if (term instanceof Struct) {
            final Struct struct = (Struct) term;
            if (struct.getArity() == 0) {
                return struct.getName();
            } else {
                return Map.of("fun", struct.getName(), "args", argumentsStream(struct).map(PrologUtils::termToDynamicObject).collect(Collectors.toList()));
            }
        }
        throw new IllegalStateException();
    }

    public static Stream<Term> argumentsStream(Struct struct) {
        return IntStream.range(0, struct.getArity()).mapToObj(struct::getArg);
    }
}
