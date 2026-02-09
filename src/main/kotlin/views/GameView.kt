package org.example.views

import org.example.models.*
import pt.isel.canvas.ESCAPE_CODE
import pt.isel.canvas.RED
import pt.isel.canvas.WHITE
import pt.isel.canvas.YELLOW
import pt.isel.canvas.loadSounds
import pt.isel.canvas.playSound


fun gameStart() {

    var game = Game()
    var flagSound: Boolean = false
    //loadSounds("click","Arkanoid SFX (8)","GameOver")

    arena.onTimeProgress(period = TIME_TICK_MLS) {

        if (!game.isGameOver()){
            arena.erase()
            game = game.progressGame()
            drawGame(game)
        }
        else {
            drawFinishText()

            if (!flagSound) {
                 playSound("GameOver")
                 flagSound = true
            }
        }

    }

    arena.onMouseMove { me ->
        game = adjustHorizontalCordForStuckBall(game, mouseX = me.x)
    }

    arena.onMouseDown { me ->
        if (me.down ) {
            if (game.balls.isEmpty() && game.lives > 0) {
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
            println("fast balls ${game.balls.first().mass}")
        }

        if (it.code == KEY_S_CODE) {
            game = game.copy(balls = giftSlowBalls(game.balls))
            println("slow balls ${game.balls.first().mass}")
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
            game = giftCancelEffects(game)
            println("cancel")
        }

        if (it.code == KEY_G_CODE) {
            game = game.copy(showGiftsOnBricks = !game.showGiftsOnBricks)
            println("Gifts visiveis")
        }

        if (it.code == KEY_H_CODE) {

            game = game.copy(balls = game.balls + Ball(radius = 3, color = RED,  x = game.racket.x.toDouble(), y = game.racket.y.toDouble(), deltaX = 0, deltaY = -4, stuck = false, flagBigBall = true))
            println("Gifts visiveis")
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
    drawBricks(bricks = game.bricks, showGift = game.showGiftsOnBricks)
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
