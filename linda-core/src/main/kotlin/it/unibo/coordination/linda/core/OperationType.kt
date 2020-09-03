package it.unibo.coordination.linda.core

import java.util.*

enum class OperationType {
    GET,
    SET,

    WRITE,
    READ,
    TAKE,
    ABSENT,

    WRITE_ALL,
    READ_ALL,
    TAKE_ALL,

    TRY_READ,
    TRY_TAKE,
    TRY_ABSENT,

    GET_SIZE,
    GET_PENDING_REQUESTS;


    companion object {

        @JvmStatic
        val NOTHING_ACCEPTING: EnumSet<OperationType> = EnumSet.of(GET, GET_SIZE, GET_PENDING_REQUESTS)

        @JvmStatic
        private val TEMPLATE_ACCEPTING = EnumSet.of(READ, TAKE, TRY_TAKE, TRY_READ, ABSENT, TRY_ABSENT, READ_ALL, TAKE_ALL)

        @JvmStatic
        private val TEMPLATES_ACCEPTING = EnumSet.noneOf(OperationType::class.java)

        @JvmStatic
        private val TUPLE_ACCEPTING = EnumSet.of(WRITE)

        @JvmStatic
        private val TUPLES_ACCEPTING = EnumSet.of(WRITE_ALL)

        @JvmStatic
        private val TUPLE_RETURNING = EnumSet.of(WRITE, READ, TAKE, TRY_READ, TRY_TAKE, TRY_ABSENT)

        @JvmStatic
        private val TUPLES_RETURNING = EnumSet.of(GET, WRITE_ALL, READ_ALL, TAKE_ALL, TRY_READ, TRY_TAKE, TRY_ABSENT)

        @JvmStatic
        private val TEMPLATE_RETURNING = EnumSet.of(ABSENT, TRY_ABSENT)

        @JvmStatic
        private val TEMPLATES_RETURNING = EnumSet.of(TRY_ABSENT)

        @JvmStatic
        private val ANY_RETURNING = EnumSet.of(GET_SIZE, GET_PENDING_REQUESTS)

        @JvmStatic
        fun anyReturning(): EnumSet<OperationType> {
            return ANY_RETURNING
        }

        @JvmStatic
        fun nothingAcceptingSet(): EnumSet<OperationType> {
            return NOTHING_ACCEPTING
        }

        @JvmStatic
        fun isNothingAccepting(type: OperationType): Boolean {
            return nothingAcceptingSet().contains(type)
        }

        @JvmStatic
        fun templateAcceptingSet(): EnumSet<OperationType> {
            return TEMPLATE_ACCEPTING
        }

        @JvmStatic
        fun isTemplateAccepting(type: OperationType): Boolean {
            return templateAcceptingSet().contains(type)
        }

        @JvmStatic
        fun templatesAcceptingSet(): EnumSet<OperationType> {
            return TEMPLATES_ACCEPTING
        }

        @JvmStatic
        fun isTemplatesAccepting(type: OperationType): Boolean {
            return templatesAcceptingSet().contains(type)
        }

        @JvmStatic
        fun tupleAcceptingSet(): EnumSet<OperationType> {
            return TUPLE_ACCEPTING
        }

        @JvmStatic
        fun isTupleAcceptingSet(type: OperationType): Boolean {
            return tupleAcceptingSet().contains(type)
        }

        @JvmStatic
        fun tuplesAcceptingSet(): EnumSet<OperationType> {
            return TUPLES_ACCEPTING
        }

        @JvmStatic
        fun isTuplesAcceptingSet(type: OperationType): Boolean {
            return tuplesAcceptingSet().contains(type)
        }

        @JvmStatic
        fun tupleReturningSet(): EnumSet<OperationType> {
            return TUPLE_RETURNING
        }

        @JvmStatic
        fun isTupleReturningSet(type: OperationType): Boolean {
            return tupleReturningSet().contains(type)
        }

        @JvmStatic
        fun tuplesReturningSet(): EnumSet<OperationType> {
            return TUPLES_RETURNING
        }

        @JvmStatic
        fun isTuplesReturningSet(type: OperationType): Boolean {
            return tuplesReturningSet().contains(type)
        }

        @JvmStatic
        fun templateReturningSet(): EnumSet<OperationType> {
            return TEMPLATE_RETURNING
        }

        @JvmStatic
        fun isTemplateReturning(type: OperationType): Boolean {
            return templateReturningSet().contains(type)
        }

        @JvmStatic
        fun templatesReturningSet(): EnumSet<OperationType> {
            return TEMPLATES_RETURNING
        }

        @JvmStatic
        fun isTemplatesReturning(type: OperationType): Boolean {
            return templatesReturningSet().contains(type)
        }

        @JvmStatic
        fun isAnyReturning(type: OperationType): Boolean {
            return anyReturning().contains(type)
        }
    }
}