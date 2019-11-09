package it.unibo.coordination.linda.test

import it.unibo.coordination.linda.core.*
import it.unibo.coordination.linda.core.events.OperationEvent
import it.unibo.coordination.linda.core.events.TupleEvent
import it.unibo.coordination.linda.core.events.TupleSpaceEvent
import it.unibo.coordination.testing.ConcurrentTestHelper
import it.unibo.coordination.utils.indexOf
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.util.*
import java.util.concurrent.*
import kotlin.streams.toList

abstract class TestTupleSpaceInspectability<T : Tuple<T>, TT : Template<T>, K, V, M : Match<T, TT, K, V>, TS : InspectableTupleSpace<T, TT, K, V, M>>(tupleTemplateFactory: TupleTemplateFactory<T, TT, K, V, M>) : TestBaseLinda<T, TT, K, V, M>(tupleTemplateFactory) {

    private lateinit var executor: ExecutorService
    private lateinit var tupleSpace: InspectableTupleSpace<T, TT, K, V, M>
    private lateinit var test: ConcurrentTestHelper

    protected abstract fun getTupleSpace(executor: ExecutorService): TS

    @Before
    fun setUp() {
        executor = Executors.newSingleThreadExecutor()
        tupleSpace = getTupleSpace(executor)
        test = ConcurrentTestHelper()
    }

    @After
    fun tearDown() {
        executor.shutdown()
        executor.awaitTermination(3, TimeUnit.SECONDS)
    }

    @Test
    fun testReadInvocation() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val template = aTemplate

