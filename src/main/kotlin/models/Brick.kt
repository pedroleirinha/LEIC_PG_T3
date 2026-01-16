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

fun List<Brick>.excludingEmpty() = this.filter { it.type != BrickType.EMPTY }

fun findClosestSide(value: Double, min: Double, max: Double) =
    if (abs(value - min) > abs(value - max)) max else min


/*
* IMPROVE ON IT
**/
fun checkBrickCollision(ball: Ball, brick: Brick): Collision {

    // ponto mais próximo dentro do retângulo
    val nearestX = ball.x.coerceIn(brick.x.toDouble(), brick.x.toDouble() + BRICK_WIDTH)
    val nearestY = ball.y.coerceIn(brick.y.toDouble(), brick.y.toDouble() + BRICK_HEIGHT)

    // diferença até ao centro da bola
    val dx = ball.x - nearestX
    val dy = ball.y - nearestY

    val nearestSideX = findClosestSide(ball.x, brick.x.toDouble(), brick.x.toDouble() + BRICK_WIDTH)
    val nearestSideY = findClosestSide(ball.y, brick.y.toDouble(), brick.y.toDouble() + BRICK_HEIGHT)

    val adjustedBallX = if(ball.deltaX.sign == DIRECTIONS.RIGHT.value) ball.x + BALL_RADIUS else ball.x - BALL_RADIUS
    val adjustedBallY = if(ball.deltaY.sign == DIRECTIONS.UP.value) ball.y - BALL_RADIUS else ball.y + BALL_RADIUS

    // diferença até ao centro da bola
    val distanceToSideX = adjustedBallX - nearestSideX
    val distanceToSideY = adjustedBallY - nearestSideY

    // se distância < raio → colisão
    if (dx * dx + dy * dy <= BALL_RADIUS * BALL_RADIUS) {

        return if (abs(distanceToSideX) < abs(distanceToSideY))
            Collision.HORIZONTAL
        else if (abs(distanceToSideY) < abs(distanceToSideX))
            Collision.VERTICAL
        else Collision.BOTH

    }
    return Collision.NONE
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
