import TusowGRPC.*
import TusowServiceGrpc.TusowServiceStub
import io.grpc.BindableService
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import it.unibo.coordaas.tusow.grpc.logic.LogicGRPCHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

class LogicClientTest {

    private lateinit var server: Server
    private lateinit var stub: TusowServiceStub
    private lateinit var testLogicTupleSpace: TupleSpaceID

    private fun createChannel(ip: String, port: Int) = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build()

    private fun createStub(ip: String, port: Int) = TusowServiceGrpc.newStub(createChannel(ip, port))

    private fun createServer(port: Int, serviceClass: BindableService) =
        ServerBuilder.forPort(port).addService(serviceClass).build()

    private fun createTupleSpace(id: String, type: TupleSpaceType) =
        TupleSpaceID.newBuilder().setId(id).setType(type).build()

    private fun startServer() {
        server = createServer(PORT, LogicGRPCHandler())
        server.start()
    }

    private fun connectStub() {
        stub = createStub(IP, PORT)
    }

    private fun createTuple(key: String, value: String) = Tuple.newBuilder().setKey(key).setValue(value).build()

    private fun createWriteRequest(tuple: Tuple, tupleSpaceID: TupleSpaceID) =
        WriteRequest.newBuilder().setTuple(tuple).setTupleSpaceID(tupleSpaceID).build()

    private fun createLogicTemplate(query: String) = Template.Logic.newBuilder().setQuery(query).build()

    private fun createLogicReadOrTakeRequest(template: Template.Logic, tupleSpaceID: TupleSpaceID) =
        ReadOrTakeRequest.newBuilder().setTupleSpaceID(tupleSpaceID).setLogicTemplate(template).build()

    private fun createTuplesList(vararg tuples: Tuple) = TuplesList.newBuilder().addAllTuples(tuples.toList()).build()

    private fun createWriteAllRequest(tuplesList: TuplesList, tupleSpaceID: TupleSpaceID) =
        WriteAllRequest.newBuilder().setTuplesList(tuplesList).setTupleSpaceID(tupleSpaceID).build()

    private fun createLogicTemplatesList(vararg queries: String) = TemplatesList.LogicTemplatesList.newBuilder()
        .addAllQueries(queries.toList().map { createLogicTemplate(it) }).build()

    private fun createReadOrTakeAllRequest(queriesList: TemplatesList.LogicTemplatesList, tupleSpaceID: TupleSpaceID) =
        ReadOrTakeAllRequest.newBuilder().setLogicTemplateList(queriesList).setTupleSpaceID(tupleSpaceID).build()

    private fun insertTupleSpace(
        tupleSpaceID: TupleSpaceID,
        stub: TusowServiceStub,
        onNext: ((value: IOResponse) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.createTupleSpace(tupleSpaceID, object : StreamObserver<IOResponse> {
            override fun onNext(value: IOResponse) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }
        })
    }

    private fun validateTupleSpace(
        tupleSpaceID: TupleSpaceID,
        stub: TusowServiceStub,
        onNext: ((value: IOResponse) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.validateTupleSpace(tupleSpaceID, object : StreamObserver<IOResponse> {
            override fun onNext(value: IOResponse) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }

        })
    }

    private fun writeTuple(
        writeRequest: WriteRequest,
        stub: TusowServiceStub,
        onNext: ((value: IOResponse) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.write(writeRequest, object : StreamObserver<IOResponse> {
            override fun onNext(value: IOResponse) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }
        })
    }

    private fun readTuple(
        readRequest: ReadOrTakeRequest,
        stub: TusowServiceStub,
        onNext: ((value: Tuple) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.read(readRequest, object : StreamObserver<Tuple> {
            override fun onNext(value: Tuple) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }
        })
    }

    private fun takeTuple(
        takeRequest: ReadOrTakeRequest,
        stub: TusowServiceStub,
        onNext: ((value: Tuple) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.take(takeRequest, object : StreamObserver<Tuple> {
            override fun onNext(value: Tuple) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }
        })
    }

    private fun writeAll(
        writeAllRequest: WriteAllRequest,
        stub: TusowServiceStub,
        onNext: ((value: IOResponseList) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.writeAll(writeAllRequest, object : StreamObserver<IOResponseList> {
            override fun onNext(value: IOResponseList) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }

        })
    }

