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
const val KEY_G_CODE = 71
const val KEY_X_CODE = 88
const val KEY_E_CODE = 69
const val KEY_C_CODE = 67
const val LIVES_COUNT = 5
const val INITIAL_LEVEL = 1

enum class Collision {
    HORIZONTAL,
    VERTICAL,
    BOTH,
    NONE
}

val gameLevels = listOf(
    thirdBricksLayout,
    firstBricksLayout,
    secondBricksLayout,
)

enum class DIRECTIONS(val value: Int) {
    DOWN(value = 1),
    UP(value = -1),
    RIGHT(value = 1),
    LEFT(value = -1),
}

data class Area(val width: Int = WIDTH, val height: Int = HEIGHT)
data class Game(
    val area: Area = Area(),
    val balls: List<Ball> = listOf(generateNewBall(Racket())),
    val racket: Racket = Racket(),
    val bricks: List<Brick> = createInitialBricksLayout(layout = gameLevels.first()),
    val points: Int = 0,
    val lives: Int = LIVES_COUNT,
    val giftsOnScreen: List<Gift> = listOf(),
    val activeGifts: List<Gift> = listOf(),
    val level: Int = INITIAL_LEVEL,
    val showGiftsOnBricks: Boolean = false
)

val arena = Canvas(WIDTH, HEIGHT, BACKGROUND_COLOR)

fun Game.newLevel() = copy(
    racket = Racket(),
    bricks = createInitialBricksLayout(layout = gameLevels[this.level]),
    balls = listOf(generateNewBall(Racket())),
    level = this.level + 1,
    activeGifts = emptyList(),
    giftsOnScreen = emptyList()
)

fun Game.loseLife() = copy(lives = lives - 1)
fun Game.newBall() = copy(balls = listOf(generateNewBall(this.racket)))

fun unstuckBalls(game: Game): Game {
    val stuckBallsCount = game.balls.filter { it.stuck }.size

    return game.copy(
        balls = game.balls.map {
            it.copy(stuck = false)
        },
        activeGifts = game.activeGifts.map { gift ->
            if (gift.type.isGlue())
                gift.copy(useCount = gift.useCount - stuckBallsCount)
            else gift
        }
    )
}

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
    Ball(x = it * LIVES_X_SPACE.toDouble(), y = LIVES_Y_POSITION.toDouble(), stuck = true)
}


fun Game.progressGame(): Game {
    val newGameAfterBricks = handleGameBricks(game = this)
    val newGameAfterGifts = handleGifts(game = newGameAfterBricks)
    val newGameAfterBalls = handleBalls(game = newGameAfterGifts)

    val filterBrokenBricks = newGameAfterBalls.bricks.filter { !it.isBroken() }
    val newGameAfterBrokenBricks = newGameAfterBalls.copy(bricks = filterBrokenBricks)
    val finalGame = newGameAfterBrokenBricks.handleActiveGifts()
    return finalGame

}

fun handleGameBricks(game: Game): Game {
    val bricks = addHitsToCollidedBricks(game.bricks, game.balls)
    val points = sumPoints(bricks)

    return game.copy(bricks = bricks, points = game.points + points)
}

fun handleGifts(game: Game): Game {
    val newActiveGifts: List<Gift> = game.bricks
        .filter { it.hitCounter == it.type.hits && it.gift != null }
        .map { it.gift as Gift } + game.giftsOnScreen

    val newGiftsPosition = updateGiftsIconMovement(newActiveGifts)
    val caughtGifts = newGiftsPosition.filter { it.isCollidingWithRacket(game.racket) }
    val finalGifts = manageGlueGift(caughtGifts, game.activeGifts)
    val uncaughtGifts = clearGiftsCaught(gifts = newGiftsPosition, game.racket)

    return game.copy(activeGifts = finalGifts, giftsOnScreen = uncaughtGifts)
}

fun handleBalls(game: Game): Game {
    val updatedBalls = handleGameBallsBehaviour(balls = game.balls, racket = game.racket, bricks = game.bricks)

    return game.copy(balls = updatedBalls)
}

fun clearGiftsCaught(gifts: List<Gift>, racket: Racket) = gifts.filter { !it.isCollidingWithRacket(racket) }
fun updateGiftsIconMovement(gifts: List<Gift>) =
    gifts.map { it.copy(y = it.y + it.deltaY) }.filter { !it.isOutOfBounds() }


fun Game.handleActiveGifts(): Game {

    var gifts: List<Gift> = this.activeGifts;

    var giftedRacket: Racket = this.racket
    var giftedBalls: List<Ball> = this.balls

    gifts = gifts.map {
        if (!it.active) {
            giftedRacket = applyRacketGiftEffect(giftedRacket, it)
            giftedBalls = applyBallsGiftEffect(giftedBalls, it)

            println("action fired $it")
            it.copy(active = true)

        } else it
    }

    val cancelGiftCaught = gifts.filterBy(type = GiftType.CANCEL).isNotEmpty()
    val unfinishedGifts = gifts.filterUnfinished()
    val glueGifts = checkIfGlueGiftIsActive(unfinishedGifts)

    val finalGame = if (cancelGiftCaught) giftCancelEffects(this)
    else this.copy(
        racket = if (glueGifts) giftedRacket.stuck() else giftedRacket.unStuck(),
        balls = giftedBalls,
        activeGifts = unfinishedGifts
    )

    return finalGame
}

/*
* If there are no bricks, other than INDESTRUCTIBLE, left than the game ends
* */
fun Game.isGameOver() = this.bricks.excludingGold().isEmpty() || (this.balls.isEmpty() && this.lives == 0)