        val expectedEvent = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.READ, template)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        try {
            await(tupleSpace.readTuple(template))
            Assert.fail()
        } catch (e: TimeoutException) {
            assertEquals(1, observableBehaviour.size.toLong())
            assertEquals(
                    listOf(expectedEvent),
                    observableBehaviour
            )
        }

    }


    @Test
    fun testTakeInvocation() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val template = aTemplate

        val expectedEvent = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TAKE, template)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        try {
            await(tupleSpace.takeTuple(template))
            Assert.fail()
        } catch (e: TimeoutException) {
            assertEquals(1, observableBehaviour.size.toLong())
            assertEquals(
                    listOf(expectedEvent),
                    observableBehaviour
            )
        }

    }

    @Test
    fun testWrite() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tuple = aTuple

        val expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tuple)
        val expectedEvent2 = TupleEvent.afterWriting(tupleSpace.name, tuple)
        val expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tuple)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.write(tuple))

        await(executor)

        assertEquals(3, observableBehaviour.size.toLong())
        assertEquals(
                listOf(expectedEvent1, expectedEvent2, expectedEvent3),
                observableBehaviour
        )
    }


    @Test
    fun testReadCompletion1() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tupleTemplate = aTupleAndATemplateMatchingIt

        val expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tupleTemplate.value0)
        val expectedEvent2 = TupleEvent.afterWriting(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.value0)
        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.READ, tupleTemplate.value1)
        val expectedEvent5 = TupleEvent.afterReading(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.write(tupleTemplate.value0))
        await(tupleSpace.read(tupleTemplate.value1))

        assertEquals(6, observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour.toSet()
        )
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2))
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent5))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
    }


    @Test
    fun testReadCompletion2() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tupleTemplate = aTupleAndATemplateMatchingIt

        val expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.READ, tupleTemplate.value1)
        val expectedEvent2 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tupleTemplate.value0)
        val expectedEvent3 = TupleEvent.afterWriting(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent4 = TupleEvent.afterReading(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent5 = expectedEvent2.toTupleReturningCompletion(tupleTemplate.value0)
        val expectedEvent6 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        val read = tupleSpace.read(tupleTemplate.value1)
        await(tupleSpace.write(tupleTemplate.value0))
        await(read)

        assertEquals(6, observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour.toSet()
        )
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2))
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent5))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
    }

    @Test
    fun testTryReadFail() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val template = aTemplate

        val expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TRY_READ, template)
        val expectedEvent2 = expectedEvent1.toTuplesReturningCompletion()

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.tryReadTuple(template))

        assertEquals(2, observableBehaviour.size.toLong())
        assertEquals(
                listOf(expectedEvent1, expectedEvent2),
                observableBehaviour
        )

    }

    @Test
    fun testTryReadSuccess() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tupleTemplate = aTupleAndATemplateMatchingIt

        val expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tupleTemplate.value0)
        val expectedEvent2 = TupleEvent.afterWriting(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.value0)
        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TRY_READ, tupleTemplate.value1)
        val expectedEvent5 = TupleEvent.afterReading(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.write(tupleTemplate.value0))
        await(tupleSpace.tryRead(tupleTemplate.value1))

        assertEquals(6, observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour.toSet()
        )
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2))
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent5))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
    }

    @Test
    fun testTakeCompletion1() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tupleTemplate = aTupleAndATemplateMatchingIt

        val expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tupleTemplate.value0)
        val expectedEvent2 = TupleEvent.afterWriting(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.value0)
        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TAKE, tupleTemplate.value1)
        val expectedEvent5 = TupleEvent.afterTaking(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.write(tupleTemplate.value0))
        await(tupleSpace.takeTuple(tupleTemplate.value1))

        assertEquals(6, observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour.toSet()
        )
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2))
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent5))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
    }

    @Test
    fun testTakeCompletion2() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tupleTemplate = aTupleAndATemplateMatchingIt

        val expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TAKE, tupleTemplate.value1)
        val expectedEvent2 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tupleTemplate.value0)
        val expectedEvent3 = TupleEvent.afterWriting(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent4 = TupleEvent.afterTaking(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent5 = expectedEvent2.toTupleReturningCompletion(tupleTemplate.value0)
        val expectedEvent6 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        val take = tupleSpace.takeTuple(tupleTemplate.value1)
        await(tupleSpace.write(tupleTemplate.value0))
        await(take)

        assertEquals(6, observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour.toSet()
        )
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2))
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent5))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
    }

    @Test
    fun testTryTakeSuccess() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tupleTemplate = aTupleAndATemplateMatchingIt

        val expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tupleTemplate.value0)
        val expectedEvent2 = TupleEvent.afterWriting(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.value0)
        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TRY_TAKE, tupleTemplate.value1)
        val expectedEvent5 = TupleEvent.afterTaking(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.write(tupleTemplate.value0))
        await(tupleSpace.tryTake(tupleTemplate.value1))

        assertEquals(6, observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour.toSet()
        )
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2))
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent5))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))

    }

    @Test
    fun testTryTakeFail() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val template = aTemplate

        val expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TRY_TAKE, template)
        val expectedEvent2 = expectedEvent1.toTuplesReturningCompletion()

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.tryTakeTuple(template))

        assertEquals(2, observableBehaviour.size.toLong())
        assertEquals(
                listOf(expectedEvent1, expectedEvent2),
                observableBehaviour
        )

    }

    @Test
    fun testWriteAll() {

        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tuples = someTuples

        val expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace.name, OperationType.WRITE_ALL, tuples)
        val expectedEvents2 = tuples.stream()
                .map { TupleEvent.afterWriting(tupleSpace.name, it) }
                .toList()

        val expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.writeAll(tuples))

        assertEquals(tuples.size + 2, observableBehaviour.size)
        assertEquals(
                setOf(expectedEvent1, expectedEvent3) + expectedEvents2.toSet(),
                observableBehaviour.toSet()
        )
        expectedEvents2.forEach{
            assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(it))
        }
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
    }

    @Test
    fun testGet() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tuples = someTuples

        val expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace.name, OperationType.WRITE_ALL, tuples)
        val expectedEvents2 = tuples.stream()
                .map { TupleEvent.afterWriting(tupleSpace.name, it) }
                .toList()
        val expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples)
        val expectedEvent4 = OperationEvent.nothingAcceptingInvocation<T, TT>(tupleSpace.name, OperationType.GET)
        val expectedEvents5 = tuples.stream()
                .map { TupleEvent.afterReading(tupleSpace.name, it) }
                .toList()
        val expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.writeAll(tuples))
        await(tupleSpace.get())

        assertEquals((tuples.size * 2 + 4).toLong(), observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent3, expectedEvent4, expectedEvent6) + expectedEvents2.toSet() + expectedEvents5.toSet(),
                observableBehaviour.toSet()
        )
        expectedEvents2.forEach{
            assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(it))
        }
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
        expectedEvents5.forEach{
            assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(it))
        }
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
    }

    @Test
    fun testReadAllCompletation1() {

        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tuples = someTuplesOfOneSort

        val expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace.name, OperationType.WRITE_ALL, tuples.value0)
        val expectedEvents2 = tuples.value0.stream()
                .map { TupleEvent.afterWriting(tupleSpace.name, it) }
                .toList()
        val expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples.value0)
        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.READ_ALL, tuples.value1)
        val expectedEvents5 = tuples.value0.stream()
                .map { TupleEvent.afterReading(tupleSpace.name, it) }
                .toList()
        val expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.writeAll(tuples.value0))
        await(tupleSpace.readAllTuples(tuples.value1))

        assertEquals((tuples.value0.size * 2 + 4).toLong(), observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent3, expectedEvent4, expectedEvent6) + expectedEvents2.toSet() + expectedEvents5.toSet(),
                observableBehaviour.toSet()
        )
        expectedEvents2.forEach{
            assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(it))
        }
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
        expectedEvents5.forEach{
            assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(it))
        }
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
    }

