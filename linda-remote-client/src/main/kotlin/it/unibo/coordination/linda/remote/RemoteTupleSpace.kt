package it.unibo.coordination.linda.remote

import it.unibo.coordination.linda.core.Match
import it.unibo.coordination.linda.core.Template
import it.unibo.coordination.linda.core.Tuple
import it.unibo.coordination.linda.core.TupleSpace
import java.net.URL

interface RemoteTupleSpace<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>> : TupleSpace<T, TT, K, V, M> {
    val service: URL
    val url: URL
}