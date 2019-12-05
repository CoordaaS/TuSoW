package it.unibo.coordination.prologx

import alice.tuprolog.Term

interface PrologSerializable {
    fun toTerm(): Term
}