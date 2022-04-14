package it.unibo.coordaas.tusow.grpc.logic

import TusowGRPC.*
import TusowServiceGrpc
import io.grpc.stub.StreamObserver
import it.unibo.coordination.Promise
import it.unibo.coordination.linda.core.TupleSpace
import it.unibo.coordination.linda.logic.LogicMatch
import it.unibo.coordination.linda.logic.LogicSpace
import it.unibo.coordination.linda.logic.LogicTemplate
import it.unibo.coordination.linda.logic.LogicTuple
import it.unibo.tuprolog.core.Term
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LogicGRPCHandler : TusowServiceGrpc.TusowServiceImplBase(){

    private val logicSpaces: MutableMap<String, LogicSpaceAlias> = HashMap()
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    private fun setPromiseTimeout(promise: Promise<LogicMatch>, request: ReadOrTakeRequest){
        promise.completeOnTimeout(
            LogicMatch.failed(LogicTemplate.of(request.logicTemplate.query)),
            5000,
            TimeUnit.MILLISECONDS
        )
    }

    private fun handleReadOrTakePromise(promise: Promise<LogicMatch>, request: ReadOrTakeRequest, responseObserver: StreamObserver<Tuple>){
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

    private fun handleReadOrTakeAllPromise(completableFutures: LinkedList<Promise<LogicMatch>>, responseObserver: StreamObserver<TuplesList>){
        val futuresArray: Array<Promise<LogicMatch>> = completableFutures.toTypedArray()
        val finalFuture = CompletableFuture.allOf(*futuresArray)
        val tuplesList = TuplesList.newBuilder()
        finalFuture.thenApply {
            completableFutures.forEach {
                val logicMatch = it.join()
                logicMatch.tuple.ifPresent { logicTuple ->
                    val tuple = Tuple.newBuilder().setValue(logicTuple.value.toString()).build()
                    tuplesList.addTuples(tuple)
                }
            }
            responseObserver.onNext(tuplesList.build())
            responseObserver.onCompleted()
        }
    }

    private fun getWritePromisesList(request: WriteAllRequest, space: LogicSpaceAlias) : LinkedList<Promise<LogicTuple>>{
        val completableFutures = LinkedList<Promise<LogicTuple>>()
        request.tuplesList.tuplesList.forEach { tuple ->
            val promise = space.write(tuple.value)
            completableFutures.add(promise)
        }
        return completableFutures
    }

    private fun handleReadOrTakeAllAsStreamPromise(completableFutures: LinkedList<Promise<LogicMatch>>, responseObserver: StreamObserver<Tuple>){
        val futuresArray: Array<Promise<LogicMatch>> = completableFutures.toTypedArray()
        val finalFuture = CompletableFuture.allOf(*futuresArray)
        finalFuture.thenApply {
            completableFutures.forEach {
                val logicMatch = it.join()
                logicMatch.tuple.ifPresent { logicTuple ->
                    val tuple = Tuple.newBuilder().setValue(logicTuple.value.toString()).build()
                    responseObserver.onNext(tuple)
                }
            }
            responseObserver.onCompleted()
        }
    }

    override fun validateTupleSpace(
        request: TupleSpaceID,
        responseObserver: StreamObserver<IOResponse>
    ) {
        responseObserver.onNext(
            IOResponse.newBuilder().setResponse(logicSpaces.containsKey(request.id))
                .setMessage(logicSpaces[request.id]?.name ?: "NULL").build()
        )
        responseObserver.onCompleted()
    }

    override fun createTupleSpace(
        request: TupleSpaceID,
        responseObserver: StreamObserver<IOResponse>
    ) {
        logicSpaces[request.id] = LogicSpace.local(request.id, executor)
        responseObserver.onNext(
            IOResponse.newBuilder().setResponse(true).setMessage(request.id).build()
        )
        responseObserver.onCompleted()
    }

    override fun write(request: WriteRequest, responseObserver: StreamObserver<IOResponse>) {
        val space = logicSpaces[request.tupleSpaceID.id]
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
                    IOResponse.newBuilder().setResponse(true).setMessage(it.value.toString()).build()
                )
                responseObserver.onCompleted()
            }
        }
    }

    override fun read(request: ReadOrTakeRequest, responseObserver: StreamObserver<Tuple>) {
        val space = logicSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val promise = space.read(request.logicTemplate.query)
            handleReadOrTakePromise(promise, request, responseObserver)
        }
    }

    override fun take(request: ReadOrTakeRequest, responseObserver: StreamObserver<Tuple>) {
        val space = logicSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onNext(Tuple.newBuilder().setValue("{tuple: null}").build())
            responseObserver.onCompleted()
        } else {
            val promise = space.take(request.logicTemplate.query)
            handleReadOrTakePromise(promise, request, responseObserver)
        }
    }

    override fun writeAll(
        request: WriteAllRequest,
        responseObserver: StreamObserver<IOResponseList>
    ) {
        val space = logicSpaces[request.tupleSpaceID.id]
        if (space == null) {
            val response = IOResponse.newBuilder().setResponse(false)
                .setMessage("{error: \"Invalid tuple space name\"}").build()
            responseObserver.onNext(IOResponseList.newBuilder().addResponses(0, response).build())
            responseObserver.onCompleted()
        } else {
            val completableFutures = getWritePromisesList(request, space)
            val futuresArray: Array<Promise<LogicTuple>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            val ioResponseListBuilder = IOResponseList.newBuilder()
            finalFuture.thenApply {
                completableFutures.forEach {
                    val logicTuple = it.join()
                    val ioResponse =
                        IOResponse.newBuilder().setResponse(logicTuple.value.toString().isNotBlank())
                            .setMessage(logicTuple.value.toString()).build()
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
        val space = logicSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = LinkedList<Promise<LogicMatch>>()
            request.logicTemplateList.queriesList.forEach { query ->
                val promise = space.read(query.query)
                completableFutures.add(promise)
            }
            handleReadOrTakeAllPromise(completableFutures, responseObserver)
        }
    }

    override fun takeAll(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TuplesList>
    ) {
        val space = logicSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = LinkedList<Promise<LogicMatch>>()
            request.logicTemplateList.queriesList.forEach { query ->
                val promise = space.take(query.query)
                completableFutures.add(promise)
            }
            handleReadOrTakeAllPromise(completableFutures, responseObserver)
        }
    }

    override fun writeAllAsStream(
        request: WriteAllRequest,
        responseObserver: StreamObserver<IOResponse>
    ) {
        val space = logicSpaces[request.tupleSpaceID.id]
        if (space == null) {
            val response = IOResponse.newBuilder().setResponse(false)
                .setMessage("{error: \"Invalid tuple space name\"}").build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } else {
            val completableFutures = getWritePromisesList(request, space)
            val futuresArray: Array<Promise<LogicTuple>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            finalFuture.thenApply {
                completableFutures.forEach {
                    val logicTuple = it.join()
                    val ioResponse =
                        IOResponse.newBuilder().setResponse(logicTuple.value.toString().isNotBlank())
                            .setMessage(logicTuple.value.toString()).build()
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
        val space = logicSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = LinkedList<Promise<LogicMatch>>()
            request.logicTemplateList.queriesList.forEach { query ->
                val promise = space.read(query.query)
                completableFutures.add(promise)
            }
            handleReadOrTakeAllAsStreamPromise(completableFutures, responseObserver)
        }
    }

    override fun takeAllAsStream(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<Tuple>
    ) {
        val space = logicSpaces[request.tupleSpaceID.id]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = LinkedList<Promise<LogicMatch>>()
            request.logicTemplateList.queriesList.forEach { query ->
                val promise = space.take(query.query)
                completableFutures.add(promise)
            }
            handleReadOrTakeAllAsStreamPromise(completableFutures, responseObserver)
        }
    }
}

typealias LogicSpaceAlias = TupleSpace<LogicTuple, LogicTemplate, String, Term, LogicMatch>