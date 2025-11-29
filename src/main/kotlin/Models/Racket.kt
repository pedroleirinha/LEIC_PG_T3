package org.example.Models


const val RACKET_CENTRAL_ZONE = 10
const val RACKET_EDGE_ZONE = 10
const val RACKET_MIDDLE_ZONE = 15
const val RACKET_EDGE_ZONE_DELTA_CHANGE = 3
const val RACKET_MIDDLE_EDGE_ZONE_DELTA_CHANGE = 1
const val RACKET_WIDTH = RACKET_CENTRAL_ZONE + RACKET_MIDDLE_ZONE * 2 + RACKET_EDGE_ZONE * 2
const val RACKET_HEIGHT = 10
const val RACKET_TOP_LAYER_HEIGHT = 5

const val RACKET_MIDDLE_EDGE_COLOR = 0xF59827
const val RACKET_DEFAULT_Y_CORD = 540
const val RACKET_STARTING_POS_X = (WIDTH / 2) - (RACKET_WIDTH / 2)
const val RACKET_X_CORD = RACKET_STARTING_POS_X

data class Racket(
    val x: Int = RACKET_X_CORD,
    val y: Int = RACKET_DEFAULT_Y_CORD,
    val sticky: Boolean = false
)

/*
* Cria uma raquete nas novas coordenadas
*/
fun Racket.newPaddle(xCord: Int, yCord: Int = RACKET_DEFAULT_Y_CORD): Racket {
    val racketXCordCorrected = when {
        xCord + RACKET_WIDTH > WIDTH -> WIDTH - RACKET_WIDTH
        xCord <= 0 -> 0
        else -> xCord
    }
    return copy(x = racketXCordCorrected, y = yCord)
}

/*
* Cria uma raquete após esta ser mexida, atualizando a sua coord X.
*/
fun Racket.moveTo(to: Int) = this.newPaddle(xCord = to - RACKET_WIDTH / 2)

//Checks where in the racket the collision happens to determine the delta change
fun checkRacketCollisionPosition(ball: Ball, racket: Racket) = when {
    ball.x <= racket.x + RACKET_EDGE_ZONE -> -RACKET_EDGE_ZONE_DELTA_CHANGE
    ball.x >= (racket.x + RACKET_WIDTH) - RACKET_EDGE_ZONE -> RACKET_EDGE_ZONE_DELTA_CHANGE

    ball.x <= racket.x + (RACKET_MIDDLE_ZONE + RACKET_EDGE_ZONE) -> -RACKET_MIDDLE_EDGE_ZONE_DELTA_CHANGE
    ball.x >= (racket.x + RACKET_WIDTH) - (RACKET_MIDDLE_ZONE + RACKET_EDGE_ZONE) -> RACKET_MIDDLE_EDGE_ZONE_DELTA_CHANGE

    else -> 0
}

fun Racket.toggleStickiness() = this.copy(sticky = !this.sticky)



