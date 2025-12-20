package org.example

import org.example.views.gameStart
import pt.isel.canvas.*

fun main() {
    onStart {
        println("START")
        gameStart()
    }

    onFinish {
        println("FINISH")
    }
}