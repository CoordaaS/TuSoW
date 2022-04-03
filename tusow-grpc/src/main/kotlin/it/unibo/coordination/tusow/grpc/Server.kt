package it.unibo.coordination.tusow.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import java.util.concurrent.TimeUnit

class Server(private val port: Int, val server: Server = ServerBuilder.forPort(port).addService(TusowServiceGRPCImpl()).build()) {

    fun start(){
        server.start()
    }

    fun shutdown(){
        server.shutdown()
    }

    fun awaitTermination(){
        server.awaitTermination()
    }

    fun awaitTermination(timeout: Long, unit: TimeUnit){
        server.awaitTermination(timeout, unit)
    }

    companion object{
        @JvmStatic
        fun main(args: Array<String>){
            val port = if(args.isNotEmpty()){
                Integer.parseInt(args[1])
            }else{
                8000
            }
            println("Listening on port $port")
            val server = Server(port)
            server.start()
            server.awaitTermination()
        }
    }
}