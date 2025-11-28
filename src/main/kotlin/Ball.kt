package org.example

import pt.isel.canvas.CYAN
import kotlin.math.sign


const val BALL_COUNT_FONTSIZE = 30
const val BALL_COUNTER_YCORD = 585
const val BALL_RADIUS = 7
const val BALL_COLOR = CYAN
const val MAX_DELTA_X = 6
const val MAX_DELTA_Y = 4

data class Ball(val x: Int = 0, val y: Int = 0, val deltaX: Int = 0, val deltaY: Int = 0)

/*
* Gera uma nova bola com movimentos horizontais e com velocidades verticais diferentes.
* A nova bola está sempre a movimentar-se para cima
* */
fun generateRandomBall(): Ball {
    val xCord = WIDTH / 2
    val yCord = HEIGHT - 30

    val xDelta = 0
    val yDelta = MAX_DELTA_Y


    return Ball(x = xCord, y = yCord, deltaX = xDelta, deltaY = -yDelta)
}

/*
* Deteta se ha colisão com a racket retorna um enumerado conforme a colisão detetada
* */
fun Ball.isCollidingWithRacket(racket: Racket): Collision {
    val horizontalCollision = (
            this.x + BALL_RADIUS in racket.x..(racket.x + RACKET_WIDTH) ||
                    this.x - BALL_RADIUS in racket.x..(racket.x + RACKET_WIDTH)
            )
    val verticalCollision = (this.y + BALL_RADIUS) in racket.y..(racket.y + RACKET_HEIGHT)

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
    val horCollision = checkBrickHorizontalCollision(this, brick)
    val verCollision = checkBrickVerticalCollision(this, brick)

    return when {
        horCollision != Collision.NONE && verCollision == Collision.NONE -> Collision.HORIZONTAL
        verCollision != Collision.NONE && horCollision == Collision.NONE -> Collision.VERTICAL
        horCollision != Collision.NONE && verCollision != Collision.NONE -> Collision.BOTH
        else -> Collision.NONE
    }
}

/*
* Cria uma bola fazendo uma cópia e atualizando apenas as coords
* */
fun Ball.move() = copy(x = this.x + this.deltaX, y = this.y + deltaY)

/*
* Verifica se uma bola está em colisão com a arena, tanto na horizontal e vertical
* Retorna um objeto do tipo Collision
* */
fun Ball.isCollidingWithArea() = when {
    this.x - BALL_RADIUS <= 0 || this.x + BALL_RADIUS >= WIDTH -> Collision.HORIZONTAL
    this.y - BALL_RADIUS <= 0 || this.y + BALL_RADIUS >= HEIGHT // && this.deltaY.sign == DIRECTIONS.UP.value
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

    //println("Ball DeltaX ${ball.deltaX} - NEW deltaX $newDeltaX, DELTA after adjustment $newBallDeltaX, BATEU EM ${ball.x - racket.x} ")

    return ball.copy(deltaX = newBallDeltaX, deltaY = newDeltaY)
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

