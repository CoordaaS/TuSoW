package it.unibo.cooordination.respect.core

import java.net.URI

sealed class TupleCentreID : EntityID {

    abstract val name: String

    data class Local(override val name: String = "default") : TupleCentreID()

    data class Remote(
            override val name: String = "default",
            val uri: URI
    ) : TupleCentreID()

}