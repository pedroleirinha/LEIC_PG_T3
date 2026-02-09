package org.example.models

import org.example.views.RACKET_DEFAULT_Y_CORD
import org.example.views.RACKET_HEIGHT
import pt.isel.canvas.BLUE
import pt.isel.canvas.CYAN
import pt.isel.canvas.GREEN
import pt.isel.canvas.MAGENTA
import pt.isel.canvas.YELLOW
import pt.isel.canvas.playSound
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
const val BALL_INITIAL_MASS = 1.0


data class Ball(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val deltaX: Int = 0,
    val deltaY: Int = 0,
    val mass: Double = BALL_INITIAL_MASS,
    val stuck: Boolean = true,
    val radius: Int = BALL_RADIUS,
    val color: Int = BALL_COLOR,
    val flagBigBall: Boolean = false
)

/*
* Gera uma nova bola com movimentos horizontais e com velocidades verticais diferentes.
* A nova bola está sempre a movimentar-se para cima
* */
fun generateNewBall(racket: Racket): Ball {
    val xCord = racket.x + (racket.width / 2).toDouble()
    val yCord = RACKET_DEFAULT_Y_CORD - BALL_RADIUS.toDouble()

    val xDelta = 0
    val yDelta = INITIAL_DELTA_Y


    return Ball(x = xCord, y = yCord, deltaX = xDelta, deltaY = -yDelta)
}

//Aumenta a massa da bola fazendo que ande mais rápido. O max faz com que a massa não ultrapasse o 1.5
fun Ball.slowVelocity() = copy(radius= BALL_RADIUS+2 , mass = max(mass - BALL_MAX_WEIGHT_DELTA, BALL_MIN_WEIGHT), color = listOf<Int>(YELLOW, MAGENTA,CYAN, GREEN, BLUE).random(), flagBigBall = true )
//Diminui a massa da bola fazendo que ande mais devagar. O min faz com que a massa não seja inferior que 0.5
fun Ball.upVelocity() = copy(mass = min(mass + BALL_MAX_WEIGHT_DELTA, BALL_MAX_WEIGHT), flagBigBall = false)

/*
* Gera uma nova bola com movimentos horizontais e com velocidades verticais diferentes.
* A nova bola está sempre a movimentar-se para cima
* */
fun generateNewBallFromPosition(xCord: Double, yCord: Double): Ball {
    val xDelta = (-MAX_DELTA_X..MAX_DELTA_X).random()
    val yDelta = (1..MAX_DELTA_Y).random()

    return Ball(x = xCord, y = yCord, deltaX = xDelta, deltaY = yDelta, stuck = false)
}


/*
* Cria uma bola fazendo uma cópia e atualizando apenas as coords
* */
fun Ball.move() =
    if (!this.stuck) copy(x = this.horizontalMovement(), y = this.verticalMovement()) else this


//Faz o cálculo da nova coordenada com base no delta e na massa da bola
fun ballMovementCalc(n: Double, delta: Int, mass: Double) =
    (n + (delta * mass))

//Obtém o novo X da bola com base no movimento horizontal (deltaX) e a massa
fun Ball.horizontalMovement(): Double =
    ballMovementCalc(this.x, this.deltaX, this.mass)

//Obtém o novo Y da bola com base no movimento vertical (deltaY) e a massa
fun Ball.verticalMovement(): Double =
    ballMovementCalc(this.y, this.deltaY, this.mass)

/*
* Deteta se ha colisão com a racket retorna um enumerado conforme a colisão detetada
* */
fun Ball.isCollidingWithRacket(racket: Racket): Collision {
    val horizontalCollision = (
            (horizontalMovement() + radius /*BALL_RADIUS*/).roundToInt() in racket.x..(racket.x + racket.width) ||
                    (horizontalMovement() - radius /*BALL_RADIUS*/).roundToInt() in racket.x..(racket.x + racket.width)
            )
    val verticalCollision = (verticalMovement() + radius /*BALL_RADIUS*/).roundToInt() in racket.y..(racket.y + RACKET_HEIGHT)

    return when {
        horizontalCollision && verticalCollision && this.deltaY.sign == DIRECTIONS.DOWN.value -> Collision.BOTH
        horizontalCollision -> Collision.HORIZONTAL
        verticalCollision -> Collision.VERTICAL
        else -> Collision.NONE
    }
}

/*
* Permite obter uma lista mais pequena de tijolos que estão posicionados no mesmo "axis" que a bola.
* Os tijolos que não estão na mesma linha horizontal e vertical não são apanhados.
* */
fun getBricksOnTrajectory(ball: Ball, bricks: List<Brick>): List<Brick> {

    val adjustedX = ball.horizontalMovement()
    val adjustedY = ball.verticalMovement()

    val vBricks = bricks.filter {
        adjustedX - ball.radius /*BALL_RADIUS*/ in it.x.toDouble()..it.x.toDouble() + BRICK_WIDTH ||
                adjustedX + ball.radius /*BALL_RADIUS*/ in it.x.toDouble()..it.x.toDouble() + BRICK_WIDTH
    }
    val hBricks = bricks.filter {
        adjustedY - ball.radius /*BALL_RADIUS*/ in it.y.toDouble()..it.y.toDouble() + BRICK_HEIGHT ||
                adjustedY + ball.radius /*BALL_RADIUS*/ in it.y.toDouble()..it.y.toDouble() + BRICK_HEIGHT
    }


    return vBricks + hBricks
}

fun Ball.checkBricksCollision(bricks: List<Brick>): Collision {

    val newBricks = getBricksOnTrajectory(ball = this, bricks)

    for (brick in newBricks) {
        val res = checkBrickCollision(this, brick)
        if (res != Collision.NONE) {
            println("$res -> $this -> $brick")
            return res
        }
    }
    return Collision.NONE
}

/*
* Verifica se uma bola está em colisão com a arena, tanto na horizontal e vertical
* Retorna um objeto do tipo Collision
* */
fun Ball.isCollidingWithArea() = when {
    this.horizontalMovement() - radius /*BALL_RADIUS*/ <= 0 ||
            this.horizontalMovement() + radius /*BALL_RADIUS*/ >= WIDTH -> Collision.HORIZONTAL

    this.verticalMovement() - radius /*BALL_RADIUS*/ <= 0 -> Collision.VERTICAL

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
    playSound("Arkanoid SFX (8)")

    return when {
        ball.flagBigBall -> ball
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
    playSound("click")
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

/*
* Filtra a lista de bolas de jogo removendo as que estão fora dos limites de jogo
* */
fun filterBallsOutOfBounds(balls: List<Ball>) = balls.filter { !it.isOutOfBounds() }
