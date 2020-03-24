package it.unibo.coordination.tusow

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class TusowCommand() : CliktCommand(name="tusow") {

    init {
        subcommands(
                ReadCommand(),
                TakeCommand(),
                WriteCommand(),
                GetCommand(),
                CountCommand()
        )
    }

    override fun run() = Unit
}