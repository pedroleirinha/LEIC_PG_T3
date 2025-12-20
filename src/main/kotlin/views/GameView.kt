package org.example.views

import org.example.models.*
import pt.isel.canvas.ESCAPE_CODE
import pt.isel.canvas.WHITE
import pt.isel.canvas.YELLOW


fun gameStart() {
    val racket = Racket()
    var game = Game(balls = listOf(generateNewBall(racket)))

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
        if (it.code == ESCAPE_CODE) arena.close();
    }
}


fun Game.progressGame(): Game {
    val bricks = clearBrokenBricks(this.bricks, this.balls)
    val points = sumPoints(bricks)
    val updatedBalls = handleGameBallsBehaviour(balls = this.balls, racket = this.racket, bricks = this.bricks)

    val filteredBricks = bricks.filter { !it.isBroken() }

    return copy(bricks = filteredBricks, balls = updatedBalls, points = this.points + points)
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
    drawRacket(racket = game.racket)
    drawBalls(ballsList = game.balls)
    drawGamePoints(game.points)
    drawBricks(bricks = game.bricks)
    drawLives(lives = game.lives)
}

fun drawLives(lives: Int) {
    drawBalls(generateLives(lives))
}

fun drawFinishText() {
    arena.drawText(WIDTH - 100, LIVES_Y_POSITION, "FINISHED!", YELLOW, 16)
}
