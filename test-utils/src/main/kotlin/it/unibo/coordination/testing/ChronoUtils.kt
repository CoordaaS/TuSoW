package it.unibo.coordination.testing

import org.junit.Assert
import java.time.Duration

inline fun chronoMillis(action: () -> Unit): Duration {
    val initTime = System.currentTimeMillis()
    action()
    val endTime = System.currentTimeMillis()
    return Duration.ofMillis(endTime - initTime)
}

inline fun chronoNano(action: () -> Unit): Duration {
    val initTime = System.nanoTime()
    action()
    val endTime = System.nanoTime()
    return Duration.ofNanos(endTime - initTime)
}

fun assertLastsAtLeast(minDuration: Duration, action: () -> Unit) {
    val elapsedTime = chronoMillis(action)
    Assert.assertTrue(
            "Action should have lasted at least $minDuration ms, while it lasted only $elapsedTime ms",
            elapsedTime >= minDuration
    )
}