//    @Test
//    fun testReadAllCompletation2() {
//
//        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()
//
//        val tuples = someTuplesOfOneSort
//
//        val expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace.name, OperationType.WRITE_ALL, tuples.value0)
//        val expectedEvents2 = tuples.value0.stream()
//                .map { TupleEvent.afterWriting(tupleSpace.name, it) }
//                .toList()
//        val expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples.value0)
//        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.READ_ALL, tuples.value1)
//        val expectedEvents5 = tuples.value0.stream()
//                .map { TupleEvent.afterReading(tupleSpace.name, it) }
//                .toList()
//        val expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples.value0)
//
//        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
//        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
//        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }
//
//        val read = tupleSpace.readAllTuples(tuples.value1)
//        await(tupleSpace.writeAll(tuples.value0))
//        await(read)
//
//        assertEquals((tuples.value0.size * 2 + 4).toLong(), observableBehaviour.size.toLong())
//        assertEquals(
//                setOf(expectedEvent1, expectedEvent3, expectedEvent4, expectedEvent6) + expectedEvents2.toSet() + expectedEvents5.toSet(),
//                observableBehaviour.toSet()
//        )
//        expectedEvents2.forEach{
//            assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(it))
//        }
//        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
//        expectedEvents5.forEach{
//            assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(it))
//        }
//        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
//    }

    @Test
    fun testTakeAllCompletation1() {

        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tuples = someTuplesOfOneSort

        val expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace.name, OperationType.WRITE_ALL, tuples.value0)
        val expectedEvents2 = tuples.value0.stream()
                .map { TupleEvent.afterWriting(tupleSpace.name, it) }
                .toList()
        val expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples.value0)
        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TAKE_ALL, tuples.value1)
        val expectedEvents5 = tuples.value0.stream()
                .map { TupleEvent.afterTaking(tupleSpace.name, it) }
                .toList()
        val expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.writeAll(tuples.value0))
        await(tupleSpace.takeAll(tuples.value1))

        assertEquals((tuples.value0.size * 2 + 4).toLong(), observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent3, expectedEvent4, expectedEvent6) + expectedEvents2.toSet() + expectedEvents5.toSet(),
                observableBehaviour.toSet()
        )
        expectedEvents2.forEach{
            assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(it))
        }
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
        expectedEvents5.forEach {
            assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(it))
        }
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
    }

