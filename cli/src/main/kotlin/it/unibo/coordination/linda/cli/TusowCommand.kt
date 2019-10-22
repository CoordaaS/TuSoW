package it.unibo.coordination.linda.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class TusowCommand() : CliktCommand(name="tusow") {

    init {
        subcommands(
                ReadCommand(),
                TakeCommand(),
                WriteCommand()
        )
    }

    override fun run() = Unit
}

fun main(args: Array<String>) {
    try {
        TusowCommand().main(args)
        System.exit(0)
    } catch (e: Exception) {
        e.printStackTrace()
        System.exit(1)
    }
}