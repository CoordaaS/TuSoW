package it.unibo.coordination.linda.text.remote

class RemoteException(val method: String, val resource: String, val statusCode: Int)
    : Exception("Unexpected status code in performing `$method $resource`: $statusCode") {
}