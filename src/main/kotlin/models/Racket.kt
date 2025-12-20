package org.example.models

import org.example.views.RACKET_DEFAULT_Y_CORD
import org.example.views.RACKET_EDGE_ZONE
import org.example.views.RACKET_EDGE_ZONE_DELTA_CHANGE
import org.example.views.RACKET_MIDDLE_EDGE_ZONE_DELTA_CHANGE
import org.example.views.RACKET_MIDDLE_ZONE
import org.example.views.RACKET_INITIAL_WIDTH
import org.example.views.RACKET_X_CORD


data class Racket(
    val x: Int = RACKET_X_CORD,
    val y: Int = RACKET_DEFAULT_Y_CORD,
    val width: Int = RACKET_INITIAL_WIDTH,
    val sticky: Boolean = false,
    val extended: Boolean = false
)

/*
* Cria uma raquete nas novas coordenadas
*/
fun Racket.newPaddle(xCord: Int, yCord: Int = RACKET_DEFAULT_Y_CORD): Racket {
    val racketXCordCorrected = when {
        xCord + this.width > WIDTH -> WIDTH - this.width
        xCord <= 0 -> 0
        else -> xCord
    }
    return copy(x = racketXCordCorrected, y = yCord)
}

/*
* Cria uma raquete após esta ser mexida, atualizando a sua coord X.
*/
fun Racket.moveTo(to: Int) = this.newPaddle(xCord = to - this.width / 2)

//Checks where in the racket the collision happens to determine the delta change
fun checkRacketCollisionPosition(ball: Ball, racket: Racket) = when {
    ball.x <= racket.x + RACKET_EDGE_ZONE -> -RACKET_EDGE_ZONE_DELTA_CHANGE
    ball.x >= (racket.x + racket.width) - RACKET_EDGE_ZONE -> RACKET_EDGE_ZONE_DELTA_CHANGE

    ball.x <= racket.x + (RACKET_MIDDLE_ZONE + RACKET_EDGE_ZONE) -> -RACKET_MIDDLE_EDGE_ZONE_DELTA_CHANGE
    ball.x >= (racket.x + racket.width) - (RACKET_MIDDLE_ZONE + RACKET_EDGE_ZONE) -> RACKET_MIDDLE_EDGE_ZONE_DELTA_CHANGE

    else -> 0
}


