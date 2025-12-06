package org.example.views

import org.example.models.*
import pt.isel.canvas.ESCAPE_CODE
import pt.isel.canvas.WHITE
import pt.isel.canvas.YELLOW


fun gameStart() {
    val racket = Racket()
    var game = Game(
        racket = racket,
        bricks = createInitialBricksLayout(bricksLayout),
        balls = listOf(generateNewBall(racket))
    )

    arena.onTimeProgress(period = TIME_TICK_MLS) {
        arena.erase()

        if (!game.isGameOver()) game = game.progressGame() else drawFinishText()
        drawGame(game)
    }

    arena.onMouseMove { me ->
        game = adjustHorizontalCordForStuckBall(game, mouseX = me.x)
    }

    arena.onMouseDown { me ->
        if (me.down) {
            if (game.balls.isEmpty() && game.lives > 0) {
                game = game.newBall()
                game = game.loseLife()
            } else {
                game = unstuckBalls(game)
            }
        }
    }

    arena.onKeyPressed {
        if (it.code == ESCAPE_CODE) arena.close()
        if (it.code == KEY_X_CODE) {
            game = game.copy(racket = game.racket.toggleStickiness())
            println("sticky ${game.racket.sticky}")
        }

        if (it.code == KEY_F_CODE) {
            game = game.copy(balls = giftFastBalls(game.balls))
            println("fast balls ${game.balls.first().weight}")
        }

        if (it.code == KEY_S_CODE) {
            game = game.copy(balls = giftSlowBalls(game.balls))
            println("slow balls ${game.balls.first().weight}")
        }

        if (it.code == KEY_D_CODE) {
            game = game.copy(balls = giftDuplicateBall(game.balls))
            println("duplicate balls")
        }

        if (it.code == KEY_E_CODE) {
            game = game.copy(racket = giftExtendedRacket(game.racket))
            println("extended racket")
        }

        if (it.code == KEY_C_CODE) {
            game = giftCancelGifts(game)
            println("cancel")
        }

    }
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

fun handleGifts(game: Game):Game{
    val newActiveGifts: List<Gift> = game.bricks
        .filter { it.hitCounter == it.type.hits && it.gift != null}
        .map { it.gift as Gift } + game.giftsOnScreen

    val newGiftsPosition = updateGiftsIconMovement(newActiveGifts)
    val caughtGifts = newGiftsPosition.filter { it.isCollidingWithRacket(game.racket) }
    val uncaughtGifts = clearGiftsCaught(gifts = newGiftsPosition, game.racket)

    return game.copy(activeGifts = game.activeGifts + caughtGifts, giftsOnScreen = uncaughtGifts)
}

fun clearGiftsCaught(gifts: List<Gift>, racket: Racket) = gifts.filter { !it.isCollidingWithRacket(racket) }
fun updateGiftsIconMovement(gifts: List<Gift>) = gifts.map { it.copy(y = it.y + it.deltaY) }.filter { !it.isOutOfBounds() }

fun Game.handleActiveGifts(): Game {
    var newUpdatedGame = this

    val gifts = this.activeGifts.map {
        if(!it.active){
            newUpdatedGame = chooseGiftAction(it, newUpdatedGame)
            println("action fired $it")
            it.copy(active = true)
        }else it
    }
    val finishedGifts = gifts.filter { it.type.useCount != 0 }
    return newUpdatedGame.copy(activeGifts = finishedGifts)
}

/*
* If there are not bricks, other than INDESTRUCTIBLE, left than the game ends
* */
fun Game.isGameOver() = (
            this.bricks.filter { it.type != BrickType.GOLD }.isEmpty()
        ) || (this.balls.isEmpty() && this.lives == 0)

/*
* Desenha no canvas o número de pontos adquiridos durante o jogo no momento*/
fun drawGamePoints(points: Int) {
    arena.drawText(
        x = WIDTH / 2,
        y = BALL_COUNTER_YCORD,
        txt = points.toString(),
        color = WHITE,
        fontSize = BALL_COUNT_FONTSIZE
    )
}

/*
* Desenha o jogo no canvas, que inclui desenhar a raquete, bolas e o contador de bolas em jogo
* */
fun drawGame(game: Game) {

    val glueCounter = game.activeGifts
        .filter { it.type == GiftType.GLUE }
        .fold(0) { sum, elem -> sum + elem.useCount}

    drawRacket(racket = game.racket, glueCounter)
    drawBalls(ballsList = game.balls)
    drawGamePoints(points = game.points)
    drawBricks(bricks = game.bricks)
    drawLives(lives = game.lives)
    drawActiveGifts(activeGifts = game.giftsOnScreen)
}

fun drawActiveGifts(activeGifts: List<Gift>) {
    activeGifts.forEach { it.drawGift() }
}

fun drawLives(lives: Int) {
    drawBalls(generateLives(lives))
}

fun drawFinishText() {
    arena.drawText(WIDTH - 100, LIVES_Y_POSITION, "FINISHED!", YELLOW, 16)
}
