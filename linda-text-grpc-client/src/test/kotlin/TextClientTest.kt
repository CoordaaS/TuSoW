import TusowGRPC.*
import TusowServiceGrpc.TusowServiceStub
import io.grpc.BindableService
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import it.unibo.coordaas.tusow.grpc.text.TextualGRPCHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

class TextClientTest {

    private lateinit var server: Server
    private lateinit var stub: TusowServiceStub
    private lateinit var testTextualTupleSpace: TupleSpaceID

    private fun createChannel(ip: String, port: Int) = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build()

    private fun createStub(ip: String, port: Int) = TusowServiceGrpc.newStub(createChannel(ip, port))

    private fun createServer(port: Int, serviceClass: BindableService) =
        ServerBuilder.forPort(port).addService(serviceClass).build()

    private fun createTupleSpace(id: String, type: TupleSpaceType) =
        TupleSpaceID.newBuilder().setId(id).setType(type).build()

    private fun startServer() {
        server = createServer(PORT, TextualGRPCHandler())
        server.start()
    }

    private fun connectStub() {
        stub = createStub(IP, PORT)
    }

    private fun createTuple(key: String, value: String) = Tuple.newBuilder().setKey(key).setValue(value).build()

    private fun createWriteRequest(tuple: Tuple, tupleSpaceID: TupleSpaceID) =
        WriteRequest.newBuilder().setTuple(tuple).setTupleSpaceID(tupleSpaceID).build()

    private fun createTextualTemplate(regex: String) = Template.Textual.newBuilder().setRegex(regex).build()

    private fun createTextualReadOrTakeRequest(template: Template.Textual, tupleSpaceID: TupleSpaceID) =
        ReadOrTakeRequest.newBuilder().setTupleSpaceID(tupleSpaceID).setTextualTemplate(template).build()

    private fun createTuplesList(vararg tuples: Tuple) = TuplesList.newBuilder().addAllTuples(tuples.toList()).build()

    private fun createWriteAllRequest(tuplesList: TuplesList, tupleSpaceID: TupleSpaceID) =
        WriteAllRequest.newBuilder().setTuplesList(tuplesList).setTupleSpaceID(tupleSpaceID).build()

    private fun createTextualTemplatesList(vararg regexes: String) = TemplatesList.TextualTemplatesList.newBuilder()
        .addAllRegexes(regexes.toList().map { createTextualTemplate(it) }).build()

    private fun createReadOrTakeAllRequest(regexesList: TemplatesList.TextualTemplatesList, tupleSpaceID: TupleSpaceID) =
        ReadOrTakeAllRequest.newBuilder().setTextualTemplateList(regexesList).setTupleSpaceID(tupleSpaceID).build()

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

