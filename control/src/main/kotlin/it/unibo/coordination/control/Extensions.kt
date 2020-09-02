@file:JvmName("Extensions")

package it.unibo.coordination.control

import it.unibo.coordination.Engine
import it.unibo.coordination.Promise
import it.unibo.coordination.utils.TimedEngine
import java.time.Duration

fun <E, R> Engine.run(environment: E, activity: Activity<E, *, R>): Promise<R> {
    return Runner.asyncOf(activity, this).run(environment)
}

fun <E, R> Activity<E, *, R>.start(environment: E): Promise<R> {
    return Runner.asyncOf(this).run(environment)
}

fun <R> Engine.run(activity: Activity<Unit, *, R>): Promise<R> {
    return Runner.asyncOf(activity, this).run(kotlin.Unit)
}

fun <R> Activity<Unit, *, R>.start(): Promise<R> {
    return Runner.asyncOf(this).run(Unit)
}

fun <E, R> TimedEngine.run(period: Duration, environment: E, activity: Activity<E, *, R>): Promise<R> {
    return Runner.periodicOf(period, activity, this).run(environment)
}

fun <E, R> Activity<E, *, R>.startPeriodic(period: Duration, environment: E): Promise<R> {
    return Runner.periodicOf(period, this).run(environment)
}

fun <R> TimedEngine.run(period: Duration, activity: Activity<Unit, *, R>): Promise<R> {
    return Runner.periodicOf(period, activity, this).run(kotlin.Unit)
}

fun <R> Activity<Unit, *, R>.startPeriodic(period: Duration): Promise<R> {
    return Runner.periodicOf(period, this).run(Unit)
}

fun <E, T, R> Activity<E, T, R>.runOnCurrentThread(environment: E): R {
    return Runner.syncOf(this).run(environment).get()
}

fun <R> Activity<Unit, *, R>.runOnCurrentThread(): R {
    return Runner.syncOf(this).run(Unit).get()
}

fun <E, T, R> Activity<E, T, R>.runOnBackgroundThread(environment: E): Promise<R> {
    return Runner.backgroundOf(this).run(environment)
}

fun <R> Activity<Unit, *, R>.runOnBackgroundThread(): Promise<R> {
    return Runner.backgroundOf(this).run(Unit)
}

