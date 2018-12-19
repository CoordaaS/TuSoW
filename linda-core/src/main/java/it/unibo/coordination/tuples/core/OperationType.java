package it.unibo.coordination.tuples.core;

import java.util.EnumSet;

public enum OperationType {
    GET("get"),

    WRITE("out"),
    READ("rd"),
    TAKE("in"),
    ABSENT("no"),

    WRITE_ALL("out_all"),
    READ_ALL("rd_all"),
    TAKE_ALL("take_all"),

    TRY_READ("rdp"),
    TRY_TAKE("inp"),
    TRY_ABSENT("nop");

    private static final EnumSet<OperationType> NOTHING_ACCEPTING = EnumSet.of(GET);
    private static final EnumSet<OperationType> TEMPLATE_ACCEPTING = EnumSet.of(READ, TAKE, TRY_TAKE, TRY_READ, ABSENT, TRY_ABSENT, READ_ALL, TAKE_ALL);
    private static final EnumSet<OperationType> TEMPLATES_ACCEPTING = EnumSet.noneOf(OperationType.class);
    private static final EnumSet<OperationType> TUPLE_ACCEPTING = EnumSet.of(WRITE);
    private static final EnumSet<OperationType> TUPLES_ACCEPTING = EnumSet.of(WRITE_ALL);

    private static final EnumSet<OperationType> TUPLE_RETURNING = EnumSet.of(WRITE, READ, TAKE, TRY_READ, TRY_TAKE, TRY_ABSENT);
    private static final EnumSet<OperationType> TUPLES_RETURNING = EnumSet.of(GET, WRITE_ALL, READ_ALL, TAKE_ALL, TRY_READ, TRY_TAKE, TRY_ABSENT);
    private static final EnumSet<OperationType> TEMPLATE_RETURNING = EnumSet.of(ABSENT, TRY_ABSENT);
    private static final EnumSet<OperationType> TEMPLATES_RETURNING = EnumSet.of(TRY_ABSENT);

    private final String classicName;

    OperationType(String classicName) {
        this.classicName = classicName;
    }

    public static EnumSet<OperationType> nothingAcceptingSet() {
        return NOTHING_ACCEPTING;
    }

    public static boolean isNothingAccepting(OperationType type) {
        return nothingAcceptingSet().contains(type);
    }

    public static EnumSet<OperationType> templateAcceptingSet() {
        return TEMPLATE_ACCEPTING;
    }

    public static boolean isTemplateAccepting(OperationType type) {
        return templateAcceptingSet().contains(type);
    }

    public static EnumSet<OperationType> templatesAcceptingSet() {
        return TEMPLATES_ACCEPTING;
    }

    public static boolean isTemplatesAccepting(OperationType type) {
        return templatesAcceptingSet().contains(type);
    }

    public static EnumSet<OperationType> tupleAcceptingSet() {
        return TUPLE_ACCEPTING;
    }

    public static boolean isTupleAcceptingSet(OperationType type) {
        return tupleAcceptingSet().contains(type);
    }

    public static EnumSet<OperationType> tuplesAcceptingSet() {
        return TUPLES_ACCEPTING;
    }

    public static boolean isTuplesAcceptingSet(OperationType type) {
        return tuplesAcceptingSet().contains(type);
    }

    public static EnumSet<OperationType> tupleReturningSet() {
        return TUPLE_RETURNING;
    }

    public static boolean isTupleReturningSet(OperationType type) {
        return tupleReturningSet().contains(type);
    }

    public static EnumSet<OperationType> tuplesReturningSet() {
        return TUPLES_RETURNING;
    }

    public static boolean isTuplesReturningSet(OperationType type) {
        return tuplesReturningSet().contains(type);
    }

    public static EnumSet<OperationType> templateReturningSet() {
        return TEMPLATE_RETURNING;
    }

    public static boolean isTemplateReturning(OperationType type) {
        return templateReturningSet().contains(type);
    }

    public static EnumSet<OperationType> templatesReturningSet() {
        return TEMPLATES_RETURNING;
    }

    public static boolean isTemplatesReturning(OperationType type) {
        return templatesReturningSet().contains(type);
    }

    public String getClassicName() {
        return classicName;
    }
}
