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
    EMPTY(points = 0, hits = 0, color = BLACK),
}

data class Brick(val x: Int, val y: Int, val type: BrickType, val hitCounter: Int = 0)
data class BricksRow(val bricks: List<BrickType>)
data class BricksColumn(val rows: List<BricksRow>)

fun findNearestBrickSide(value: Int, side1: Int, side2: Int) = when {
    value < side1 -> side1
    value > side2 -> side2
    else -> value
}

fun checkBrickCollision(ball: Ball, brick: Brick): Collision {

    // Lado do BRICK mais perto da bola
    val nearestBrickX = findNearestBrickSide(value = ball.x, side1 = brick.x, side2 = brick.x + BRICK_WIDTH)
    val nearestBrickY = findNearestBrickSide(value = ball.y, side1 = brick.y, side2 = brick.y + BRICK_HEIGHT)

    // diferença até ao centro da bola
    val distanceX = ball.x - nearestBrickX
    val distanceY = ball.y - nearestBrickY

    if (distanceX * distanceX + distanceY * distanceY <= BALL_RADIUS * BALL_RADIUS) {

        return if (abs(distanceX) > abs(distanceY))
            Collision.HORIZONTAL
        else
            Collision.VERTICAL
    }
    return Collision.NONE
}

fun Brick.addHit() = this.copy(hitCounter = this.hitCounter + 1)
fun Brick.isBroken() = this.hitCounter == this.type.hits

