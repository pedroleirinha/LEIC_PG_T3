package org.example.models

import org.example.views.GIFT_CIRCLE_RADIUS
import org.example.views.RACKET_HEIGHT
import pt.isel.canvas.BLUE
import pt.isel.canvas.CYAN
import pt.isel.canvas.GREEN
import pt.isel.canvas.MAGENTA
import pt.isel.canvas.YELLOW

const val GIFT_GLUE_USECOUNT = 3

enum class GiftType(val letter: String, val color: Int, val useCount: Int = 0) {
    EXTENDED(letter = "E", color = GREEN),
    BALLS(letter = "B", color = YELLOW),
    SLOW(letter = "S", color = BLUE),
    FAST(letter = "F", color = CYAN),
    GLUE(letter = "G", color = MAGENTA, useCount = GIFT_GLUE_USECOUNT),
    CANCEL(letter = "C", color = ORANGE_COLOR)
}

fun GiftType.isGlue() = this == GiftType.GLUE

data class Gift(
    val x: Int = 0,
    val y: Int = 0,
    val deltaY: Int = 1,
    val type: GiftType,
    val active: Boolean = false,
    val useCount: Int = GIFT_GLUE_USECOUNT
)

fun Gift.isOutOfBounds() = this.y > HEIGHT

fun Gift.isCollidingWithRacket(racket: Racket) =
    ((this.x + GIFT_CIRCLE_RADIUS in racket.x..racket.x + racket.width) ||
        (this.x - GIFT_CIRCLE_RADIUS in racket.x..racket.x + racket.width)) &&
            (this.y + GIFT_CIRCLE_RADIUS in racket.y..racket.y + RACKET_HEIGHT)


fun generateGifsInRandomBricks(bricks: List<Brick>): List<Brick> {
    val availableGifts =
        GiftType.entries + GiftType.entries + GiftType.entries + GiftType.entries + GiftType.CANCEL + GiftType.CANCEL + GiftType.CANCEL + GiftType.CANCEL + GiftType.CANCEL
    val bricksMutableList = bricks.toMutableList()

    availableGifts.forEach {
        val randomIndex = (0 until bricks.size).random()
        val randomBrick = bricks[randomIndex]
        bricksMutableList[randomIndex] =
            randomBrick.copy(gift = Gift(x = randomBrick.x, y = randomBrick.y, deltaY = 2, type = it))
    }

    return bricksMutableList.toList()
}

fun chooseGiftAction(gift: Gift, game: Game):Game {
    val giftedRacket = when (gift.type) {
        GiftType.EXTENDED -> game.racket.setExtended()
        GiftType.GLUE -> game.racket.toggleStickiness()
        else -> game.racket
    }
    val giftedBalls = when (gift.type) {
        GiftType.BALLS -> giftDuplicateBall(game.balls)
        GiftType.FAST -> giftFastBalls(game.balls)
        GiftType.SLOW -> giftSlowBalls(game.balls)
        else -> game.balls
    }

    return if (gift.type == GiftType.CANCEL) giftCancelGifts(game) else
        game.copy(racket = giftedRacket, balls = giftedBalls)
}

fun giftDuplicateBall(balls: List<Ball>): List<Ball> {
    var newBallsList: List<Ball> = balls

    for (ball in balls) {
        newBallsList = newBallsList + generateNewBallFromPosition(xCord = ball.x, yCord = ball.y)
    }
    return newBallsList
}

fun giftCancelGifts(game: Game): Game {
    //val newBallsList: List<Ball> = game.balls.map { it.copy(weight = 1.0, stuck = false) }
    //val racket: Racket = game.racket.copy(sticky = false, extended = false, width = RACKET_INITIAL_WIDTH)

    //return game.copy(balls = newBallsList, racket = racket)

    return game.copy(activeGifts = emptyList())
}


fun giftSlowBalls(balls: List<Ball>) = balls.map { it.slowVelocity() }
fun giftFastBalls(balls: List<Ball>) = balls.map { it.upVelocity() }
fun giftExtendedRacket(racket: Racket) = racket.toggleExtendiness()