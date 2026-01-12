package org.example.views

import org.example.models.*
import pt.isel.canvas.ESCAPE_CODE
import pt.isel.canvas.WHITE
import pt.isel.canvas.YELLOW


fun gameStart() {
    val racket = Racket()
    var game = Game(
        racket = racket,
        bricks = createInitialBricksLayout(gameLevels.first()),
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
        if (me.down && game.lives > 0) {
            if (game.balls.isEmpty()) {
                game = game.newBall()
                game = game.loseLife()
            } else {
                if (game.isGameOver() && game.level < gameLevels.size) {
                    game = game.newLevel()
                } else {
                    game = unstuckBalls(game)
                }
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
        .fold(0) { sum, elem -> sum + elem.useCount }

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
