package it.unibo.coordination.linda.text

import it.unibo.coordination.Engines.defaultEngine
import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.TupleSpace
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.regex.Pattern

interface TextualSpace : TupleSpace<StringTuple, RegexTemplate, Any, String, RegularMatch> {
    @JvmDefault
    fun readTuple(template: Pattern): Promise<StringTuple> {
        return readTuple(RegexTemplate.of(template))
    }

    @JvmDefault
    fun read(template: Pattern): Promise<RegularMatch> {
        return read(RegexTemplate.of(template))
    }

    @JvmDefault
    fun tryReadTuple(template: Pattern): Promise<Optional<StringTuple>> {
        return tryReadTuple(RegexTemplate.of(template))
    }

    @JvmDefault
    fun takeTuple(template: Pattern): Promise<StringTuple> {
        return takeTuple(RegexTemplate.of(template))
    }

    @JvmDefault
    fun take(template: Pattern): Promise<RegularMatch> {
        return take(RegexTemplate.of(template))
    }

    @JvmDefault
    fun tryTakeTuple(template: Pattern): Promise<Optional<StringTuple>> {
        return tryTakeTuple(RegexTemplate.of(template))
    }

    @JvmDefault
    fun absent(template: Pattern): Promise<RegularMatch> {
        return absent(RegexTemplate.of(template))
    }

    @JvmDefault
    fun tryAbsentTuple(template: Pattern): Promise<Optional<StringTuple>> {
        return tryAbsentTuple(RegexTemplate.of(template))
    }

    @JvmDefault
    fun tryRead(template: Pattern): Promise<RegularMatch> {
        return tryRead(RegexTemplate.of(template))
    }

    @JvmDefault
    fun tryTake(template: Pattern): Promise<RegularMatch> {
        return tryTake(RegexTemplate.of(template))
    }

    @JvmDefault
    fun tryAbsent(template: Pattern): Promise<RegularMatch> {
        return tryAbsent(RegexTemplate.of(template))
    }

    @JvmDefault
    override fun String.toTuple(): StringTuple {
        return StringTuple.of(this)
    }

    @JvmDefault
    override fun String.toTemplate(): RegexTemplate {
        return RegexTemplate.of(this)
    }

    companion object {
        @JvmStatic
        fun local(name: String?, executorService: ExecutorService): TextualSpace {
            return when (name) {
                null -> TextualSpaceImpl(executorService)
                else -> TextualSpaceImpl(name, executorService)
            }
        }

        @JvmStatic
        fun local(name: String?): TextualSpace {
            return local(name, defaultEngine)
        }

        @JvmStatic
        fun local(executorService: ExecutorService): TextualSpace {
            return local(null, executorService)
        }
    }
}