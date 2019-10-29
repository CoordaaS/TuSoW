package it.unibo.coordination.linda.cli

import java.net.URL

data class TupleSpaceID(val name: String, val type: TupleSpaceTypes, val host: URL)