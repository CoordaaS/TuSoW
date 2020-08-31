package it.unibo.cooordination.respect.core

import java.net.URI

sealed class TransducerID : EntityID {

    abstract val name: String

    data class Local(override val name: String) : TransducerID()

    data class Remote(
            override val name: String,
            val uri: URI
    ) : TransducerID()

}