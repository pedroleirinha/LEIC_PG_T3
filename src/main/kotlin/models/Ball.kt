package org.example.models

import org.example.ENVIRONMENT
import org.example.runningENVIRONMENT
import org.example.views.RACKET_DEFAULT_Y_CORD
import org.example.views.RACKET_HEIGHT
import pt.isel.canvas.CYAN
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign

const val BALL_COUNT_FONTSIZE = 30
const val BALL_COUNTER_YCORD = 585
const val BALL_RADIUS = 7
const val BALL_COLOR = CYAN
const val MAX_DELTA_X = 6
const val MAX_DELTA_Y = 4
const val INITIAL_DELTA_Y = 2
const val BALL_MAX_WEIGHT = 1.5
const val BALL_MIN_WEIGHT = 0.5
const val BALL_MAX_WEIGHT_DELTA = 0.5

data class Ball(
    val x: Int = 0,
    val y: Int = 0,
    val deltaX: Int = 0,
    val deltaY: Int = 0,
    val weight: Double = 1.0,
    val stuck: Boolean = true
)

/*
* Gera uma nova bola com movimentos horizontais e com velocidades verticais diferentes.
* A nova bola está sempre a movimentar-se para cima
* */
fun generateNewBall(racket: Racket): Ball {
    val xCord = racket.x + (racket.width / 2)
    val yCord = RACKET_DEFAULT_Y_CORD - BALL_RADIUS

    val xDelta = 0
    val yDelta = INITIAL_DELTA_Y


    return Ball(x = xCord, y = yCord, deltaX = xDelta, deltaY = -yDelta)
}

fun Ball.slowVelocity() = copy(weight = max(weight - BALL_MAX_WEIGHT_DELTA, BALL_MIN_WEIGHT))
fun Ball.upVelocity() = copy(weight = min(weight + BALL_MAX_WEIGHT_DELTA, BALL_MAX_WEIGHT))

/*
* Gera uma nova bola com movimentos horizontais e com velocidades verticais diferentes.
* A nova bola está sempre a movimentar-se para cima
* */
fun generateNewBallFromPosition(xCord: Int, yCord: Int): Ball {
    val xDelta = (-MAX_DELTA_X..MAX_DELTA_X).random()
    val yDelta = (1..MAX_DELTA_Y).random()


    return Ball(x = xCord, y = yCord, deltaX = xDelta, deltaY = yDelta, stuck = false)
}


/*
* Cria uma bola fazendo uma cópia e atualizando apenas as coords
* */
fun Ball.move() =
    if (!this.stuck) copy(x = this.horizontalMovement(), y = this.verticalMovement()) else this


fun ballMovementCalc(n: Int, delta: Int, weight: Double) =
    (n + (delta * weight)).roundToInt()

fun Ball.horizontalMovement() =
    ballMovementCalc(this.x, this.deltaX, this.weight)


fun Ball.verticalMovement() =
    ballMovementCalc(this.y, this.deltaY, this.weight)

/*
* Deteta se ha colisão com a racket retorna um enumerado conforme a colisão detetada
* */
fun Ball.isCollidingWithRacket(racket: Racket): Collision {
    val horizontalCollision = (
            horizontalMovement() + BALL_RADIUS in racket.x..(racket.x + racket.width) ||
            horizontalMovement() - BALL_RADIUS in racket.x..(racket.x + racket.width)
            )
    val verticalCollision = (verticalMovement() + BALL_RADIUS) in racket.y..(racket.y + RACKET_HEIGHT)

    return when {
        horizontalCollision && verticalCollision && this.deltaY.sign == DIRECTIONS.DOWN.value -> Collision.BOTH
        horizontalCollision -> Collision.HORIZONTAL
        verticalCollision -> Collision.VERTICAL
        else -> Collision.NONE
    }
}


fun Ball.checkBricksCollision(bricks: List<Brick>): Collision {
    for (brick in bricks) {
        val res = checkBrickCollision(this, brick)
        if (res != Collision.NONE) {
            println("$res -> $this -> $brick")
            return res
        }
    }


    return Collision.NONE
}

