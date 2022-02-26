package it.unibo.coordination.tusow.grpc

import TusowGRPC
import TusowServiceGrpc
import io.grpc.stub.StreamObserver
import it.unibo.coordaas.tusow.grpc.logic.LogicGRPCHandler
import it.unibo.coordaas.tusow.grpc.text.TextualGRPCHandler
import it.unibo.coordination.linda.logic.LogicSpace

class TusowServiceGRPCImpl : TusowServiceGrpc.TusowServiceImplBase() {
    private val textualGRPCHandler = TextualGRPCHandler()
    private val logicGRPCHandler = LogicGRPCHandler()

    override fun validateTupleSpace(
        request: TusowGRPC.TupleSpace,
        responseObserver: StreamObserver<TusowGRPC.IOResponse>
    ) {
        when (request.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.validateTupleSpace(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.validateTupleSpace(request, responseObserver)
            }
        }
    }

    override fun createTupleSpace(
        request: TusowGRPC.TupleSpace,
        responseObserver: StreamObserver<TusowGRPC.IOResponse>
    ) {
        when (request.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.createTupleSpace(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.createTupleSpace(request, responseObserver)
            }
        }
    }

    override fun write(request: TusowGRPC.WriteRequest, responseObserver: StreamObserver<TusowGRPC.IOResponse>) {
        val tupleSpace = request.tupleSpace
        when (tupleSpace.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.write(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.write(request, responseObserver)
            }
        }
    }

    override fun read(request: TusowGRPC.ReadOrTakeRequest, responseObserver: StreamObserver<TusowGRPC.Tuple>) {
        val tupleSpace = request.tupleSpace
        when (tupleSpace.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.read(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.read(request, responseObserver)
            }
        }
    }

    override fun take(request: TusowGRPC.ReadOrTakeRequest, responseObserver: StreamObserver<TusowGRPC.Tuple>) {
        val tupleSpace = request.tupleSpace
        when (tupleSpace.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.take(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.take(request, responseObserver)
            }
        }
    }

    override fun writeAll(
        request: TusowGRPC.WriteAllRequest,
        responseObserver: StreamObserver<TusowGRPC.IOResponseList>
    ) {
        val tupleSpace = request.tupleSpace
        when (tupleSpace.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.writeAll(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.writeAll(request, responseObserver)
            }
        }
    }

    override fun readAll(
        request: TusowGRPC.ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TusowGRPC.TuplesList>
    ) {
        val tupleSpace = request.tupleSpace
        when (tupleSpace.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.readAll(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.readAll(request, responseObserver)
            }
        }
    }

    override fun takeAll(
        request: TusowGRPC.ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TusowGRPC.TuplesList>
    ) {
        val tupleSpace = request.tupleSpace
        when (tupleSpace.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.takeAll(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.takeAll(request, responseObserver)
            }
        }
    }

    override fun writeAllAsStream(
        request: TusowGRPC.WriteAllRequest,
        responseObserver: StreamObserver<TusowGRPC.IOResponse>
    ) {
        val tupleSpace = request.tupleSpace
        when (tupleSpace.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.writeAllAsStream(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.writeAllAsStream(request, responseObserver)
            }
        }
    }

    override fun readAllAsStream(
        request: TusowGRPC.ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TusowGRPC.Tuple>
    ) {
        val tupleSpace = request.tupleSpace
        when (tupleSpace.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.readAllAsStream(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.readAllAsStream(request, responseObserver)
            }
        }
    }

    override fun takeAllAsStream(
        request: TusowGRPC.ReadOrTakeAllRequest,
        responseObserver: StreamObserver<TusowGRPC.Tuple>
    ) {
        val tupleSpace = request.tupleSpace
        when (tupleSpace.type) {
            TusowGRPC.TupleSpaceType.TEXTUAL -> {
                textualGRPCHandler.takeAllAsStream(request, responseObserver)
            }
            else -> {
                logicGRPCHandler.takeAllAsStream(request, responseObserver)
            }
        }
    }
}