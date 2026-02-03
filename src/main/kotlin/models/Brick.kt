package org.example.models

import pt.isel.canvas.BLACK
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
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

fun List<Brick>.excludingEmpty() = this.filter { it.type != BrickType.EMPTY }
fun List<Brick>.excludingGold() = this.filter { it.type != BrickType.GOLD }

fun findClosestSide(value: Double, min: Double, max: Double) =
    if (abs(value - min) > abs(value - max)) max else min

fun checkBrickCollision(ball: Ball, brick: Brick): Collision {

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

fun Brick.addHit() = this.copy(hitCounter = this.hitCounter + 1)
fun Brick.isBroken() = this.hitCounter == this.type.hits

fun addHitsToCollidedBricks(bricks: List<Brick>, balls: List<Ball>): List<Brick> {
    val newBricks = bricks.map { brick ->
        if (balls.any {
                checkBrickCollision(it, brick) != Collision.NONE
            })
            brick.addHit()
        else
            brick

    }
    return newBricks
}
