package it.unibo.coordination.linda.logic

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.serialize.TermDeobjectifier
import it.unibo.tuprolog.serialize.TermObjectifier
import it.unibo.coordination.linda.core.Presentation as Prototype

object Presentation : it.unibo.presentation.Presentation by Prototype {
    init {
        registerDynamicSerializers(LogicTemplate::class.java) { _, _, template ->
            TermObjectifier.default.objectify(template.template)
        }

        registerDynamicSerializers(LogicTuple::class.java) { _, _, tuple ->
            TermObjectifier.default.objectify(tuple.value)
        }

        registerDynamicSerializers(Term::class.java) { _, _, term ->
            TermObjectifier.default.objectify(term)
        }

        registerDynamicSerializers(LogicMatch::class.java, ::LogicMatchSerializer)

        registerDynamicDeserializers(LogicTuple::class.java) { _, _, tuple ->
            LogicTuple.of(TermDeobjectifier.default.deobjectify(tuple))
        }

        registerDynamicDeserializers(LogicTemplate::class.java) { _, _, template ->
            LogicTemplate.of(TermDeobjectifier.default.deobjectify(template))
        }

        registerDynamicDeserializers(Term::class.java) { _, _, term ->
            TermDeobjectifier.default.deobjectify(term)
        }

        registerDynamicDeserializers(LogicMatch::class.java, ::LogicMatchDeserializer)

        Prototype.initialize<LogicTuple, LogicTemplate>()
    }
}