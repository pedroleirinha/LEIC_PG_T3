package org.example.models

import org.example.ENVIRONMENT
import org.example.runningENVIRONMENT
import pt.isel.canvas.BLACK
import pt.isel.canvas.Canvas

const val WIDTH = BRICK_WIDTH * 13
const val HEIGHT = 600
const val BACKGROUND_COLOR = BLACK
const val LIVES_Y_POSITION = HEIGHT - 20
const val LIVES_X_SPACE = (BALL_RADIUS * 3)
const val TIME_TICK_MLS = 10
const val KEY_S_CODE = 83
const val KEY_D_CODE = 68
const val KEY_F_CODE = 70
const val KEY_X_CODE = 88
const val KEY_E_CODE = 69
const val KEY_C_CODE = 67

enum class Collision {
    HORIZONTAL,
    VERTICAL,
    BOTH,
    NONE
}

enum class DIRECTIONS(val value: Int) {
    DOWN(value = 1),
    UP(value = -1),
}

data class Area(val width: Int = WIDTH, val height: Int = HEIGHT)
data class Game(
    val area: Area = Area(),
    val balls: List<Ball> = emptyList(),
    val racket: Racket = Racket(),
    val bricks: List<Brick> = emptyList(),
    val points: Int = 0,
    val lives: Int = 5,
    val giftsOnScreen: List<Gift> = listOf(),
    val activeGifts: List<Gift> = listOf()
)

val arena = Canvas(WIDTH, HEIGHT, BACKGROUND_COLOR)

fun Game.loseLife() = copy(lives = lives - 1)
fun Game.newBall() = copy(balls = listOf(generateNewBall(this.racket)))

fun unstuckBalls(game: Game) =
    game.copy(
        balls = game.balls.map {
            if (it.stuck) {
                it.copy(stuck = false)
            } else it
        },
        activeGifts = game.activeGifts.map {
            if (it.type == GiftType.GLUE)
                it.copy(useCount = it.useCount - 1)
            else it
        }
    )

fun adjustHorizontalCordForStuckBall(game: Game, mouseX: Int): Game {
    val updatedRacket = game.racket.moveTo(to = mouseX)
    val newBalls = game.balls.map {
        if (it.stuck) {
            val relativeX = it.x - game.racket.x
            val newX = updatedRacket.x + relativeX
            it.copy(x = newX)
        } else it
    }

    return game.copy(balls = newBalls, racket = updatedRacket)
}

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

fun sumPoints(bricks: List<Brick>) =
    bricks
        .filter { it.hitCounter > 0 }
        .fold(initial = 0) { sum, brick -> sum + brick.type.points * brick.hitCounter }

/*
* A cada step do jogo, remove as bolas fora de jogo, verifica as colisões e atualiza os movimentos das bolas para serem desenhadas novamente.
* */
fun handleGameBallsBehaviour(balls: List<Ball>, racket: Racket, bricks: List<Brick>): List<Ball> {

    val filteredBalls = filterBallsOutOfBounds(balls = balls)
    val newBallsUpdated =
        (if (runningENVIRONMENT == ENVIRONMENT.DEBUG) balls else filteredBalls).map {
            checkAndUpdateBallMovementAfterCollision(
                ball = it,
                racket = racket,
                bricks = bricks
            )
        }
    val ballsMoved = updateBallsMovement(balls = newBallsUpdated)
    return ballsMoved
}

/*
* Filtra a lista de bolas de jogo removendo as que estão fora dos limites de jogo
* */
fun filterBallsOutOfBounds(balls: List<Ball>) = balls.filter { !it.isOutOfBounds() }

/*
* Verifica a colisão de cada bola e atualiza a velocidade e movimento de cada uma
* */
fun checkAndUpdateBallMovementAfterCollision(ball: Ball, racket: Racket, bricks: List<Brick>) =
    updateBallMovementAfterCollision(
        ball = ball,
        racket = racket,
        racketCollision = ball.isCollidingWithRacket(racket = racket),
        areaCollision = ball.isCollidingWithArea(),
        brickCollision = ball.checkBricksCollision(bricks)
    )

