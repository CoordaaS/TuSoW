package it.unibo.coordination.linda.core

import it.unibo.presentation.Presentation.Prototype
import it.unibo.presentation.TypeToken
import it.unibo.presentation.toTypeToken

object Presentation : it.unibo.presentation.Presentation by Prototype {

    inline fun <reified T : Tuple<T>, reified TT : Template<T>> initialize() {
        val pendingRequestToken = PendingRequest::class.java.toTypeToken(T::class.java, TT::class.java) as TypeToken<PendingRequest<T, TT>>

        registerDynamicSerializers(pendingRequestToken) { mimeTypes, objectMapper ->
            PendingRequestSerializer(mimeTypes, objectMapper)
        }

        registerDynamicDeserializers(pendingRequestToken) { mimeTypes, objectMapper ->
            PendingRequestDeserializer(pendingRequestToken, mimeTypes, objectMapper)
        }
    }
}