fun Ball.isCollidingWithBrick(brick: Brick): Collision {
    val horCollision = checkBrickHorizontalCollision(ball = this, brick)
    val verCollision = checkBrickVerticalCollision(ball = this, brick)

    return when {
        horCollision != Collision.NONE && verCollision == Collision.NONE -> Collision.HORIZONTAL
        verCollision != Collision.NONE && horCollision == Collision.NONE -> Collision.VERTICAL
        horCollision != Collision.NONE && verCollision != Collision.NONE -> Collision.BOTH
        else -> Collision.NONE
    }
}

/*
* Verifica se uma bola está em colisão com a arena, tanto na horizontal e vertical
* Retorna um objeto do tipo Collision
* */
fun Ball.isCollidingWithArea() = when {
    this.horizontalMovement() - BALL_RADIUS <= 0 ||
            this.horizontalMovement() + BALL_RADIUS >= WIDTH -> Collision.HORIZONTAL

    this.verticalMovement() - BALL_RADIUS <= 0 || (this.verticalMovement() + BALL_RADIUS >= HEIGHT &&
            (runningENVIRONMENT == ENVIRONMENT.DEBUG && this.deltaY.sign != DIRECTIONS.UP.value))// && this.deltaY.sign == DIRECTIONS.UP.value
        -> Collision.VERTICAL

    else -> Collision.NONE
}

/*
* Após a deteção de colisão com a raquete e após verificar a mudança de deltaX necessária,
* verifica se a mudança excede o máximo deltaX possível */
fun Ball.adjustDirectionAfterColliding(newDeltaX: Int) = when {
    this.deltaX + newDeltaX > MAX_DELTA_X -> MAX_DELTA_X
    this.deltaX + newDeltaX < -MAX_DELTA_X -> -MAX_DELTA_X
    else -> this.deltaX + newDeltaX
}

/*
* Verifica se uma determinada bola saiu da arena pela parte de baixo do jogo.
* */
fun Ball.isOutOfBounds() = this.y >= HEIGHT && this.deltaY.sign == DIRECTIONS.DOWN.value

/*
* Atualiza o deltaX e deltaY da bola dependendo da colisão detetada com a Area de jogo*/
fun updateBallAfterCollisionArea(ball: Ball, areaCollision: Collision): Ball {
    val newDeltaX = if (areaCollision == Collision.HORIZONTAL) -ball.deltaX else ball.deltaX
    val newDeltaY = if (areaCollision == Collision.VERTICAL) -ball.deltaY else ball.deltaY

    return ball.copy(deltaX = newDeltaX, deltaY = newDeltaY)
}

/*
* Atualiza o deltaX e deltaY da bola dependendo da colisão detetada com a Area de jogo*/
fun updateBallAfterCollisionBrick(ball: Ball, brickCollision: Collision): Ball {
    return when {
        brickCollision == Collision.BOTH -> ball.copy(deltaX = -ball.deltaX, deltaY = -ball.deltaY)
        brickCollision == Collision.HORIZONTAL -> ball.copy(deltaX = -ball.deltaX)
        brickCollision == Collision.VERTICAL -> ball.copy(deltaY = -ball.deltaY)
        else -> ball
    }
}


/*
* Verifica a colisão com a Raquete, ajusta o novo DeltaX após a colisão e atualiza
* */
fun updateBallAfterCollisionRacket(ball: Ball, racket: Racket): Ball {
    val newDeltaX = checkRacketCollisionPosition(ball, racket)
    val newDeltaY = -ball.deltaY

    val newBallDeltaX = ball.adjustDirectionAfterColliding(newDeltaX)

    return ball.copy(deltaX = newBallDeltaX, deltaY = newDeltaY, stuck = racket.sticky)
}

/*
* Caso tenha sido detetada uma colisão com a raquete ou area de jogo, o movimento da bola é ajustado de acordo com o tipo de colisão.
* */
fun updateBallMovementAfterCollision(
    ball: Ball, racket: Racket, racketCollision: Collision, areaCollision: Collision,
    brickCollision: Collision
) = when {
    racketCollision == Collision.BOTH -> updateBallAfterCollisionRacket(ball, racket)
    areaCollision != Collision.NONE -> updateBallAfterCollisionArea(ball, areaCollision)
    brickCollision != Collision.NONE -> updateBallAfterCollisionBrick(ball, brickCollision)
    else -> ball
}

/*
* Move a bola atualizando a sua posição no jogo. A nova posição é calculada somando a posição antiga com os deltas.
* */
fun updateBallsMovement(balls: List<Ball>) = balls.map { it.move() }

