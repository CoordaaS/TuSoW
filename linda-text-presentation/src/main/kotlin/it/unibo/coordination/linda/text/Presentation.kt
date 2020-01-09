package it.unibo.coordination.linda.text

import it.unibo.presentation.Presentation as Prototype

object Presentation : Prototype by Prototype.default {
    init {
        registerDynamicSerializers(RegexTemplate::class.java, ::RegexTemplateSerializer)
        registerDynamicSerializers(StringTuple::class.java, ::StringTupleSerializer)
        registerDynamicSerializers(RegularMatch::class.java, ::RegularMatchSerializer)
        registerDynamicDeserializers(RegexTemplate::class.java, ::RegexTemplateDeserializer)
        registerDynamicDeserializers(StringTuple::class.java, ::StringTupleDeserializer)
        registerDynamicDeserializers(RegularMatch::class.java, ::RegularMatchDeserializer)
    }
}