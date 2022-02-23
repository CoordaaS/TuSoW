package it.unibo.coordination.tusow.grpc

import io.grpc.Server
import io.grpc.ServerBuilder

class Server(val port: Int, val server: Server = ServerBuilder.forPort(port).addService(TusowGRPCService()).build()) {

    fun start(){
        server.start()
    }

    companion object{
        @JvmStatic
        fun main(args: Array<String>){
            val server = Server(8000)
            server.start()
        }
    }
}