fun generateLives(lives: Int): List<Ball> = (1..lives).map {
    Ball(x = it * LIVES_X_SPACE, y = LIVES_Y_POSITION, stuck = true)
}


fun Game.progressGame(): Game {
    val newGameAfterBricks = handleGameBricks(this)
    val newGameAfterGifs = handleGifts(newGameAfterBricks)

    val filterBrokenBricks = newGameAfterGifs.bricks.filter { !it.isBroken() }

    return newGameAfterGifs.copy(bricks = filterBrokenBricks)
        .handleActiveGifts()

}

fun handleGameBricks(game: Game): Game {
    val bricks = addHitsToCollidedBricks(game.bricks, game.balls)
    val points = sumPoints(bricks)
    val updatedBalls = handleGameBallsBehaviour(balls = game.balls, racket = game.racket, bricks = game.bricks)

    return game.copy(bricks = bricks, balls = updatedBalls, points = game.points + points)
}

fun addOtherActiveGifts(activeGifts: List<Gift>, caughtGifts: List<Gift>): List<Gift> {
    return activeGifts + caughtGifts.filter { !it.type.isGlue() }
}

fun updateGlueGiftCounter(activeGifts: List<Gift>, numberToAdd: Int): List<Gift> {
    return activeGifts.map { it.copy(useCount = if (it.type.isGlue()) it.useCount + numberToAdd else it.useCount) }
}

fun checkIfGlueGiftIsActive(activeGifts: List<Gift>): Boolean {
    return activeGifts.filter { it.type.isGlue() }.isNotEmpty()
}

fun handleGifts(game: Game): Game {
    val newActiveGifts: List<Gift> = game.bricks
        .filter { it.hitCounter == it.type.hits && it.gift != null }
        .map { it.gift as Gift } + game.giftsOnScreen

    val newGiftsPosition = updateGiftsIconMovement(newActiveGifts)
    val caughtGifts = newGiftsPosition.filter { it.isCollidingWithRacket(game.racket) }

    val justGlueGifts = caughtGifts.filter { it.type.isGlue() }
    val accGlueCount = justGlueGifts.fold(initial = 0) { sum, elem -> sum + elem.useCount }

    val finalGifts: List<Gift> = if (justGlueGifts.isNotEmpty() && checkIfGlueGiftIsActive(game.activeGifts))
        updateGlueGiftCounter(
            activeGifts =
                addOtherActiveGifts(activeGifts = game.activeGifts, caughtGifts),
            numberToAdd = accGlueCount
        )
    else game.activeGifts + caughtGifts

    val uncaughtGifts = clearGiftsCaught(gifts = newGiftsPosition, game.racket)

    return game.copy(activeGifts = finalGifts, giftsOnScreen = uncaughtGifts)
}

fun clearGiftsCaught(gifts: List<Gift>, racket: Racket) = gifts.filter { !it.isCollidingWithRacket(racket) }
fun updateGiftsIconMovement(gifts: List<Gift>) =
    gifts.map { it.copy(y = it.y + it.deltaY) }.filter { !it.isOutOfBounds() }

fun Game.handleActiveGifts(): Game {
    var newUpdatedGame = this

    val gifts = this.activeGifts.map {
        if (!it.active) {
            newUpdatedGame = chooseGiftAction(it, newUpdatedGame)
            println("action fired $it")
            it.copy(active = true)
        } else it
    }

    val unfinishedGifts = gifts.filter { it.useCount != 0 }
    val glueGifts = checkIfGlueGiftIsActive(unfinishedGifts)

    return newUpdatedGame.copy(
        activeGifts = unfinishedGifts,
        racket = if (glueGifts) racket.stuck() else racket.unStuck()
    )
}

/*
* If there are not bricks, other than INDESTRUCTIBLE, left than the game ends
* */
fun Game.isGameOver() = (
        this.bricks.filter { it.type != BrickType.GOLD }.isEmpty()
        ) || (this.balls.isEmpty() && this.lives == 0)

