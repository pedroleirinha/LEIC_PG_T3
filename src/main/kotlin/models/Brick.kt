package org.example.models

import pt.isel.canvas.BLACK
import kotlin.math.abs
import kotlin.math.sign

const val BRICK_HEIGHT = 15
const val BRICK_WIDTH = 32

const val TopMarginBricks = BRICK_HEIGHT * 3
const val SINGLE_HIT = 1
const val DOUBLE_HIT = 2
const val INDESTRUCTIBLE = -1
const val ORANGE_COLOR = 0xFFA500
const val SILVER_COLOR = 0xC0C0C0
const val GOLD_COLOR = 0xDAA520
const val BRICK_STROKE_OFFSET_ADJUSTMENT = 2

enum class BrickType(val points: Int, val hits: Int, val color: Int) {
    YELLOW(points = 9, hits = SINGLE_HIT, color = pt.isel.canvas.YELLOW),
    MAGENTA(points = 8, hits = SINGLE_HIT, color = pt.isel.canvas.MAGENTA),
    BLUE(points = 7, hits = SINGLE_HIT, color = pt.isel.canvas.BLUE),
    RED(points = 6, hits = SINGLE_HIT, color = pt.isel.canvas.RED),
    GREEN(points = 4, hits = SINGLE_HIT, color = pt.isel.canvas.GREEN),
    CYAN(points = 3, hits = SINGLE_HIT, color = pt.isel.canvas.CYAN),
    ORANGE(points = 2, hits = SINGLE_HIT, color = ORANGE_COLOR),
    WHITE(points = 1, hits = SINGLE_HIT, color = pt.isel.canvas.WHITE),
    SILVER(points = 0, hits = DOUBLE_HIT, color = SILVER_COLOR),
    GOLD(points = 0, hits = INDESTRUCTIBLE, color = GOLD_COLOR),
    EMPTY(points = 0, hits = INDESTRUCTIBLE, color = BLACK),
}

data class Brick(val x: Int, val y: Int, val type: BrickType, val hitCounter: Int = 0, val gift: Gift? = null)

//Função extensão de Brick que verifica se o tipo do tijolo é diferente dos tipos indestrutíveis
fun Brick.isBreakable() = this.type.hits != INDESTRUCTIBLE
//Função extensão de Brick que acrescenta um "hit" no tijolo após haver colisão com a bola
fun Brick.addHit() = this.copy(hitCounter = this.hitCounter + 1)
//Função extensão de Brick verifica se o tijolo já atingiu o numero de hits para se "partir". Devolve true ou false.
fun Brick.isBroken() = this.hitCounter == this.type.hits


//Função extensão da lista de tijolos que filtra automaticamente todos os tijolos que não sejam "EMPTY"
fun List<Brick>.excludingEmpty() = this.filter { it.type != BrickType.EMPTY }
//Função extensão da lista de tijolos que filtra automaticamente todos os tijolos que não sejam "GOLD" ou seja, indestrutíveis
fun List<Brick>.excludingGold() = this.filter { it.type != BrickType.GOLD }

//Após saber que ha colisão, a função retorna qual o lado do tijolo está mais proximo da bola para verificar o "ricochete"
fun findClosestSide(value: Double, min: Double, max: Double) =
    if (abs(value - min) > abs(value - max)) max else min

/*
* Função que deteta se ha colisão entre a bola e um tijolo, mas reconhece colisões nos cantos e retorna a colisão mais adequada
* dependendo do ângulo da colisão e o canto onde toca
* */
fun checkBrickCollisionWithCorners(ball: Ball, brick: Brick): Collision {

    val ballX = ball.horizontalMovement()
    val ballY = ball.verticalMovement()

    // ponto mais próximo dentro do retângulo
    val nearestX = ballX.coerceIn(brick.x.toDouble(), brick.x.toDouble() + BRICK_WIDTH)
    val nearestY = ballY.coerceIn(brick.y.toDouble(), brick.y.toDouble() + BRICK_HEIGHT)

    // diferença até ao centro da bola
    val dx = ballX - nearestX
    val dy = ballY - nearestY

    //lado mais perto do "edge" da bola
    val nearestSideX = findClosestSide(ballX, brick.x.toDouble(), brick.x.toDouble() + BRICK_WIDTH)
    val nearestSideY = findClosestSide(ballY, brick.y.toDouble(), brick.y.toDouble() + BRICK_HEIGHT)

    val adjustedBallX =
        when (ball.deltaX.sign) {
            DIRECTIONS.RIGHT.value -> ballX + BALL_RADIUS
            DIRECTIONS.LEFT.value -> ballX - BALL_RADIUS
            else -> ballX
        }
    val adjustedBallY =
        when (ball.deltaY.sign) {
            DIRECTIONS.DOWN.value -> ballY + BALL_RADIUS
            DIRECTIONS.UP.value -> ballY - BALL_RADIUS
            else -> ballY
        }

    // diferença do edge da bola até ao lado do brick mais perto
    val distanceToSideX = (adjustedBallX - nearestSideX)
    val distanceToSideY = (adjustedBallY - nearestSideY)

    // se distância < raio = colisão
    if ((dx * dx) + (dy * dy) <= (BALL_RADIUS * BALL_RADIUS) + 10) {

        if (dx == 0.0 && dy == 0.0) {
            println("dx0 e dy0 -> Corner")
        }
        return if (abs(distanceToSideX) < abs(distanceToSideY))
            Collision.HORIZONTAL
        else if (abs(distanceToSideY) < abs(distanceToSideX))
            Collision.VERTICAL
        else {
            handleCornerCollision(ball, brick, nearestSideX, nearestSideY)
        }
    }
    return Collision.NONE
}