    private fun readAll(
        readAllRequest: ReadOrTakeAllRequest,
        stub: TusowServiceStub,
        onNext: ((value: TuplesList) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.readAll(readAllRequest, object : StreamObserver<TuplesList> {
            override fun onNext(value: TuplesList) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }
        })
    }

    private fun takeAll(
        takeAllRequest: ReadOrTakeAllRequest,
        stub: TusowServiceStub,
        onNext: ((value: TuplesList) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.takeAll(takeAllRequest, object : StreamObserver<TuplesList> {
            override fun onNext(value: TuplesList) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }
        })
    }

    private fun writeAllAsStream(
        writeAllRequest: WriteAllRequest,
        stub: TusowServiceStub,
        onNext: ((value: IOResponse) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {

        stub.writeAllAsStream(writeAllRequest, object : StreamObserver<IOResponse> {
            override fun onNext(value: IOResponse) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }
        })
    }

    private fun readAllStream(
        readAllRequest: ReadOrTakeAllRequest,
        stub: TusowServiceStub,
        onNext: ((value: Tuple) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.readAllAsStream(readAllRequest, object : StreamObserver<Tuple> {
            override fun onNext(value: Tuple) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }
        })
    }

    private fun takeAllStream(
        takeAllRequest: ReadOrTakeAllRequest,
        stub: TusowServiceStub,
        onNext: ((value: Tuple) -> Unit)?,
        onError: ((t: Throwable?) -> Unit)?,
        onCompleted: (() -> Unit)?
    ) {
        stub.takeAllAsStream(takeAllRequest, object : StreamObserver<Tuple> {
            override fun onNext(value: Tuple) {
                onNext?.invoke(value)
            }

            override fun onError(t: Throwable?) {
                onError?.invoke(t)
            }

            override fun onCompleted() {
                onCompleted?.invoke()
            }
        })
    }

    private fun defaultOnErrorFunction(t: Throwable?) = t?.printStackTrace()

    private fun createTestLogicTupleSpace(tupleSpaceName: String) {
        testLogicTupleSpace = createTupleSpace(tupleSpaceName, TupleSpaceType.LOGIC)
        var completed = false
        val onNext = fun(value: IOResponse) {
            println("Response ${value.response}")
        }
        val onCompleted = fun() { completed = true }
        insertTupleSpace(testLogicTupleSpace, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        runBlocking {
            while (!completed) {
                delay(100)
            }
        }
    }

    private fun writeTestLogicTuple(vararg tupleValues: String) {
        tupleValues.forEach {
            val tuple = createTuple("a(1)", it)
            val writeRequest = createWriteRequest(tuple, testLogicTupleSpace)
            var completed = false
            val onCompleted = fun() {
                completed = true
            }
            writeTuple(writeRequest, stub, onNext = null, onError = ::defaultOnErrorFunction, onCompleted = onCompleted)
            runBlocking {
                while (!completed) {
                    delay(100)
                }
            }
        }
    }

    @Before
    fun initializeTestResources() {
        startServer()
        connectStub()
        createTestLogicTupleSpace("testLogicTupleSpace")
        writeTestLogicTuple("a(1)", "b(1)", "c(1)", "d(1)")
    }

    @Test
    fun testLogicTupleSpaceCreation() {
        val logicTupleSpace = createTupleSpace("logicTupleSpace", TupleSpaceType.TEXTUAL)
        var response = false
        val onNext = fun(value: IOResponse) {
            response = value.response
        }
        val onCompleted = fun() { server.shutdown() }
        insertTupleSpace(logicTupleSpace, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(response)
    }

    @Test
    fun testTupleSpaceValidation() {
        var response = false
        val onNext = fun(value: IOResponse) {
            response = value.response
        }
        val onCompleted = fun() { server.shutdown() }
        validateTupleSpace(testLogicTupleSpace, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(response)
    }

    @Test
    fun testLogicTupleWrite() {
        val tuple = createTuple("g(X)", "g(1)")
        val writeRequest = createWriteRequest(tuple, testLogicTupleSpace)
        var response = false
        val onNext = fun(value: IOResponse) {
            response = value.response
        }
        val onCompleted = fun() { server.shutdown() }
        writeTuple(writeRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(response)
    }

    @Test
    fun testLogicTupleRead() {
        val logicTemplate = createLogicTemplate("a(X)")
        val readRequest = createLogicReadOrTakeRequest(logicTemplate, testLogicTupleSpace)
        var tuple = ""
        val onNext = fun(value: Tuple) {
            tuple = value.value
        }
        val onCompleted = fun() { server.shutdown() }
        readTuple(readRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertEquals(tuple, "a(1)")
    }

    @Test
    fun testLogicTupleTake() {
        val logicTemplate = createLogicTemplate("a(X)")
        val readRequest = createLogicReadOrTakeRequest(logicTemplate, testLogicTupleSpace)
        var tuple = ""
        val onNext = fun(value: Tuple) {
            tuple = value.value
        }
        val onCompleted = fun() { server.shutdown() }
        takeTuple(readRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertEquals(tuple, "a(1)")
    }

    @Test
    fun testWriteAll() {
        val tuplesList = createTuplesList(
            createTuple("a(X)", "a(1)"),
            createTuple("b(X)", "b(1)"),
            createTuple("c(X)", "c(1)")
        )
        val writeAllRequest = createWriteAllRequest(tuplesList, testLogicTupleSpace)
        var responses: Array<Boolean> = Array(0) { false }
        val onNext = fun(value: IOResponseList) {
            responses = value.responsesList.map { it.response }.toTypedArray()
        }
        val onCompleted = fun() { server.shutdown() }
        writeAll(writeAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        responses.forEach { assertTrue(it) }
    }

    @Test
    fun testReadAll() {
        val queriesList = createLogicTemplatesList("a(X)", "b(X)", "c(X)")
        val readAllRequest = createReadOrTakeAllRequest(queriesList, testLogicTupleSpace)
        var tuples: List<String> = LinkedList()
        val onNext = fun(value: TuplesList) {
            tuples = value.tuplesList.map { it.value }.toList()
        }
        val onCompleted = fun() { server.shutdown() }
        readAll(readAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(tuples.containsAll(listOf("a(1)", "b(1)", "c(1)")))
    }

    @Test
    fun testTakeAll() {
        val queriesList = createLogicTemplatesList("a(X)", "b(X)", "c(X)")
        val readAllRequest = createReadOrTakeAllRequest(queriesList, testLogicTupleSpace)
        var tuples: List<String> = LinkedList()
        val onNext = fun(value: TuplesList) {
            tuples = value.tuplesList.map { it.value }.toList()
        }
        val onCompleted = fun() { server.shutdown() }
        takeAll(readAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(tuples.containsAll(listOf("a(1)", "b(1)", "c(1)")))
    }

    @Test
    fun testWriteAllAsStream() {
        val tuplesList = createTuplesList(
            createTuple("a(X)", "a(1)"),
            createTuple("b(X)", "b(1)"),
            createTuple("c(X)", "c(1)")
        )
        val writeAllRequest = createWriteAllRequest(tuplesList, testLogicTupleSpace)
        val ioResponses = LinkedList<IOResponse>()
        val onNext = fun(value: IOResponse) { ioResponses.add(value) }
        val onCompleted = fun() { server.shutdown() }
        writeAllAsStream(writeAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertEquals(ioResponses.map { it.response }.toList(), listOf(true, true, true))
    }

    @Test
    fun testReadAllStream() {
        val queriesList = createLogicTemplatesList("a(X)", "b(X)", "c(X)")
        val readAllRequest = createReadOrTakeAllRequest(queriesList, testLogicTupleSpace)
        val tuples = LinkedList<String>()
        val onNext = fun(value: Tuple) {
            tuples.add(value.value)
        }
        val onCompleted = fun() { server.shutdown() }
        readAllStream(readAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(tuples.containsAll(listOf("a(1)", "b(1)", "c(1)")))
    }


    @Test
    fun testTakeAllStream() {
        val queriesList = createLogicTemplatesList("a(X)", "b(X)", "c(X)")
        val readAllRequest = createReadOrTakeAllRequest(queriesList, testLogicTupleSpace)
        val tuples = LinkedList<String>()
        val onNext = fun(value: Tuple) {
            tuples.add(value.value)
        }
        val onCompleted = fun() { server.shutdown() }
        takeAllStream(readAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(tuples.containsAll(listOf("a(1)", "b(1)", "c(1)")))
    }

    companion object {
        private const val PORT = 8000
        private const val IP = "localhost"
    }

}