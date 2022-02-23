package it.unibo.coordaas.tusow.grpc.text

import io.grpc.stub.StreamObserver
import it.unibo.coordination.Promise
import it.unibo.coordination.linda.text.RegexTemplate
import it.unibo.coordination.linda.text.RegularMatch
import it.unibo.coordination.linda.text.StringTuple
import it.unibo.coordination.linda.text.TextualSpace
import java.util.ArrayList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TextualGRPCHandler : TusowServiceGrpc.TusowServiceImplBase(){

    private val textualSpaces: MutableMap<String, TextualSpace> = HashMap()
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun validateTupleSpace(
        request: TusowGRPC.TupleSpace,
        responseObserver: StreamObserver<TusowGRPC.IOResponse>
    ) {
        responseObserver.onNext(
            TusowGRPC.IOResponse.newBuilder().setResponse(textualSpaces.containsKey(request.name))
                .setMessage(textualSpaces[request.name]?.name ?: "NULL").build()
        )
        responseObserver.onCompleted()
    }

    override fun createTupleSpace(
        request: TusowGRPC.TupleSpace,
        responseObserver: StreamObserver<TusowGRPC.IOResponse>
    ) {
        textualSpaces[request.name] = TextualSpace.local(request.name, executor)
        responseObserver.onNext(
            TusowGRPC.IOResponse.newBuilder().setResponse(true).setMessage(request.name).build()
        )
        responseObserver.onCompleted()
    }

    override fun write(request: TusowGRPC.WriteRequest, responseObserver: StreamObserver<TusowGRPC.IOResponse>) {
        val space = textualSpaces[request.tupleSpace.name]
        if (space == null) {
            responseObserver.onNext(
                TusowGRPC.IOResponse.newBuilder().setResponse(false)
                    .setMessage("{error: \"Invalid tuple space name\"}").build()
            )
            responseObserver.onCompleted()
        } else {
            val promise = space.write(request.tuple.value)
            promise.thenAccept {
                responseObserver.onNext(
                    TusowGRPC.IOResponse.newBuilder().setResponse(true).setMessage(it.value).build()
                )
                responseObserver.onCompleted()
            }
        }
    }

    override fun read(request: TusowGRPC.ReadOrTakeRequest, responseObserver: StreamObserver<TusowGRPC.Tuple>) {
        val space = textualSpaces[request.tupleSpace.name]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val promise = space.read(request.textualTemplate.regex)
            //TODO Does TupleSpace.read() timeout by himself or do I need to call CompletableFuture<M>.completeOnTimeout()?
            promise.completeOnTimeout(
                RegularMatch.failed(RegexTemplate.of(request.textualTemplate.regex)),
                5000,
                TimeUnit.MILLISECONDS
            )
            promise.thenAccept {
                responseObserver.onNext(TusowGRPC.Tuple.newBuilder().setValue(it.tuple.get().value).build())
                responseObserver.onCompleted()
            }
        }
    }

    override fun take(request: TusowGRPC.ReadOrTakeRequest, responseObserver: StreamObserver<TusowGRPC.Tuple>) {
        val space = textualSpaces[request.tupleSpace.name]
        if (space == null) {
            responseObserver.onNext(TusowGRPC.Tuple.newBuilder().setValue("{tuple: null}").build())
            responseObserver.onCompleted()
        } else {
            val promise = space.take(request.textualTemplate.regex)
            promise.thenAccept {
                responseObserver.onNext(TusowGRPC.Tuple.newBuilder().setValue(it.tuple.get().value).build())
                responseObserver.onCompleted()
            }
        }
    }

    override fun writeAll(
        request: TusowGRPC.WriteAllRequest,
        responseObserver: StreamObserver<TusowGRPC.IOResponseList>
    ) {
        val space = textualSpaces[request.tupleSpace.name]
        if (space == null) {
            val response = TusowGRPC.IOResponse.newBuilder().setResponse(false)
                .setMessage("{error: \"Invalid tuple space name\"}").build()
            responseObserver.onNext(TusowGRPC.IOResponseList.newBuilder().addResponses(0, response).build())
            responseObserver.onCompleted()
        } else {
            val completableFutures = ArrayList<Promise<StringTuple>>()
            request.tuplesList.tuplesList.forEach { tuple ->
                val promise = space.write(tuple.value)
                completableFutures.add(promise)
            }
            val futuresArray: Array<Promise<StringTuple>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            val ioResponseListBuilder = TusowGRPC.IOResponseList.newBuilder()
            finalFuture.thenApply {
                completableFutures.forEach {
                    val stringTuple = it.join()
                    val ioResponse =
                        TusowGRPC.IOResponse.newBuilder().setResponse(stringTuple.value.isNotEmpty())
                            .setMessage(stringTuple.value).build()
                    ioResponseListBuilder.addResponses(ioResponse)
                }
                responseObserver.onNext(ioResponseListBuilder.build())
                responseObserver.onCompleted()
            }
        }
    }

    override fun readAll(
        request: TusowGRPC.ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TusowGRPC.TuplesList>
    ) {
        val space = textualSpaces[request.tupleSpace.name]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = ArrayList<Promise<RegularMatch>>()
            request.textualTemplateList.regexesList.forEach { regex ->
                val promise = space.read(regex.regex)
                completableFutures.add(promise)
            }
            val futuresArray: Array<Promise<RegularMatch>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            val tuplesList = TusowGRPC.TuplesList.newBuilder()
            finalFuture.thenApply {
                completableFutures.forEach {
                    val regularMatch = it.join()
                    val tuple = TusowGRPC.Tuple.newBuilder().setValue(regularMatch.tuple.get().value).build()
                    tuplesList.addTuples(tuple)
                }
                responseObserver.onNext(tuplesList.build())
                responseObserver.onCompleted()
            }
        }
    }

    override fun takeAll(
        request: TusowGRPC.ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TusowGRPC.TuplesList>
    ) {
        val space = textualSpaces[request.tupleSpace.name]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = ArrayList<Promise<RegularMatch>>()
            request.textualTemplateList.regexesList.forEach { regex ->
                val promise = space.take(regex.regex)
                completableFutures.add(promise)
            }
            val futuresArray: Array<Promise<RegularMatch>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            val tuplesList = TusowGRPC.TuplesList.newBuilder()
            finalFuture.thenApply {
                completableFutures.forEach {
                    val regularMatch = it.join()
                    val tuple = TusowGRPC.Tuple.newBuilder().setValue(regularMatch.tuple.get().value).build()
                    tuplesList.addTuples(tuple)
                }
                responseObserver.onNext(tuplesList.build())
                responseObserver.onCompleted()
            }
        }
    }

    override fun writeAllAsStream(
        request: TusowGRPC.WriteAllRequest,
        responseObserver: StreamObserver<TusowGRPC.IOResponse>
    ) {
        val space = textualSpaces[request.tupleSpace.name]
        if (space == null) {
            val response = TusowGRPC.IOResponse.newBuilder().setResponse(false)
                .setMessage("{error: \"Invalid tuple space name\"}").build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } else {
            val completableFutures = ArrayList<Promise<StringTuple>>()
            request.tuplesList.tuplesList.forEach { tuple ->
                val promise = space.write(tuple.value)
                completableFutures.add(promise)
            }
            val futuresArray: Array<Promise<StringTuple>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            finalFuture.thenApply {
                completableFutures.forEach {
                    val stringTuple = it.join()
                    val ioResponse =
                        TusowGRPC.IOResponse.newBuilder().setResponse(stringTuple.value.isNotEmpty())
                            .setMessage(stringTuple.value).build()
                    responseObserver.onNext(ioResponse)
                }
                responseObserver.onCompleted()
            }
        }
    }

    override fun readAllAsStream(
        request: TusowGRPC.ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TusowGRPC.Tuple>
    ) {
        val space = textualSpaces[request.tupleSpace.name]
        if (space == null) {
            println("shouldnt happen")
            responseObserver.onCompleted()
        } else {
            val completableFutures = ArrayList<Promise<RegularMatch>>()
            request.textualTemplateList.regexesList.forEach { regex ->
                val promise = space.read(regex.regex)
                completableFutures.add(promise)
            }
            val futuresArray: Array<Promise<RegularMatch>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            finalFuture.thenApply {
                completableFutures.forEach {
                    val regularMatch = it.join()
                    val tuple = TusowGRPC.Tuple.newBuilder().setValue(regularMatch.tuple.get().value).build()
                    responseObserver.onNext(tuple)
                }
                responseObserver.onCompleted()
            }
        }
    }

    override fun takeAllAsStream(
        request: TusowGRPC.ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TusowGRPC.Tuple>
    ) {
        val space = textualSpaces[request.tupleSpace.name]
        if (space == null) {
            responseObserver.onCompleted()
        } else {
            val completableFutures = ArrayList<Promise<RegularMatch>>()
            request.textualTemplateList.regexesList.forEach { regex ->
                val promise = space.take(regex.regex)
                completableFutures.add(promise)
            }
            val futuresArray: Array<Promise<RegularMatch>> = completableFutures.toTypedArray()
            val finalFuture = CompletableFuture.allOf(*futuresArray)
            finalFuture.thenApply {
                completableFutures.forEach {
                    val regularMatch = it.join()
                    val tuple = TusowGRPC.Tuple.newBuilder().setValue(regularMatch.tuple.get().value).build()
                    responseObserver.onNext(tuple)
                }
                responseObserver.onCompleted()
            }
        }
    }
}