@file:JvmName("Extensions")

package it.unibo.coordination.control

import it.unibo.coordination.Engine
import it.unibo.coordination.Promise
import it.unibo.coordination.utils.TimedEngine
import java.time.Duration

fun <E, R> Engine.run(activity: Activity<E, *, R>, input: E): Promise<R> {
    return Runner.asyncOf(activity, this).run(input)
}

fun <E, R> Activity<E, *, R>.start(input: E): Promise<R> {
    return Runner.asyncOf(this).run(input)
}

fun <R> Engine.run(activity: Activity<Unit, *, R>): Promise<R> {
    return Runner.asyncOf(activity, this).run(Unit)
}

fun <R> Activity<Unit, *, R>.start(): Promise<R> {
    return Runner.asyncOf(this).run(Unit)
}

fun <E, R> TimedEngine.run(activity: Activity<E, *, R>, period: Duration, input: E): Promise<R> {
    return Runner.periodicOf(period, activity, this).run(input)
}

fun <E, R> Activity<E, *, R>.startPeriodic(period: Duration, input: E): Promise<R> {
    return Runner.periodicOf(period, this).run(input)
}

fun <R> TimedEngine.run(activity: Activity<Unit, *, R>, period: Duration): Promise<R> {
    return Runner.periodicOf(period, activity, this).run(Unit)
}

fun <R> Activity<Unit, *, R>.startPeriodic(period: Duration): Promise<R> {
    return Runner.periodicOf(period, this).run(Unit)
}

fun <E, T, R> Activity<E, T, R>.runOnCurrentThread(input: E): R {
    return Runner.syncOf(this).run(input).get()
}

fun <R> Activity<Unit, *, R>.runOnCurrentThread(): R {
    return Runner.syncOf(this).run(Unit).get()
}

fun <E, T, R> Activity<E, T, R>.runOnBackgroundThread(input: E): Promise<R> {
    return Runner.backgroundOf(this).run(input)
}

fun <R> Activity<Unit, *, R>.runOnBackgroundThread(): Promise<R> {
    return Runner.backgroundOf(this).run(Unit)
}