fun handleCornerCollision(ball: Ball, brick: Brick, nearestSideX: Double, nearestSideY: Double): Collision {
    val isTopLeftCorner = nearestSideX == brick.x.toDouble() && nearestSideY == brick.y.toDouble()
    val isTopRightCorner = nearestSideX != brick.x.toDouble() && nearestSideY == brick.y.toDouble()
    val isBottomLeftCorner = nearestSideX == brick.x.toDouble() && nearestSideY != brick.y.toDouble()
    val isBottomRightCorner = nearestSideX != brick.x.toDouble() && nearestSideY != brick.y.toDouble()

    // Se não é um canto, retorna NONE
    if (!isTopLeftCorner && !isTopRightCorner &&
        !isBottomLeftCorner && !isBottomRightCorner
    ) {
        return Collision.NONE
    }

    return when {
        isTopLeftCorner -> handleTopLeftCorner(ball)
        isTopRightCorner -> handleTopRightCorner(ball)
        isBottomLeftCorner -> handleBottomLeftCorner(ball)
        isBottomRightCorner -> handleBottomRightCorner(ball)
        else -> Collision.NONE
    }
}

fun handleTopLeftCorner(ball: Ball): Collision = when (ball.deltaX.sign) {
    DIRECTIONS.RIGHT.value -> {
        when (ball.deltaY.sign) {
            DIRECTIONS.UP.value -> Collision.HORIZONTAL
            else -> Collision.BOTH
        }
    }

    DIRECTIONS.LEFT.value -> {
        when (ball.deltaY.sign) {
            DIRECTIONS.DOWN.value -> Collision.VERTICAL
            else -> Collision.NONE
        }
    }

    else -> Collision.VERTICAL
}

fun handleTopRightCorner(ball: Ball): Collision {
    return when (ball.deltaX.sign) {
        DIRECTIONS.RIGHT.value -> {
            when (ball.deltaY.sign) {
                DIRECTIONS.DOWN.value -> Collision.VERTICAL
                else -> Collision.NONE
            }
        }

        DIRECTIONS.LEFT.value -> {
            when (ball.deltaY.sign) {
                DIRECTIONS.DOWN.value -> Collision.BOTH
                else -> Collision.HORIZONTAL
            }
        }

        else -> Collision.VERTICAL
    }
}

fun handleBottomLeftCorner(ball: Ball): Collision {
    return when (ball.deltaX.sign) {
        DIRECTIONS.RIGHT.value -> {
            when (ball.deltaY.sign) {
                DIRECTIONS.UP.value -> Collision.BOTH
                else -> Collision.HORIZONTAL
            }
        }

        DIRECTIONS.LEFT.value -> {
            when (ball.deltaY.sign) {
                DIRECTIONS.UP.value -> Collision.VERTICAL
                else -> Collision.NONE
            }
        }

        else -> Collision.NONE
    }
}

fun handleBottomRightCorner(ball: Ball): Collision {
    return when (ball.deltaX.sign) {
        DIRECTIONS.LEFT.value -> {
            when (ball.deltaY.sign) {
                DIRECTIONS.UP.value -> Collision.BOTH
                else -> Collision.HORIZONTAL
            }
        }

        DIRECTIONS.RIGHT.value -> {
            when (ball.deltaY.sign) {
                DIRECTIONS.UP.value -> Collision.VERTICAL
                else -> Collision.NONE
            }
        }

        else -> Collision.NONE
    }
}

fun findNearestBrickSide(value: Int, side1: Int, side2: Int) = when {
    value < side1 -> side1
    value > side2 -> side2
    else -> value
}

/*
* Função recebe uma bola e um tijolo e verifica se há colisão usando a fórmula da distância.
* Distancia = sqrt((x2-x1)^2 + (y2-y1)^2)
* */
fun checkBrickCollision(ball: Ball, brick: Brick): Collision {
    val ballX = ball.horizontalMovement().toInt()
    val ballY = ball.verticalMovement().toInt()

    // Lado do BRICK mais perto da bola
    val nearestBrickX = findNearestBrickSide(value = ballX, side1 = brick.x, side2 = brick.x + BRICK_WIDTH)
    val nearestBrickY = findNearestBrickSide(value = ballY, side1 = brick.y, side2 = brick.y + BRICK_HEIGHT)

    // diferença até ao centro da bola
    val distanceX = ballX - nearestBrickX
    val distanceY = ballY - nearestBrickY

    if (distanceX * distanceX + distanceY * distanceY <= BALL_RADIUS * BALL_RADIUS) {

        return if (abs(distanceX) > abs(distanceY))
        //Se a houver colisão, mas se não existir movimento lateral, então é colisão vertical
            if (ball.deltaX == 0) Collision.VERTICAL else Collision.HORIZONTAL
        else
        //Se a houver colisão, mas se não existir movimento vertical, então é colisão horizontal
            if (ball.deltaY == 0) Collision.HORIZONTAL else Collision.VERTICAL
    }
    return Collision.NONE
}

/*
* Função que passa por todos os bricks e verifica se houve colisão e acrescenta um hit a esse tijolo
* */
fun addHitsToCollidedBricks(bricks: List<Brick>, balls: List<Ball>): List<Brick> {
    val newBricks = bricks.map { brick ->
        if (brick.isBreakable() && balls.any {
                checkBrickCollision(it, brick) != Collision.NONE
            }) {
            brick.addHit()
        } else {
            brick
        }

    }
    return newBricks
}