    private fun createTestTextualTupleSpace(tupleSpaceName: String) {
        testTextualTupleSpace = createTupleSpace(tupleSpaceName, TupleSpaceType.TEXTUAL)
        var completed = false
        val onNext = fun(value: IOResponse) {
            println("Response ${value.response}")
        }
        val onCompleted = fun() { completed = true }
        insertTupleSpace(testTextualTupleSpace, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        runBlocking {
            while (!completed) {
                delay(100)
            }
        }
    }

    private fun writeTestTextualTuple(vararg tupleValues: String) {
        tupleValues.forEach {
            val tuple = createTuple("testTuple", it)
            val writeRequest = createWriteRequest(tuple, testTextualTupleSpace)
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
        createTestTextualTupleSpace("testTextualTupleSpace")
        writeTestTextualTuple("hello world", "hello world 1", "hello world 2", "hello world 3")
    }

    @Test
    fun testTextualTupleSpaceCreation() {
        val textualTupleSpace = createTupleSpace("textualTupleSpace", TupleSpaceType.TEXTUAL)
        var response = false
        val onNext = fun(value: IOResponse) {
            response = value.response
        }
        val onCompleted = fun() { server.shutdown() }
        insertTupleSpace(textualTupleSpace, stub, onNext, ::defaultOnErrorFunction, onCompleted)
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
        validateTupleSpace(testTextualTupleSpace, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(response)
    }

    @Test
    fun testTextualTupleWrite() {
        val tuple = createTuple("test", "hello world")
        val writeRequest = createWriteRequest(tuple, testTextualTupleSpace)
        var response = false
        val onNext = fun(value: IOResponse) {
            response = value.response
        }
        val onCompleted = fun() { server.shutdown() }
        writeTuple(writeRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(response)
    }

    //TODO Remove the key attribute from the Template class
    @Test
    fun testTextualTupleRead() {
        val textualTemplate = createTextualTemplate("hello world")
        val readRequest = createTextualReadOrTakeRequest(textualTemplate, testTextualTupleSpace)
        var tuple = ""
        val onNext = fun(value: Tuple) {
            tuple = value.value
        }
        val onCompleted = fun() { server.shutdown() }
        readTuple(readRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertEquals(tuple, "hello world")
    }

    @Test
    fun testTextualTupleTake() {
        val textualTemplate = createTextualTemplate("hello world")
        val readRequest = createTextualReadOrTakeRequest(textualTemplate, testTextualTupleSpace)
        var tuple = ""
        val onNext = fun(value: Tuple) {
            tuple = value.value
        }
        val onCompleted = fun() { server.shutdown() }
        takeTuple(readRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertEquals(tuple, "hello world")
    }

    @Test
    fun testWriteAll() {
        val tuplesList = createTuplesList(
            createTuple("test", "hello world 1"),
            createTuple("test", "hello world 2"),
            createTuple("test", "hello world 3")
        )
        val writeAllRequest = createWriteAllRequest(tuplesList, testTextualTupleSpace)
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
        val regexesList = createTextualTemplatesList("hello world 1", "hello world 2", "hello world 3")
        val readAllRequest = createReadOrTakeAllRequest(regexesList, testTextualTupleSpace)
        var tuples: List<String> = LinkedList()
        val onNext = fun(value: TuplesList) {
            tuples = value.tuplesList.map { it.value }.toList()
        }
        val onCompleted = fun() { server.shutdown() }
        readAll(readAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(tuples.containsAll(listOf("hello world 1", "hello world 2", "hello world 3")))
    }

    @Test
    fun testTakeAll() {
        val regexesList = createTextualTemplatesList("hello world 1", "hello world 2", "hello world 3")
        val readAllRequest = createReadOrTakeAllRequest(regexesList, testTextualTupleSpace)
        var tuples: List<String> = LinkedList()
        val onNext = fun(value: TuplesList) {
            tuples = value.tuplesList.map { it.value }.toList()
        }
        val onCompleted = fun() { server.shutdown() }
        takeAll(readAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(tuples.containsAll(listOf("hello world 1", "hello world 2", "hello world 3")))
    }

    @Test
    fun testWriteAllAsStream() {
        val tuplesList = createTuplesList(
            createTuple("test", "hello world 1"),
            createTuple("test", "hello world 2"),
            createTuple("test", "hello world 3")
        )
        val writeAllRequest = createWriteAllRequest(tuplesList, testTextualTupleSpace)
        val ioResponses = LinkedList<IOResponse>()
        val onNext = fun(value: IOResponse) { ioResponses.add(value) }
        val onCompleted = fun() { server.shutdown() }
        writeAllAsStream(writeAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertEquals(ioResponses.map { it.response }.toList(), listOf(true, true, true))
    }

    @Test
    fun testReadAllStream() {
        val regexesList = createTextualTemplatesList("hello world 1", "hello world 2", "hello world 3")
        val readAllRequest = createReadOrTakeAllRequest(regexesList, testTextualTupleSpace)
        val tuples = LinkedList<String>()
        val onNext = fun(value: Tuple) {
            tuples.add(value.value)
        }
        val onCompleted = fun() { server.shutdown() }
        readAllStream(readAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(tuples.containsAll(listOf("hello world 1", "hello world 2", "hello world 3")))
    }


    @Test
    fun testTakeAllStream() {
        val regexesList = createTextualTemplatesList("hello world 1", "hello world 2", "hello world 3")
        val readAllRequest = createReadOrTakeAllRequest(regexesList, testTextualTupleSpace)
        val tuples = LinkedList<String>()
        val onNext = fun(value: Tuple) {
            tuples.add(value.value)
        }
        val onCompleted = fun() { server.shutdown() }
        takeAllStream(readAllRequest, stub, onNext, ::defaultOnErrorFunction, onCompleted)
        server.awaitTermination()
        assertTrue(tuples.containsAll(listOf("hello world 1", "hello world 2", "hello world 3")))
    }

    companion object {
        private const val PORT = 8000
        private const val IP = "localhost"
    }

}