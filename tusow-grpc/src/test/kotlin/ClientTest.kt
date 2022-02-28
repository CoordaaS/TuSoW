import io.grpc.BindableService
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class ClientTest {

    private lateinit var server: Server
    private lateinit var stub: TusowServiceGrpc.TusowServiceStub
    private lateinit var testTextualTupleSpace: TusowGRPC.TupleSpace

    private fun createChannel(ip: String, port: Int) = ManagedChannelBuilder.forAddress(ip, port).usePlaintext().build()

    private fun createStub(ip: String, port: Int) = TusowServiceGrpc.newStub(createChannel(ip, port))

    private fun createServer(port: Int, serviceClass: BindableService) =
        ServerBuilder.forPort(port).addService(serviceClass).build()

    private fun createTupleSpace(name: String, type: TusowGRPC.TupleSpaceType) = TusowGRPC.TupleSpace.newBuilder().setName(name).setType(type).build()

    /*
    @Test
    fun testStubCreation() {
        val server = createServer(PORT, ServiceTest())
        server.start()
        val stub = createStub(IP, 8001)
        runBlocking {
            launch {
                delay(40000)
                server.shutdown()
            }
            server.awaitTermination(60, TimeUnit.SECONDS)
        }
    }
     */

    fun startServer(){
        server = createServer(PORT, ServiceTest())
        server.start()
    }

    private fun connectStub(){
        stub = createStub(IP, PORT)
    }

    fun createTestTextualTupleSpace(){
        testTextualTupleSpace = createTupleSpace("testTextualTupleSpace", TusowGRPC.TupleSpaceType.TEXTUAL)
        var completed = false
        stub.createTupleSpace(testTextualTupleSpace, object : StreamObserver<TusowGRPC.IOResponse> {
            override fun onNext(value: TusowGRPC.IOResponse) {
                println("Response ${value.response}")
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                completed = true
            }
        })
        runBlocking {
            while(!completed){
                delay(100)
            }
        }
    }

    fun writeTestTextualTuple(tupleValue: String = "hello world"){
        val tuple = TusowGRPC.Tuple.newBuilder().setKey("testTuple").setValue("hello world").build()
        val writeRequest = TusowGRPC.WriteRequest.newBuilder().setTuple(tuple).setTupleSpace(testTextualTupleSpace).build()
        var completed = false
        stub.write(writeRequest, object : StreamObserver<TusowGRPC.IOResponse> {
            override fun onNext(value: TusowGRPC.IOResponse) {
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                completed = true
            }
        })
        runBlocking {
            while(!completed){
                delay(100)
            }
        }
    }

    @Before
    fun initializeTestResources(){
        startServer()
        connectStub()
        createTestTextualTupleSpace()
        writeTestTextualTuple()
        writeTestTextualTuple("hello world 1")
        writeTestTextualTuple("hello world 2")
        writeTestTextualTuple("hello world 3")
    }

    @Test
    fun testTextualTupleSpaceCreation(){
       val textualTupleSpace = createTupleSpace("textualTupleSpace", TusowGRPC.TupleSpaceType.TEXTUAL)
        var response = false
        stub.createTupleSpace(textualTupleSpace, object : StreamObserver<TusowGRPC.IOResponse> {
            override fun onNext(value: TusowGRPC.IOResponse) {
                response = value.response
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                server.shutdown()
            }
        })
        server.awaitTermination()
        assertTrue(response)
    }

    @Test
    fun testTupleSpaceValidation(){
        var response = false
        stub.validateTupleSpace(testTextualTupleSpace, object : StreamObserver<TusowGRPC.IOResponse> {
            override fun onNext(value: TusowGRPC.IOResponse) {
                response = value.response
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                server.shutdown()
            }
        })
        server.awaitTermination()
        assertTrue(response)
    }

    @Test
    fun testTextualTupleWrite(){
        val tuple = TusowGRPC.Tuple.newBuilder().setKey("test").setValue("hello world").build()
        val writeRequest = TusowGRPC.WriteRequest.newBuilder().setTuple(tuple).setTupleSpace(testTextualTupleSpace).build()
        var response = false
        stub.write(writeRequest, object : StreamObserver<TusowGRPC.IOResponse> {
            override fun onNext(value: TusowGRPC.IOResponse) {
                response = value.response
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                server.shutdown()
            }
        })
        server.awaitTermination()
        assertTrue(response)
    }

    //TODO Remove the key attribute from the Template class
    @Test
    fun testTextualTupleRead(){
        val textualTemplate = TusowGRPC.ReadRequest.Textual.newBuilder().setTemplate(TusowGRPC.Template.Textual.newBuilder().setRegex("hello world").build()).build()
        val readRequest = TusowGRPC.ReadRequest.newBuilder().setTupleSpace(testTextualTupleSpace).setTextualTemplate(textualTemplate).build()
        var tuple = ""
        stub.read(readRequest, object : StreamObserver<TusowGRPC.Tuple> {
            override fun onNext(value: TusowGRPC.Tuple) {
                tuple = value.value
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                server.shutdown()
            }

        })
        server.awaitTermination()
        assertEquals(tuple, "hello world")
    }

    @Test
    fun testTextualTupleTake(){
        val textualTemplate = TusowGRPC.TakeRequest.Textual.newBuilder().setTemplate(TusowGRPC.Template.Textual.newBuilder().setRegex("hello world").build()).build()
        val takeRequest = TusowGRPC.TakeRequest.newBuilder().setTupleSpace(testTextualTupleSpace).setTextualTemplate(textualTemplate).build()
        var firstAttempt = ""
        //var secondAttempt = "FFFFFFFFFF"
        stub.take(takeRequest, object : StreamObserver<TusowGRPC.Tuple> {
            override fun onNext(value: TusowGRPC.Tuple) {
                firstAttempt = value.value
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                /*
                val textualTemplate2 = TusowGRPC.ReadRequest.Textual.newBuilder().setTemplate(TusowGRPC.Template.Textual.newBuilder().setRegex("hello world").build()).build()
                val readRequest = TusowGRPC.ReadRequest.newBuilder().setTupleSpace(testTextualTupleSpace).setTextualTemplate(textualTemplate2).build()
                stub.read(readRequest, object : StreamObserver<TusowGRPC.Tuple> {
                    override fun onNext(value: TusowGRPC.Tuple) {
                        secondAttempt = value.value
                    }

                    override fun onError(t: Throwable?) {
                        t?.printStackTrace()
                    }

                    override fun onCompleted() {
                        server.shutdown()
                    }

                })*/
                server.shutdown()
            }

        })
        server.awaitTermination()
        assertEquals(firstAttempt, "hello world")
        //assertEquals(secondAttempt, "")
    }

    @Test
    fun testWriteAll(){
        val tuple = TusowGRPC.Tuple.newBuilder().setKey("test").setValue("hello world").build()
        val tuplesList = TusowGRPC.TuplesList.newBuilder().addAllTuples(listOf(
            TusowGRPC.Tuple.newBuilder().setValue("hello world 1").build(),
            TusowGRPC.Tuple.newBuilder().setValue("hello world 2").build(),
            TusowGRPC.Tuple.newBuilder().setValue("hello world 3").build()
        ))
        val writeAllRequest = TusowGRPC.WriteAllRequest.newBuilder().setTuplesList(tuplesList).setTupleSpace(testTextualTupleSpace).build()
        var responses: Array<Boolean> = Array(0){false}
        stub.writeAll(writeAllRequest, object : StreamObserver<TusowGRPC.IOResponseList> {
            override fun onNext(value: TusowGRPC.IOResponseList) {
                responses = value.responsesList.map { it.response }.toTypedArray()
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                server.shutdown()
            }
        })
        server.awaitTermination()
        responses.forEach { assertTrue(it) }
    }

    @Test
    fun testReadAll(){
        val regexesList = TusowGRPC.TemplatesList.TextualTemplatesList.newBuilder().addAllRegexes(listOf(
            TusowGRPC.Template.Textual.newBuilder().setRegex("hello world 1").build(),
            TusowGRPC.Template.Textual.newBuilder().setRegex("hello world 2").build(),
            TusowGRPC.Template.Textual.newBuilder().setRegex("hello world 3").build()
            ))
        val readAllRequest = TusowGRPC.ReadAllRequest.newBuilder().setTextualTemplateList(regexesList).setTupleSpace(testTextualTupleSpace).build()
        var tuples: List<String> = LinkedList()
        stub.readAll(readAllRequest, object : StreamObserver<TusowGRPC.TuplesList> {
            override fun onNext(value: TusowGRPC.TuplesList) {
                tuples = value.tuplesList.map { it.value }.toList()
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                server.shutdown()
            }
        })
        server.awaitTermination()
        //tuples.forEach { assertEs }
    }

    @Test
    fun testTakeAll(){
        val regexesList = TusowGRPC.TemplatesList.TextualTemplatesList.newBuilder().addAllRegexes(listOf(
            TusowGRPC.Template.Textual.newBuilder().setRegex("hello world 1").build(),
            TusowGRPC.Template.Textual.newBuilder().setRegex("hello world 2").build(),
            TusowGRPC.Template.Textual.newBuilder().setRegex("hello world 3").build()
        ))
        val readAllRequest = TusowGRPC.ReadAllRequest.newBuilder().setTextualTemplateList(regexesList).setTupleSpace(testTextualTupleSpace).build()
        var tuples: List<String> = LinkedList()
        stub.takeAll(readAllRequest, object : StreamObserver<TusowGRPC.TuplesList> {
            override fun onNext(value: TusowGRPC.TuplesList) {
                tuples = value.tuplesList.map { it.value }.toList()
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                server.shutdown()
            }
        })
        server.awaitTermination()
    }

    @Test
    fun testReadAllStream(){
        val regexesList = TusowGRPC.TemplatesList.TextualTemplatesList.newBuilder().addAllRegexes(listOf(
            TusowGRPC.Template.Textual.newBuilder().setRegex("hello world 1").build(),
            TusowGRPC.Template.Textual.newBuilder().setRegex("hello world 2").build(),
            TusowGRPC.Template.Textual.newBuilder().setRegex("hello world 3").build()
        ))
        val readAllRequest = TusowGRPC.ReadAllRequest.newBuilder().setTextualTemplateList(regexesList).setTupleSpace(testTextualTupleSpace).build()
        var tuples = LinkedList<String>()
        stub.readAllAsStream(readAllRequest, object : StreamObserver<TusowGRPC.Tuple> {
            override fun onNext(value: TusowGRPC.Tuple) {
                tuples.add(value.value)
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onCompleted() {
                server.shutdown()
            }
        })
        server.awaitTermination()
    }

    companion object {
        private const val PORT = 8000
        private const val IP = "localhost"
    }

}