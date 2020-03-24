package it.unibo.coordination.linda.cli

object Cli {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            TusowCommand().main(args)
            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
            System.exit(1)
        }
    }
}