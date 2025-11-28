package org.example

import pt.isel.canvas.*

enum class ENVIROMENT {
    DEBUG,
    PROD
}

val runningEnviroment = ENVIROMENT.DEBUG

fun main() {
    onStart {
        println("START")
        gameStart()
    }

    onFinish {
        println("FINISH")
    }
}