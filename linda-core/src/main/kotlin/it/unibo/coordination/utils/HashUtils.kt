package it.unibo.coordination.utils

import java.nio.ByteBuffer

val Any.readableHashString: String
    get() {
        return ByteBuffer.allocate(Int.SIZE_BYTES)
                .putInt(System.identityHashCode(this))
                .array()
                .joinToString { it.toString(16) }
                .padStart(8, '0')
    }