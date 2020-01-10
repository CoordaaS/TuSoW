package it.unibo.coordination.linda.text

import it.unibo.coordination.linda.core.Presentation as Prototype

object Presentation : it.unibo.presentation.Presentation by Prototype {
    init {
        registerDynamicSerializers(RegexTemplate::class.java, ::RegexTemplateSerializer)
        registerDynamicSerializers(StringTuple::class.java, ::StringTupleSerializer)
        registerDynamicSerializers(RegularMatch::class.java, ::RegularMatchSerializer)
        registerDynamicDeserializers(RegexTemplate::class.java, ::RegexTemplateDeserializer)
        registerDynamicDeserializers(StringTuple::class.java, ::StringTupleDeserializer)
        registerDynamicDeserializers(RegularMatch::class.java, ::RegularMatchDeserializer)
        Prototype.initialize<StringTuple, RegexTemplate>()
    }
}