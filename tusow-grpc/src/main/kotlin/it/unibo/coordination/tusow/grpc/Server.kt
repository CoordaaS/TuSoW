package it.unibo.coordination.tusow.grpc

import io.grpc.*
import io.grpc.Server
import java.io.File

class Server(val port: Int, val server: Server = ServerBuilder.forPort(port).addService(TusowServiceGRPCImpl()).build()) {

    fun start(){
        server.start()
        val creds: ChannelCredentials = TlsChannelCredentials.newBuilder()
            .trustManager(File("roots.pem"))
            .build()
        val channel: ManagedChannel = Grpc.newChannelBuilder("myservice.example.com:443", creds)
            .build()
    }

    companion object{
        @JvmStatic
        fun main(args: Array<String>){
            val server = Server(8000)
            server.start()
        }
    }
}