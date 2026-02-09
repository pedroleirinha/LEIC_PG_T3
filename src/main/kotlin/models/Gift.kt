package org.example.models

import org.example.views.GIFT_CIRCLE_RADIUS
import org.example.views.RACKET_HEIGHT
import org.example.views.RACKET_INITIAL_WIDTH
import pt.isel.canvas.BLUE
import pt.isel.canvas.CYAN
import pt.isel.canvas.GREEN
import pt.isel.canvas.MAGENTA
import pt.isel.canvas.YELLOW

const val GIFT_USECOUNT = 1
const val GIFT_GLUE_USECOUNT = 3
const val GIFT_DELTA_Y = 2

enum class GiftType(val letter: String, val color: Int, val useCount: Int = 0) {
    EXTENDED(letter = "E", color = GREEN, useCount = GIFT_USECOUNT),
    BALLS(letter = "B", color = YELLOW, useCount = GIFT_USECOUNT),
    SLOW(letter = "S", color = BLUE, useCount = GIFT_USECOUNT),
    FAST(letter = "F", color = CYAN, useCount = GIFT_USECOUNT),
    GLUE(letter = "G", color = MAGENTA, useCount = GIFT_GLUE_USECOUNT),
    CANCEL(letter = "C", color = ORANGE_COLOR, useCount = GIFT_USECOUNT)
}

fun GiftType.isGlue() = this == GiftType.GLUE

data class Gift(
    val x: Int = 0,
    val y: Int = 0,
    val deltaY: Int = 1,
    val type: GiftType,
    val active: Boolean = false,
    val useCount: Int = type.useCount
)

fun Gift.isOutOfBounds() = this.y > HEIGHT

fun Gift.isCollidingWithRacket(racket: Racket) =
    ((this.x + GIFT_CIRCLE_RADIUS in racket.x..racket.x + racket.width) ||
            (this.x - GIFT_CIRCLE_RADIUS in racket.x..racket.x + racket.width)) &&
            (this.y + GIFT_CIRCLE_RADIUS in racket.y..racket.y + RACKET_HEIGHT)

fun List<Gift>.filterBy(type: GiftType) = this.filter { it.type == type }
fun List<Gift>.filterUnfinished() = this.filter { it.useCount != 0 }

fun generateGifsInRandomBricks(bricks: List<Brick>): List<Brick> {
    val availableGifts =
        GiftType.entries + GiftType.entries + GiftType.entries + GiftType.entries +
                GiftType.CANCEL + GiftType.CANCEL + GiftType.CANCEL + GiftType.CANCEL + GiftType.CANCEL

    val bricksMutableList = bricks.toMutableList()

    availableGifts.forEach {
        val randomIndex = getBreakableUnusedBrick(bricks)
        val randomBrick = bricks[randomIndex]
        bricksMutableList[randomIndex] =
            randomBrick.copy(gift = Gift(x = randomBrick.x, y = randomBrick.y, deltaY = GIFT_DELTA_Y, type = it))
    }

    return bricksMutableList.toList()
}


fun getBreakableUnusedBrick(bricks: List<Brick>): Int {
    var index: Int
    do {
        index = (0 until bricks.size).random()
        val brick = bricks[index]
    } while (brick.type.hits == INDESTRUCTIBLE)

    return index
}

fun applyRacketGiftEffect(racket: Racket, gift: Gift): Racket =
    when (gift.type) {
        GiftType.EXTENDED -> racket.setExtended()
        GiftType.GLUE -> racket.toggleStickiness()
        else -> racket
    }


fun applyBallsGiftEffect(balls: List<Ball>, gift: Gift): List<Ball> =
    when (gift.type) {
        GiftType.BALLS -> giftDuplicateBall(balls)
        GiftType.FAST -> giftFastBalls(balls)
        GiftType.SLOW -> giftSlowBalls(balls)
        else -> balls
    }

fun giftDuplicateBall(balls: List<Ball>): List<Ball> {
    var newBallsList: List<Ball> = balls

    for (ball in balls) {
        newBallsList = newBallsList + generateNewBallFromPosition(xCord = ball.x, yCord = ball.y)
    }
    return newBallsList
}

fun giftCancelEffects(game: Game): Game {
    val newBallsList: List<Ball> = game.balls.map { it.copy(color = BALL_COLOR, radius = BALL_RADIUS, mass = BALL_INITIAL_MASS, stuck = false) }
    val racket: Racket = game.racket.copy(sticky = false, extended = false, width = RACKET_INITIAL_WIDTH)

    return game.copy(balls = newBallsList, racket = racket, activeGifts = emptyList())
}

fun giftSlowBalls(balls: List<Ball>) = balls.map { it.slowVelocity() }
fun giftFastBalls(balls: List<Ball>) = balls.map { it.upVelocity() }
fun giftExtendedRacket(racket: Racket) = racket.toggleExtensiveness()

fun manageGlueGift(caughtGifts: List<Gift>, activeGifts: List<Gift>): List<Gift> {

    val justGlueGifts = caughtGifts.filter { it.type.isGlue() }
    val accGlueCount = justGlueGifts.fold(initial = 0) { sum, elem -> sum + elem.useCount }

    return if (justGlueGifts.isNotEmpty() && checkIfGlueGiftIsActive(activeGifts))
        updateGlueGiftCounter(
            activeGifts =
                addOtherActiveGifts(activeGifts = activeGifts, caughtGifts),
            numberToAdd = accGlueCount
        )
    else activeGifts + caughtGifts
}


fun addOtherActiveGifts(activeGifts: List<Gift>, caughtGifts: List<Gift>): List<Gift> {
    return activeGifts + caughtGifts.filter { !it.type.isGlue() }
}

fun updateGlueGiftCounter(activeGifts: List<Gift>, numberToAdd: Int): List<Gift> {
    return activeGifts.map {
        it.copy(
            useCount =
                if (it.type.isGlue())
                    it.useCount + numberToAdd
                else it.useCount
        )
    }
}

fun checkIfGlueGiftIsActive(activeGifts: List<Gift>): Boolean {
    return activeGifts.filter { it.type.isGlue() }.isNotEmpty()
}