//    @Test
//    fun testTakeAllCompletation2() {
//
//        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()
//
//        val tuples = someTuplesOfOneSort
//
//        val expectedEvent1 = OperationEvent.tuplesAcceptingInvocation(tupleSpace.name, OperationType.WRITE_ALL, tuples.value0)
//        val expectedEvents2 = tuples.value0.stream()
//                .map { TupleEvent.afterWriting(tupleSpace.name, it) }
//                .toList()
//        val expectedEvent3 = expectedEvent1.toTuplesReturningCompletion(tuples.value0)
//        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TAKE_ALL, tuples.value1)
//        val expectedEvents5 = tuples.value0.stream()
//                .map { TupleEvent.afterTaking(tupleSpace.name, it) }
//                .toList()
//        val expectedEvent6 = expectedEvent4.toTuplesReturningCompletion(tuples.getValue0());
//
//        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
//        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
//        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }
//
//        val take = tupleSpace.takeAllTuples(tuples.value1)
//        await(tupleSpace.writeAll(tuples.value0))
//        await(take)
//
//        assertEquals((tuples.value0.size * 2 + 4).toLong(), observableBehaviour.size.toLong())
//        assertEquals(
//                setOf(expectedEvent1, expectedEvent3, expectedEvent4, expectedEvent6) + expectedEvents2.toSet() + expectedEvents5.toSet(),
//                observableBehaviour.toSet()
//        )
//        expectedEvents2.forEach{
//            assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(it))
//        }
//        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
//        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
//        expectedEvents5.forEach{
//            assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(it))
//        }
//        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
//    }

    @Test
    fun testAbsentInvocation() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tupleTemplate = aTupleAndATemplateMatchingIt

        val expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tupleTemplate.value0)
        val expectedEvent2 = TupleEvent.afterWriting(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.value0)
        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.ABSENT, tupleTemplate.value1)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.write(tupleTemplate.value0))
        try {
            //pending request because there is a tuple with T as template
            await(tupleSpace.absentTemplate(tupleTemplate.value1))
            Assert.fail()
        } catch (e: TimeoutException) {
            assertEquals(4, observableBehaviour.size.toLong())
            assertEquals(
                    listOf(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4),
                    observableBehaviour
            )
        }

    }

    @Test
    fun testAbsentCompletation() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val template = aTemplate

        val expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.ABSENT, template)
        val expectedEvent2 = TupleEvent.afterAbsent(tupleSpace.name, template)
        val expectedEvent3 = expectedEvent1.toTemplateReturningCompletion(template)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.absentTemplate(template))

        assertEquals(3, observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent2, expectedEvent3),
                observableBehaviour.toSet()
        )
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2))
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
    }

    @Test
    fun testAbsentInvocationAndCompletation() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tupleTemplate = aTupleAndATemplateMatchingIt

        val expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tupleTemplate.value0)
        val expectedEvent2 = TupleEvent.afterWriting(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.value0)
        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.ABSENT, tupleTemplate.value1)
        val expectedEvent5 = TupleEvent.afterAbsent(tupleSpace.name, tupleTemplate.value1)
        val expectedEvent6 = expectedEvent4.toTemplateReturningCompletion(tupleTemplate.value1)
        val expectedEvent7 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TAKE, tupleTemplate.value1)
        val expectedEvent8 = TupleEvent.afterTaking(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent9 = expectedEvent7.toTupleReturningCompletion(tupleTemplate.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.write(tupleTemplate.value0))
        val absent = tupleSpace.absentTemplate(tupleTemplate.value1)
        await(tupleSpace.takeTuple(tupleTemplate.value1))
        await(absent)

        assertEquals(9, observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent2, expectedEvent3,
                        expectedEvent4, expectedEvent5, expectedEvent6,
                        expectedEvent7, expectedEvent8, expectedEvent9),
                observableBehaviour.toSet()
        )
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2))
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent5))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent7))
        assertTrue(observableBehaviour.indexOf(expectedEvent7) < observableBehaviour.indexOf(expectedEvent8))
        assertTrue(observableBehaviour.indexOf(expectedEvent7) < observableBehaviour.indexOf(expectedEvent9))
    }


    @Test
    fun testTryAbsentSuccess() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val template = aTemplate

        val expectedEvent1 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TRY_ABSENT, template)
        val expectedEvent2 = expectedEvent1.toTuplesReturningCompletion()

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.tryAbsent(template))

        assertEquals(2, observableBehaviour.size.toLong())
        assertEquals(
                listOf(expectedEvent1, expectedEvent2),
                observableBehaviour
        )

    }

    @Test
    fun testTryAbsentFail() {
        val observableBehaviour = LinkedList<TupleSpaceEvent<T, TT>>()

        val tupleTemplate = aTupleAndATemplateMatchingIt

        val expectedEvent1 = OperationEvent.tupleAcceptingInvocation(tupleSpace.name, OperationType.WRITE, tupleTemplate.value0)
        val expectedEvent2 = TupleEvent.afterWriting(tupleSpace.name, tupleTemplate.value0)
        val expectedEvent3 = expectedEvent1.toTupleReturningCompletion(tupleTemplate.value0)
        val expectedEvent4 = OperationEvent.templateAcceptingInvocation(tupleSpace.name, OperationType.TRY_ABSENT, tupleTemplate.value1)
        val expectedEvent5 = TupleEvent.afterAbsent(tupleSpace.name, tupleTemplate.value1, tupleTemplate.value0)
        val expectedEvent6 = expectedEvent4.toTupleReturningCompletion(tupleTemplate.value0)

        tupleSpace.operationInvoked.bind { observableBehaviour.add(it) }
        tupleSpace.tupleSpaceChanged.bind { observableBehaviour.add(it) }
        tupleSpace.operationCompleted.bind { observableBehaviour.add(it) }

        await(tupleSpace.write(tupleTemplate.value0))
        await(tupleSpace.tryAbsent(tupleTemplate.value1))

        assertEquals(6, observableBehaviour.size.toLong())
        assertEquals(
                setOf(expectedEvent1, expectedEvent2, expectedEvent3, expectedEvent4, expectedEvent5, expectedEvent6),
                observableBehaviour.toSet()
        )
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent2))
        assertTrue(observableBehaviour.indexOf(expectedEvent1) < observableBehaviour.indexOf(expectedEvent3))
        assertTrue(observableBehaviour.indexOf(expectedEvent3) < observableBehaviour.indexOf(expectedEvent4))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent5))
        assertTrue(observableBehaviour.indexOf(expectedEvent4) < observableBehaviour.indexOf(expectedEvent6))
    }

    companion object {

        private val MAX_WAIT = Duration.ofSeconds(2)

        @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
        private fun <T> await(future: Future<T>): T {
            return future.get(MAX_WAIT.toMillis(), TimeUnit.MILLISECONDS)
        }

        @Throws(InterruptedException::class)
        private fun await(engine: ExecutorService): Boolean {
            return engine.awaitTermination(MAX_WAIT.toMillis(), TimeUnit.MILLISECONDS)
        }
    }
}
