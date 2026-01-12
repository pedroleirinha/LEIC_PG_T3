package org.example.models

import pt.isel.canvas.BLACK
import kotlin.math.abs

const val BRICK_HEIGHT = 15
const val BRICK_WIDTH = 32

const val TopMarginBricks = BRICK_HEIGHT * 3
const val LeftMarginBricks = BRICK_WIDTH
const val RightMarginBricks = BRICK_WIDTH
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

/*
* IMPROVE ON IT
**/
fun checkBrickCollision(ball: Ball, brick: Brick): Collision {

    // ponto mais próximo dentro do retângulo
    val nearestX = ball.horizontalMovement().coerceIn(brick.x.toDouble(), brick.x.toDouble() + BRICK_WIDTH)
    val nearestY = ball.verticalMovement().coerceIn(brick.y.toDouble(), brick.y.toDouble() + BRICK_HEIGHT)

    // diferença até ao centro da bola
    val dx = ball.horizontalMovement() - nearestX
    val dy = ball.verticalMovement() - nearestY

    // se distância < raio → colisão
    if (dx * dx + dy * dy <= BALL_RADIUS * BALL_RADIUS) {

        return if (abs(dx) > abs(dy))
            Collision.HORIZONTAL   // bateu nas laterais
        else
            Collision.VERTICAL     // bateu em cima/baixo
    }
    return Collision.NONE
}

fun Brick.addHit() = this.copy(hitCounter = this.hitCounter + 1)


fun Brick.isBroken() = this.hitCounter == this.type.hits

