package it.unibo.coordaas.tusow.grpc.text

import TusowGRPC.*
import TusowServiceGrpc
import io.grpc.stub.StreamObserver
import it.unibo.coordination.Promise
import it.unibo.coordination.linda.text.RegexTemplate
import it.unibo.coordination.linda.text.RegularMatch
import it.unibo.coordination.linda.text.StringTuple
import it.unibo.coordination.linda.text.TextualSpace
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TextualGRPCHandler : TusowServiceGrpc.TusowServiceImplBase(){

    private val textualSpaces: MutableMap<String, TextualSpace> = HashMap()
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    private fun setPromiseTimeout(promise: Promise<RegularMatch>, request: ReadOrTakeRequest){
        promise.completeOnTimeout(
            RegularMatch.failed(RegexTemplate.of(request.textualTemplate.regex)),
            5000,
            TimeUnit.MILLISECONDS
        )
    }

    private fun handleReadOrTakePromise(promise: Promise<RegularMatch>, request: ReadOrTakeRequest, responseObserver: StreamObserver<Tuple>){
        setPromiseTimeout(promise, request)
        promise.thenAccept {
            var value = ""
            it.tuple.ifPresent { logicTuple ->
                value = logicTuple.value.toString()
            }
            responseObserver.onNext(Tuple.newBuilder().setValue(value).build())
            responseObserver.onCompleted()
        }
    }

    private fun handleReadOrTakeAllPromise(completableFutures: LinkedList<Promise<RegularMatch>>, responseObserver: StreamObserver<TuplesList>){
        val futuresArray: Array<Promise<RegularMatch>> = completableFutures.toTypedArray()
        val finalFuture = CompletableFuture.allOf(*futuresArray)
        val tuplesList = TuplesList.newBuilder()
        finalFuture.thenApply {
            completableFutures.forEach {
                val regularMatch = it.join()
                val tuple = Tuple.newBuilder().setValue(regularMatch.tuple.get().value).build()
                tuplesList.addTuples(tuple)
            }
            responseObserver.onNext(tuplesList.build())
            responseObserver.onCompleted()
        }
    }

    private fun getWritePromisesList(request: WriteAllRequest, space: TextualSpace) : LinkedList<Promise<StringTuple>>{
        val completableFutures = LinkedList<Promise<StringTuple>>()
        request.tuplesList.tuplesList.forEach { tuple ->
            val promise = space.write(tuple.value)
            completableFutures.add(promise)
        }
        return completableFutures
    }

    private fun handleReadOrTakeAllAsStreamPromise(completableFutures: LinkedList<Promise<RegularMatch>>, responseObserver: StreamObserver<Tuple>){
        val futuresArray: Array<Promise<RegularMatch>> = completableFutures.toTypedArray()
        val finalFuture = CompletableFuture.allOf(*futuresArray)
        finalFuture.thenApply {
            completableFutures.forEach {
                val regularMatch = it.join()
                val tuple = Tuple.newBuilder().setValue(regularMatch.tuple.get().value).build()
                responseObserver.onNext(tuple)
            }
            responseObserver.onCompleted()
        }
    }

    override fun validateTupleSpace(
        request: TupleSpaceID,
        responseObserver: StreamObserver<IOResponse>
    ) {
        responseObserver.onNext(
            IOResponse.newBuilder().setResponse(textualSpaces.containsKey(request.id))
                .setMessage(textualSpaces[request.id]?.name ?: "NULL").build()
        )
        responseObserver.onCompleted()
    }

    override fun createTupleSpace(
        request: TupleSpaceID,
        responseObserver: StreamObserver<IOResponse>
    ) {
        textualSpaces[request.id] = TextualSpace.local(request.id, executor)
        responseObserver.onNext(
            IOResponse.newBuilder().setResponse(true).setMessage(request.id).build()
        )
        responseObserver.onCompleted()
    }

    override fun write(request: WriteRequest, responseObserver: StreamObserver<IOResponse>) {
        val space = textualSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onNext(
                IOResponse.newBuilder().setResponse(false)
                    .setMessage("{error: \"Invalid tuple space name\"}").build()
            )
            responseObserver.onCompleted()
        } else {
            val promise = space.write(request.tuple.value)
            promise.thenAccept {
                responseObserver.onNext(
                    IOResponse.newBuilder().setResponse(true).setMessage(it.value).build()
                )
                responseObserver.onCompleted()
            }
        }
    }

    override fun read(request: ReadOrTakeRequest, responseObserver: StreamObserver<Tuple>) {
        val space = textualSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val promise = space.read(request.textualTemplate.regex)
            handleReadOrTakePromise(promise, request, responseObserver)
        }
    }

    override fun take(request: ReadOrTakeRequest, responseObserver: StreamObserver<Tuple>) {
        val space = textualSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onNext(Tuple.newBuilder().setValue("{tuple: null}").build())
            responseObserver.onCompleted()
        } else {
            val promise = space.take(request.textualTemplate.regex)
            handleReadOrTakePromise(promise, request, responseObserver)
        }
    }

    override fun writeAll(
        request: WriteAllRequest,
        responseObserver: StreamObserver<IOResponseList>
    ) {
        val space = textualSpaces[request.tupleSpaceID.id]
        if (space == null) {
            val response = IOResponse.newBuilder().setResponse(false)
                .setMessage("{error: \"Invalid tuple space name\"}").build()
            responseObserver.onNext(IOResponseList.newBuilder().addResponses(0, response).build())
            responseObserver.onCompleted()
        } else {
            val completableFutures = getWritePromisesList(request, space)
            val futuresArray: Array<Promise<StringTuple>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            val ioResponseListBuilder = IOResponseList.newBuilder()
            finalFuture.thenApply {
                completableFutures.forEach {
                    val stringTuple = it.join()
                    val ioResponse =
                        IOResponse.newBuilder().setResponse(stringTuple.value.isNotEmpty())
                            .setMessage(stringTuple.value).build()
                    ioResponseListBuilder.addResponses(ioResponse)
                }
                responseObserver.onNext(ioResponseListBuilder.build())
                responseObserver.onCompleted()
            }
        }
    }

    override fun readAll(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TuplesList>
    ) {
        val space = textualSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = LinkedList<Promise<RegularMatch>>()
            request.textualTemplateList.regexesList.forEach { regex ->
                val promise = space.read(regex.regex)
                completableFutures.add(promise)
            }
            handleReadOrTakeAllPromise(completableFutures, responseObserver)
        }
    }

    override fun takeAll(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TuplesList>
    ) {
        val space = textualSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = LinkedList<Promise<RegularMatch>>()
            request.textualTemplateList.regexesList.forEach { regex ->
                val promise = space.take(regex.regex)
                completableFutures.add(promise)
            }
            handleReadOrTakeAllPromise(completableFutures, responseObserver)
        }
    }

    override fun writeAllAsStream(
        request: WriteAllRequest,
        responseObserver: StreamObserver<IOResponse>
    ) {
        val space = textualSpaces[request.tupleSpaceID.id]
        if (space == null) {
            val response = IOResponse.newBuilder().setResponse(false)
                .setMessage("{error: \"Invalid tuple space name\"}").build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } else {
            val completableFutures = getWritePromisesList(request, space)
            val futuresArray: Array<Promise<StringTuple>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            finalFuture.thenApply {
                completableFutures.forEach {
                    val stringTuple = it.join()
                    val ioResponse =
                        IOResponse.newBuilder().setResponse(stringTuple.value.isNotEmpty())
                            .setMessage(stringTuple.value).build()
                    responseObserver.onNext(ioResponse)
                }
                responseObserver.onCompleted()
            }
        }
    }

    override fun readAllAsStream(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<Tuple>
    ) {
        val space = textualSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = LinkedList<Promise<RegularMatch>>()
            request.textualTemplateList.regexesList.forEach { regex ->
                val promise = space.read(regex.regex)
                completableFutures.add(promise)
            }
            handleReadOrTakeAllAsStreamPromise(completableFutures, responseObserver)
        }
    }

    override fun takeAllAsStream(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<Tuple>
    ) {
        val space = textualSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = LinkedList<Promise<RegularMatch>>()
            request.textualTemplateList.regexesList.forEach { regex ->
                val promise = space.take(regex.regex)
                completableFutures.add(promise)
            }
            handleReadOrTakeAllAsStreamPromise(completableFutures, responseObserver)
        }
    }
}