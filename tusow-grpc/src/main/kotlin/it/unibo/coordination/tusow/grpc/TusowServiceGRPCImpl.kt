package it.unibo.coordination.tusow.grpc

import TusowGRPC.*
import TusowServiceGrpc
import io.grpc.stub.StreamObserver
import it.unibo.coordaas.tusow.grpc.logic.LogicGRPCHandler
import it.unibo.coordaas.tusow.grpc.text.TextualGRPCHandler
import it.unibo.coordination.linda.logic.LogicSpace

class TusowServiceGRPCImpl : TusowServiceGrpc.TusowServiceImplBase() {
    private val textualGRPCHandler = TextualGRPCHandler()
    private val logicGRPCHandler = LogicGRPCHandler()

    override fun validateTupleSpace(
        request: TupleSpaceID,
        responseObserver: StreamObserver<IOResponse>
    ) {
        when (request.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.validateTupleSpace(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.validateTupleSpace(request, responseObserver)
            }
        }
    }

    override fun createTupleSpace(
        request: TupleSpaceID,
        responseObserver: StreamObserver<IOResponse>
    ) {
        when (request.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.createTupleSpace(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.createTupleSpace(request, responseObserver)
            }
        }
    }

    override fun write(request: WriteRequest, responseObserver: StreamObserver<IOResponse>) {
        val tupleSpace = request.tupleSpaceID
        when (tupleSpace.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.write(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.write(request, responseObserver)
            }
        }
    }

    override fun read(request: ReadOrTakeRequest, responseObserver: StreamObserver<Tuple>) {
        val tupleSpace = request.tupleSpaceID
        when (tupleSpace.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.read(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.read(request, responseObserver)
            }
        }
    }

    override fun take(request: ReadOrTakeRequest, responseObserver: StreamObserver<Tuple>) {
        val tupleSpace = request.tupleSpaceID
        when (tupleSpace.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.take(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.take(request, responseObserver)
            }
        }
    }

    override fun writeAll(
        request: WriteAllRequest,
        responseObserver: StreamObserver<IOResponseList>
    ) {
        val tupleSpace = request.tupleSpaceID
        when (tupleSpace.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.writeAll(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.writeAll(request, responseObserver)
            }
        }
    }

    override fun readAll(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TuplesList>
    ) {
        val tupleSpace = request.tupleSpaceID
        when (tupleSpace.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.readAll(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.readAll(request, responseObserver)
            }
        }
    }

    override fun takeAll(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TuplesList>
    ) {
        val tupleSpace = request.tupleSpaceID
        when (tupleSpace.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.takeAll(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.takeAll(request, responseObserver)
            }
        }
    }

    override fun writeAllAsStream(
        request: WriteAllRequest,
        responseObserver: StreamObserver<IOResponse>
    ) {
        val tupleSpace = request.tupleSpaceID
        when (tupleSpace.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.writeAllAsStream(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.writeAllAsStream(request, responseObserver)
            }
        }
    }

    override fun readAllAsStream(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<Tuple>
    ) {
        val tupleSpace = request.tupleSpaceID
        when (tupleSpace.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.readAllAsStream(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.readAllAsStream(request, responseObserver)
            }
        }
    }

    override fun takeAllAsStream(
        request: ReadOrTakeAllRequest,
        responseObserver: StreamObserver<Tuple>
    ) {
        val tupleSpace = request.tupleSpaceID
        when (tupleSpace.type) {
            TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.takeAllAsStream(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.takeAllAsStream(request, responseObserver)
            }
        }
    }
}